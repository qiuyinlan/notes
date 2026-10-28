package com.qiuyinlan.suixinji

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class AppUpdateTest {
    @Test fun setsHeadersBeforeConnectingIncludingRedirects() {
        val connections = mutableListOf<java.net.HttpURLConnection>()
        val result = AppUpdateClient.openHttpsConnection(
            "https://updates.example/stable.json", "GET", 1000, 1000, "application/json",
        ) { url ->
            object : java.net.HttpURLConnection(url) {
                override fun connect() { connected = true }
                override fun disconnect() {}
                override fun usingProxy() = false
                override fun getResponseCode(): Int {
                    assertEquals("application/json", getRequestProperty("Accept"))
                    assertEquals("no-cache, no-store", getRequestProperty("Cache-Control"))
                    assertEquals("SuiXinJi-Android-Updater", getRequestProperty("User-Agent"))
                    assertTrue(instanceFollowRedirects)
                    connect()
                    return if (connections.size == 1) 302 else 200
                }
                override fun getHeaderField(name: String?): String? =
                    if (name == "Location") "https://cdn.example/stable.json" else null
            }.also { connections.add(it) }
        }
        assertEquals(2, connections.size)
        assertEquals("https://cdn.example/stable.json", result.url.toString())
        assertSame(connections.last(), result)
    }

    @Test fun usesSeparateFallbackForEachUpdateChannel() {
        assertEquals("https://raw.githubusercontent.com/qiuyinlan/notes/main/updates/stable.json",
            updateManifestFallbackUrl("https://github.com/qiuyinlan/notes/releases/latest/download/stable.json"))
        assertEquals("https://raw.githubusercontent.com/qiuyinlan/notes/main/updates/latest.json",
            updateManifestFallbackUrl("https://github.com/qiuyinlan/notes/releases/latest/download/latest.json"))
        assertEquals("https://cdn.jsdelivr.net/gh/qiuyinlan/notes@main/updates/stable.json",
            updateManifestMirrorUrl("https://github.com/qiuyinlan/notes/releases/latest/download/stable.json"))
        assertEquals("https://cdn.jsdelivr.net/gh/qiuyinlan/notes@main/updates/latest.json",
            updateManifestMirrorUrl("https://github.com/qiuyinlan/notes/releases/latest/download/latest.json"))
    }

    @Test fun doesNotOverrideCustomOrUntrustedUpdateSources() {
        assertNull(updateManifestFallbackUrl("https://updates.example/stable.json"))
        assertNull(updateManifestFallbackUrl("http://github.com/qiuyinlan/notes/releases/latest/download/stable.json"))
        assertNull(updateManifestFallbackUrl("https://github.com.evil.example/qiuyinlan/notes/releases/latest/download/stable.json"))
        assertNull(updateManifestFallbackUrl("invalid url"))
    }

    private val valid = """{"versionCode":32,"versionName":"0.1.4","apkUrl":"https://updates.example/app.apk","sha256":"${"a".repeat(64)}","changelog":"修复问题"}"""

    @Test fun parsesNewHttpsUpdate() {
        val update = AppUpdate.parse(JSONObject(valid), 31)
        assertNotNull(update)
        assertEquals(32L, update?.versionCode)
        assertEquals("0.1.4", update?.versionName)
        assertEquals("https://updates.example/app.apk", update?.apkUrl)
    }

    @Test fun ignoresCurrentOrOlderVersions() {
        assertNull(AppUpdate.parse(JSONObject(valid), 32))
        val older = JSONObject(valid).put("versionCode", 30)
        assertNull(AppUpdate.parse(older, 31))
        assertNull(AppUpdate.parse(JSONObject("""{"versionCode":36,"versionName":"0.1.8"}"""), 36))
    }

    @Test fun rejectsInsecureOrInvalidMetadata() {
        assertNull(AppUpdate.parse(JSONObject(valid).put("apkUrl", "http://updates.example/app.apk"), 31))
        assertNull(AppUpdate.parse(JSONObject(valid).put("sha256", "bad"), 31))
        assertNull(AppUpdate.parse(JSONObject(valid).put("apkUrl", "https:///missing-host"), 31))
    }

    @Test fun rejectsUpdateWhenAnySignerIsUntrusted() {
        val trustedDigest = java.security.MessageDigest.getInstance("SHA-256").digest(byteArrayOf(1))
        val trusted = candidateSignersTrusted(listOf(byteArrayOf(1))) { actual -> actual.contentEquals(trustedDigest) }
        val mixedSigners = candidateSignersTrusted(listOf(byteArrayOf(1), byteArrayOf(2))) { actual -> actual.contentEquals(trustedDigest) }
        assertTrue(trusted)
        assertFalse(mixedSigners)
        assertFalse(candidateSignersTrusted(emptyList()) { true })
    }
}
