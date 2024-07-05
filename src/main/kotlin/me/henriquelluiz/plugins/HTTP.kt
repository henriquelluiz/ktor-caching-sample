package me.henriquelluiz.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.resources.*

fun Application.configureHTTP() {
    install(Resources)
    install(CORS) {
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader("If-None-Match")
        allowHeader("If-Modified-Since")
        allowHost("localhost")
    }

    install(CachingHeaders) {
        options { _, content ->
            when (content.contentType?.withoutParameters()) {
                ContentType.Application.Json -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 3600))
                ContentType.Text.Html -> CachingOptions(CacheControl.NoCache(CacheControl.Visibility.Public))
                ContentType.Text.CSS -> CachingOptions(CacheControl.NoCache(CacheControl.Visibility.Public))
                ContentType.Text.JavaScript -> CachingOptions(CacheControl.NoCache(CacheControl.Visibility.Public))
                else -> null
            }
        }
    }
}