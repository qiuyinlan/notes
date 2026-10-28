package com.qiuyinlan.suixinji

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URI
import java.security.MessageDigest

internal data class AppUpdate(
    val versionCode: Long,
    val versionName: String,
    val apkUrl: String,
    val sha256: String,
    val changelog: String,
) {
    companion object {
        fun parse(json: JSONObject, currentVersionCode: Long): AppUpdate? {
            val versionCode = json.optLong("versionCode", -1L)
            if (versionCode <= currentVersionCode) return null
            val versionName = json.optString("versionName").trim()
            val apkUrl = json.optString("apkUrl").trim()
            val sha256 = json.optString("sha256").trim().lowercase()
            val uri = runCatching { URI(apkUrl) }.getOrNull()
            if (versionName.isBlank() ||
                uri?.scheme != "https" || uri.host.isNullOrBlank() || !sha256.matches(Regex("[a-f0-9]{64}"))
            ) return null
            return AppUpdate(versionCode, versionName, apkUrl, sha256, json.optString("changelog"))
        }
    }
}

internal fun candidateSignersTrusted(signers: Iterable<ByteArray>, isTrustedSha256: (ByteArray) -> Boolean): Boolean {
    val candidates = signers.toList()
    return candidates.isNotEmpty() && candidates.all { signer ->
        isTrustedSha256(MessageDigest.getInstance("SHA-256").digest(signer))
    }
}

internal fun updateManifestFallbackUrl(configuredUrl: String): String? {
    val uri = runCatching { URI(configuredUrl) }.getOrNull() ?: return null
    if (uri.scheme != "https" || uri.host != "github.com") return null
    val match = Regex("^/([^/]+)/([^/]+)/releases/latest/download/(stable|latest)\\.json$")
        .matchEntire(uri.path) ?: return null
    val (owner, repository, channel) = match.destructured
    return "https://raw.githubusercontent.com/$owner/$repository/main/updates/$channel.json"
}

internal fun updateManifestMirrorUrl(configuredUrl: String): String? {
    val rawUrl = updateManifestFallbackUrl(configuredUrl) ?: return null
    val match = Regex("^https://raw\\.githubusercontent\\.com/([^/]+)/([^/]+)/main/updates/(stable|latest)\\.json$")
        .matchEntire(rawUrl) ?: return null
    val (owner, repository, channel) = match.destructured
    return "https://cdn.jsdelivr.net/gh/$owner/$repository@main/updates/$channel.json"
}

internal fun updateManifestApiFallbackUrls(configuredUrl: String): List<String> {
    val uri = runCatching { URI(configuredUrl) }.getOrNull() ?: return emptyList()
    if (uri.scheme != "https" || uri.host != "api.github.com") return emptyList()
    val match = Regex("^/repos/([^/]+)/([^/]+)/contents/updates/(stable|latest)\\.json$")
        .matchEntire(uri.path) ?: return emptyList()
    val (owner, repository, channel) = match.destructured
    return listOf(
        "https://raw.githubusercontent.com/$owner/$repository/main/updates/$channel.json",
        "https://cdn.jsdelivr.net/gh/$owner/$repository@main/updates/$channel.json",
    )
}

internal fun updateManifestAcceptHeader(source: String): String {
    val uri = runCatching { URI(source) }.getOrNull()
    return if (uri?.host == "api.github.com" && uri.path.matches(Regex("^/repos/[^/]+/[^/]+/contents/.+\\.json$"))) {
        "application/vnd.github.raw+json"
    } else {
        "application/json"
    }
}

internal object AppUpdateClient {
    suspend fun check(context: Context): AppUpdate? = withContext(Dispatchers.IO) {
        val configuredUrl = BuildConfig.APP_UPDATE_MANIFEST_URL
        val sources = listOfNotNull(configuredUrl, updateManifestFallbackUrl(configuredUrl), updateManifestMirrorUrl(configuredUrl)) +
            updateManifestApiFallbackUrls(configuredUrl)
        val currentVersion = context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
        val sourceErrors = mutableListOf<String>()
        var manifestRead = false
        for (source in sources) {
            try {
                val update = AppUpdate.parse(readManifest(source), currentVersion)
                manifestRead = true
                if (update != null) return@withContext update
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val host = runCatching { URI(source).host }.getOrNull() ?: "更新地址"
                sourceErrors += "$host: ${error.message ?: error.javaClass.simpleName}"
            }
        }
        if (manifestRead) return@withContext null
        throw IOException("无法获取更新清单：${sourceErrors.joinToString("；")}")
    }

