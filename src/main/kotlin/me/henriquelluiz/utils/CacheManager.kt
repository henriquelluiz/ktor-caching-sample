package me.henriquelluiz.utils

import io.github.reactivecircus.cache4k.Cache
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import kotlin.time.Duration.Companion.hours

class CacheManager {
    private val storage = Cache.Builder<String, String>()
        .expireAfterWrite(1.hours)
        .build()

    suspend fun <T> handleReadOperations(
        call: ApplicationCall,
        key: String,
        serializer: KSerializer<T>,
        getData: suspend () -> T?
    ) {
        val cachedData = storage.get(key)
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
            val data = getData() ?: throw RuntimeException("Task not found")
            val encodedData = Json.encodeToString(serializer, data)
            storage.put(key, encodedData)
            call.respondText(
                status = HttpStatusCode.OK,
                text = encodedData,
                contentType = ContentType.Application.Json
            )
        }
    }

    suspend fun <T> handleWriteOperations(
        call: ApplicationCall,
        statusCode: HttpStatusCode,
        key: String,
        serializer: KSerializer<T>,
        getCurrentData: suspend () -> T?
    ) {
        val data = getCurrentData() ?: return call.respond(HttpStatusCode.NotFound)
        val encodedData = Json.encodeToString(serializer, data)
        storage.put(key, encodedData)
        call.respond(statusCode)
    }

    fun <T> updateSingleData(key: String, serializer: KSerializer<T>, data: T) {
        val encodedData = Json.encodeToString(serializer, data)
        storage.put(key, encodedData)
    }

    fun invalidateData(key: String) = storage.invalidate(key)

    private fun generateETag(content: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(content.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}