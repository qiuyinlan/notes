package com.qiuyinlan.suixinji

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

@Composable
internal fun MembershipPage(onBack: () -> Unit) {
    val context = LocalContext.current
    val api = remember { MemberApi(tokens = KeystoreMemberTokenStore(context)) }
    var email by rememberSaveable { mutableStateOf("") }
    var code by rememberSaveable { mutableStateOf("") }
    var redeemCode by rememberSaveable { mutableStateOf("") }
    var session by remember { mutableStateOf<MemberSession?>(null) }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var codeSent by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun runRequest(action: suspend () -> Unit) {
        if (loading) return
        loading = true
        message = null
        scope.launch {
            try { action() }
            catch (error: MemberApiException) {
                if (error.status == 401) { session = null; message = "登录已失效，请重新登录" }
                else message = error.message
            } catch (_: Exception) { message = "网络连接失败，请检查网络后重试" }
            finally { loading = false }
        }
    }

    LaunchedEffect(Unit) {
        runRequest { session = api.loadSession() }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("会员中心", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = onBack) { Text("返回") }
        }
        Text("会员资格由服务端确认", style = MaterialTheme.typography.bodyMedium)
        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        session?.let { current ->
            Text(current.account.email, style = MaterialTheme.typography.titleMedium)
            val entitlement = current.entitlement
            Text(when {
                entitlement == null -> "尚未查询会员状态"
                entitlement.active && entitlement.validUntil == null -> "会员有效 · 永久"
                entitlement.active -> "会员有效 · 截止 ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date((entitlement.validUntil ?: 0L) * 1000))}"
                else -> "会员未开通或已过期"
            }, style = MaterialTheme.typography.bodyLarge)
            OutlinedButton(onClick = { runRequest { session = api.loadSession(); message = "会员状态已更新" } }, modifier = Modifier.fillMaxWidth(), enabled = !loading) { Text("刷新会员状态") }
            OutlinedTextField(redeemCode, { redeemCode = it }, modifier = Modifier.fillMaxWidth(), label = { Text("人工发放的兑换码") }, singleLine = true)
            Button(onClick = { runRequest { val entitlement = api.redeem(redeemCode.trim()); session = session?.copy(entitlement = entitlement); redeemCode = ""; message = if (entitlement.active) "兑换成功，会员已开通" else "兑换完成，但会员当前未生效" } }, modifier = Modifier.fillMaxWidth(), enabled = !loading && redeemCode.isNotBlank()) { Text("核销兑换码") }
            TextButton(onClick = { runRequest { api.logout(); session = null; codeSent = false; code = ""; message = "已退出登录" } }, enabled = !loading) { Text("退出账号") }
        } ?: run {
            TextButton(onClick = { runRequest { session = api.loadSession(); if (session == null) message = "当前未登录，请使用邮箱验证码登录" } }, enabled = !loading) { Text("重试查询登录状态") }
            OutlinedTextField(email, { email = it.trim() }, modifier = Modifier.fillMaxWidth(), label = { Text("邮箱") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true)
            Button(onClick = { runRequest { api.sendCode(email); codeSent = true; message = "验证码已发送" } }, modifier = Modifier.fillMaxWidth(), enabled = !loading && email.contains('@')) { Text("发送验证码") }
            if (codeSent) {
                OutlinedTextField(code, { code = it.filter(Char::isDigit).take(6) }, modifier = Modifier.fillMaxWidth(), label = { Text("6 位验证码") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                Button(onClick = { runRequest { api.verifyCode(email, code); session = api.loadSession(); code = "" } }, modifier = Modifier.fillMaxWidth(), enabled = !loading && code.length == 6) { Text("验证并登录") }
            }
        }
        message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

internal data class MemberAccount(val id: String, val email: String)
internal data class MemberEntitlement(val active: Boolean, val status: String, val validUntil: Long?)
internal data class MemberSession(val account: MemberAccount, val entitlement: MemberEntitlement?)

internal interface MemberTokenStore {
    fun read(): String?
    fun write(token: String)
    fun clear()
}

internal data class MemberResponse(val status: Int, val body: JSONObject)
internal fun interface MemberTransport {
    suspend fun execute(url: String, method: String, body: JSONObject?, token: String?): MemberResponse
}

private object HttpMemberTransport : MemberTransport {
    override suspend fun execute(url: String, method: String, body: JSONObject?, token: String?): MemberResponse {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 10_000
            readTimeout = 15_000
            setRequestProperty("Accept", "application/json")
            if (body != null) setRequestProperty("Content-Type", "application/json")
            token?.let { setRequestProperty("Authorization", "Bearer $it") }
            if (body != null) doOutput = true
        }
        try {
            body?.let { connection.outputStream.bufferedWriter(Charsets.UTF_8).use { writer -> writer.write(it.toString()) } }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val payload = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            return MemberResponse(status, if (payload.isBlank()) JSONObject() else JSONObject(payload))
        } finally { connection.disconnect() }
    }
}

internal class KeystoreMemberTokenStore(context: Context) : MemberTokenStore {
    private val prefs = context.getSharedPreferences("membership_credentials", Context.MODE_PRIVATE)
    private val keyAlias = "suixinji_membership_token"

    override fun read(): String? = runCatching {
        val iv = prefs.getString("iv", null) ?: return null
        val ciphertext = prefs.getString("token", null) ?: return null
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, Base64.getDecoder().decode(iv)))
        String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), Charsets.UTF_8)
    }.getOrNull()

    override fun write(token: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val encrypted = Base64.getEncoder().encodeToString(cipher.doFinal(token.toByteArray(Charsets.UTF_8)))
        prefs.edit().putString("iv", Base64.getEncoder().encodeToString(cipher.iv)).putString("token", encrypted).apply()
    }

    override fun clear() { prefs.edit().clear().apply() }

    private fun key(): java.security.Key = runCatching {
        val store = java.security.KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        store.getKey(keyAlias, null) as? java.security.Key
    }.getOrNull() ?: KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").run {
        init(KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        generateKey()
    }
}