    private fun readManifest(source: String): JSONObject {
        val manifestUrl = source + if ('?' in source) "&_=${System.currentTimeMillis()}" else "?_=${System.currentTimeMillis()}"
        val connection = openHttpsConnection(manifestUrl, "GET", 10_000, 15_000, updateManifestAcceptHeader(source))
        try {
            if (connection.responseCode !in 200..299) throw IOException("更新服务返回 HTTP ${connection.responseCode}")
            if (!connection.url.protocol.equals("https", ignoreCase = true)) throw IOException("更新服务跳转到了非 HTTPS 地址")
            val json = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText().removePrefix("\uFEFF") }
            return JSONObject(json)
        } finally { connection.disconnect() }
    }

    suspend fun downloadAndVerify(context: Context, update: AppUpdate): File = withContext(Dispatchers.IO) {
        val directory = File(context.cacheDir, "updates").apply { mkdirs() }
        val apk = File(directory, "app-update-${update.versionCode}.apk")
        val connection = openHttpsConnection(update.apkUrl, "GET", 15_000, 30_000)
        try {
            if (connection.responseCode !in 200..299) throw IOException("更新包下载失败（HTTP ${connection.responseCode}），请重试")
            connection.inputStream.use { input -> apk.outputStream().buffered().use(input::copyTo) }
        } finally { connection.disconnect() }

        val hasher = MessageDigest.getInstance("SHA-256")
        apk.inputStream().buffered().use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                hasher.update(buffer, 0, count)
            }
        }
        val digest = hasher.digest().joinToString("") { "%02x".format(it) }
        try {
            require(digest == update.sha256) { "更新包校验失败，请重新下载" }
            verifyPackageAndSigner(context, apk)
        } catch (error: Exception) {
            apk.delete()
            throw error
        }
        apk
    }

    internal fun openHttpsConnection(
        urlText: String,
        method: String,
        connectTimeout: Int,
        readTimeout: Int,
        accept: String = "*/*",
        connectionFactory: (URL) -> HttpURLConnection = { it.openConnection() as HttpURLConnection },
    ): HttpURLConnection {
        var uri = URI(urlText)
        repeat(6) {
            require(uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank()) { "更新地址必须使用 HTTPS" }
            val connection = connectionFactory(URL(uri.toString())).apply {
                requestMethod = method
                instanceFollowRedirects = true
                useCaches = false
                this.connectTimeout = connectTimeout
                this.readTimeout = readTimeout
                setRequestProperty("Cache-Control", "no-cache, no-store")
                setRequestProperty("Accept", accept)
                setRequestProperty("User-Agent", "SuiXinJi-Android-Updater")
            }
            val code = connection.responseCode
            if (code in listOf(301, 302, 303, 307, 308)) {
                val location = connection.getHeaderField("Location")
                connection.disconnect()
                require(!location.isNullOrBlank()) { "更新服务返回了无效跳转" }
                uri = uri.resolve(location)
            } else return connection
        }
        throw IllegalStateException("更新服务跳转次数过多")
    }

    private fun verifyPackageAndSigner(context: Context, apk: File) {
        val flags = PackageManager.GET_SIGNING_CERTIFICATES
        val candidate = context.packageManager.getPackageArchiveInfo(apk.absolutePath, flags)
            ?: throw IllegalArgumentException("更新文件不是有效的 Android 安装包")
        require(candidate.packageName == context.packageName) { "更新包与当前应用不匹配" }
        val signers = candidate.signingInfo?.apkContentsSigners.orEmpty()
        require(candidateSignersTrusted(signers.map { it.toByteArray() }) { signerDigest ->
            context.packageManager.hasSigningCertificate(
                context.packageName,
                signerDigest,
                PackageManager.CERT_INPUT_SHA256,
            )
        }) { "更新包签名与当前应用不一致，已阻止安装" }
    }

    fun install(context: Context, apk: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apk)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

@Composable
internal fun AppUpdateDialog(
    update: AppUpdate,
    downloading: Boolean,
    downloaded: Boolean,
    message: String?,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    onInstall: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("发现新版本 ${update.versionName}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(update.changelog.ifBlank { "随心记有新版本可用。" })
                if (downloading) LinearProgressIndicator(Modifier.fillMaxWidth())
                message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(onClick = if (downloaded) onInstall else onDownload, enabled = !downloading) {
                Text(when { downloading -> "正在下载"; downloaded -> "安装更新"; else -> "下载更新" })
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("稍后") } },
    )
}
