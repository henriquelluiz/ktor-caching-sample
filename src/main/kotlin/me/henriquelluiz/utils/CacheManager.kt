package me.henriquelluiz.utils

import io.github.reactivecircus.cache4k.Cache
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.security.MessageDigest

class CacheManager {
    private val store = Cache.Builder<String, String>().build()

    suspend fun <T> handleCache(
        call: ApplicationCall,
        key: String,
        serializer: KSerializer<T>,
        getData: suspend () -> T?
    ) {
        val cachedData = store.get(key)
        if (cachedData != null) {
            val eTag = generateETag(cachedData)
            val clientETag = call.request.headers[HttpHeaders.IfNoneMatch]
            call.response.header(HttpHeaders.ETag, eTag)

            if (clientETag == eTag) {
                call.respond(HttpStatusCode.NotModified)
            } else {
                call.respondText(
                    status = HttpStatusCode.OK,
                    text = cachedData,
                    contentType = ContentType.Application.Json
                )
            }
        } else {
            val data = getData() ?: return call.respond(HttpStatusCode.NotFound)
            val encodedData = Json.encodeToString(serializer, data)
            store.put(key, encodedData)
            call.respondText(
                status = HttpStatusCode.OK,
                text = encodedData,
                contentType = ContentType.Application.Json
            )
        }
    }

    private fun generateETag(content: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(content.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}