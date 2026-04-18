package com.example.skreenup.update

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

@Serializable
data class GitHubRelease(
    val tag_name: String,
    val html_url: String,
    val body: String? = null
)

class UpdateChecker {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val releaseUrl = "https://api.github.com/repos/Pankaj-Meharchandani/Skreenup/releases/latest"

    suspend fun checkForUpdate(): GitHubRelease? {
        val request = Request.Builder()
            .url(releaseUrl)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string() ?: return null
                json.decodeFromString<GitHubRelease>(body)
            }
        } catch (e: IOException) {
            null
        }
    }
}
