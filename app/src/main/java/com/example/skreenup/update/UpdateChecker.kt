package com.example.skreenup.update

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun checkForUpdate(): GitHubRelease? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(releaseUrl)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: return@withContext null
                json.decodeFromString<GitHubRelease>(body)
            }
        } catch (e: Exception) {
            null
        }
    }
}
