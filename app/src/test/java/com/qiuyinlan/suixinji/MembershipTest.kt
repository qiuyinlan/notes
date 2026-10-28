package com.qiuyinlan.suixinji

import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class MembershipTest {
    private class MemoryTokens : MemberTokenStore {
        var value: String? = null
        override fun read() = value
        override fun write(token: String) { value = token }
        override fun clear() { value = null }
    }

    private class FakeTransport : MemberTransport {
        val requests = mutableListOf<Triple<String, String, JSONObject?>>()
        val replies = ArrayDeque<MemberResponse>()
        var failWith: Exception? = null
        var receivedToken: String? = null
        override suspend fun execute(url: String, method: String, body: JSONObject?, token: String?): MemberResponse {
            requests += Triple(url, method, body)
            receivedToken = token
            failWith?.let { throw it }
            return replies.removeFirst()
        }
    }

    private fun api(transport: FakeTransport, tokens: MemoryTokens = MemoryTokens()) = MemberApi(
        "https://members.example", tokens, transport,
    ) to tokens

    @Test fun sendsCodeAndLogsInWithoutSendingCredentialsInUrl() = runBlocking {
        val transport = FakeTransport().apply {
            replies += MemberResponse(202, JSONObject("""{"message":"验证码已发送"}"""))
            replies += MemberResponse(200, JSONObject("""{"access_token":"secret","token_type":"Bearer","expires_in":2592000,"user":{"id":"u1","email":"a@example.com"}}"""))
        }
        val (api, tokens) = api(transport)
        api.sendCode("a@example.com")
        val account = api.verifyCode("a@example.com", "123456")
        assertEquals("u1", account.id)
        assertEquals("secret", tokens.value)
        assertEquals("/v1/auth/email/send-code", transport.requests[0].first.removePrefix("https://members.example"))
        assertEquals("a@example.com", transport.requests[0].third?.getString("email"))
        assertEquals("123456", transport.requests[1].third?.getString("code"))
        assertFalse(transport.requests.any { it.first.contains("123456") || it.first.contains("secret") })
    }

    @Test fun anonymousSessionDoesNotMakeARequest() = runBlocking {
        val transport = FakeTransport()
        val (api, _) = api(transport)
        assertNull(api.loadSession())
        assertTrue(transport.requests.isEmpty())
    }

    @Test fun serverControlsActiveAndInactiveEntitlements() = runBlocking {
        for ((active, status) in listOf(true to "active", false to "inactive")) {
            val transport = FakeTransport().apply {
                replies += MemberResponse(200, JSONObject("""{"id":"u1","email":"a@example.com"}"""))
                replies += MemberResponse(200, JSONObject("""{"entitlement":{"product_id":"annual","source":"manual_code","status":"$status","active":$active,"valid_until":null}}"""))
            }
            val (api, tokens) = api(transport).also { it.second.value = "token" }
            val session = api.loadSession()
            assertEquals(active, session?.entitlement?.active)
            assertEquals(status, session?.entitlement?.status)
            assertEquals("Bearer token", transport.receivedToken?.let { "Bearer $it" })
        }
    }

    @Test fun redeemReturnsServerEntitlementAndSurfacesFailure() = runBlocking {
        val transport = FakeTransport().apply {
            replies += MemberResponse(200, JSONObject("""{"message":"兑换成功","entitlement":{"active":true,"status":"active","valid_until":null}}"""))
            replies += MemberResponse(400, JSONObject("""{"error":{"code":"invalid_code","message":"兑换码无效"}}"""))
        }
        val (api, tokens) = api(transport).also { it.second.value = "token" }
        assertTrue(api.redeem("ABCD").active)
        try { api.redeem("BAD"); fail("expected API error") }
        catch (error: MemberApiException) { assertEquals("兑换码无效", error.message) }
        assertEquals("ABCD", transport.requests.first().third?.getString("code"))
        assertEquals("token", transport.receivedToken)
    }

    @Test fun logoutClearsTokenEvenWhenNetworkFails() = runBlocking {
        val transport = FakeTransport().apply { failWith = IOException("offline") }
        val (api, tokens) = api(transport).also { it.second.value = "token" }
        try { api.logout(); fail("expected network error") } catch (_: IOException) { }
        assertNull(tokens.value)
    }

    @Test fun successfulLogoutSendsBearerTokenAndClearsLocalSession() = runBlocking {
        val transport = FakeTransport().apply { replies += MemberResponse(200, JSONObject()) }
        val (api, tokens) = api(transport).also { it.second.value = "token" }
        api.logout()
        assertEquals("token", transport.receivedToken)
        assertEquals("/v1/auth/logout", transport.requests.single().first.removePrefix("https://members.example"))
        assertNull(tokens.value)
    }

    @Test fun unauthorizedClearsTokenAndRateLimitHasRetryMessage() = runBlocking {
        val transport = FakeTransport().apply {
            replies += MemberResponse(401, JSONObject("""{"error":{"code":"unauthorized","message":"expired"}}"""))
            replies += MemberResponse(429, JSONObject())
        }
        val (api, tokens) = api(transport).also { it.second.value = "expired-token" }
        try { api.loadSession(); fail("expected unauthorized") } catch (error: MemberApiException) { assertEquals(401, error.status) }
        assertNull(tokens.value)
        try { api.sendCode("a@example.com"); fail("expected rate limit") } catch (error: MemberApiException) {
            assertEquals(429, error.status)
            assertTrue(error.message.contains("稍后"))
        }
    }

    @Test fun networkFailureIsPropagatedForRetryUi() = runBlocking {
        val transport = FakeTransport().apply { failWith = IOException("offline") }
        val (api, _) = api(transport)
        try { api.sendCode("a@example.com"); fail("expected network error") }
        catch (error: IOException) { assertEquals("offline", error.message) }
    }
}