internal class MemberApi(
    private val baseUrl: String = BuildConfig.MEMBERSHIP_API_BASE_URL,
    private val tokens: MemberTokenStore,
    private val transport: MemberTransport = HttpMemberTransport,
) {
    init { require(baseUrl.startsWith("https://")) { "会员 API 地址必须使用 HTTPS" } }

    suspend fun sendCode(email: String) = request("POST", "/v1/auth/email/send-code", JSONObject().put("email", email), authenticated = false)

    suspend fun verifyCode(email: String, code: String): MemberAccount {
        val response = request("POST", "/v1/auth/email/verify-code", JSONObject().put("email", email).put("code", code), authenticated = false)
        val token = response.getString("access_token")
        tokens.write(token)
        return response.getJSONObject("user").let { MemberAccount(it.getString("id"), it.getString("email")) }
    }

    suspend fun loadSession(): MemberSession? {
        if (tokens.read() == null) return null
        return try {
            val user = request("GET", "/v1/me").let { MemberAccount(it.getString("id"), it.getString("email")) }
            val entitlement = request("GET", "/v1/entitlements").getJSONObject("entitlement").let {
                MemberEntitlement(it.optBoolean("active"), it.optString("status"), if (it.isNull("valid_until")) null else it.optLong("valid_until"))
            }
            MemberSession(user, entitlement)
        } catch (error: MemberApiException) {
            if (error.status == 401) tokens.clear()
            throw error
        }
    }

    suspend fun redeem(code: String): MemberEntitlement {
        val entitlement = request("POST", "/v1/membership/redeem", JSONObject().put("code", code)).getJSONObject("entitlement")
        return MemberEntitlement(entitlement.optBoolean("active"), entitlement.optString("status"), if (entitlement.isNull("valid_until")) null else entitlement.optLong("valid_until"))
    }

    suspend fun logout() {
        try { request("POST", "/v1/auth/logout", JSONObject()) } finally { tokens.clear() }
    }

    private suspend fun request(method: String, path: String, body: JSONObject? = null, authenticated: Boolean = true): JSONObject = withContext(Dispatchers.IO) {
        val response = transport.execute(baseUrl.trimEnd('/') + path, method, body, if (authenticated) tokens.read() else null)
        if (response.status !in 200..299) {
            if (response.status == 401) tokens.clear()
            val message = runCatching { response.body.getJSONObject("error").optString("message") }.getOrNull()
            throw MemberApiException(response.status, message?.takeIf(String::isNotBlank) ?: when (response.status) { 401 -> "登录已失效，请重新登录"; 429 -> "操作太频繁，请稍后再试"; else -> "会员服务暂不可用，请重试" })
        }
        response.body
    }
}

internal class MemberApiException(val status: Int, override val message: String) : Exception(message)
