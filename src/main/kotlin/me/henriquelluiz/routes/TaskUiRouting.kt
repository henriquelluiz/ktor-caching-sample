package me.henriquelluiz.routes

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import me.henriquelluiz.repositories.TaskRepository
import me.henriquelluiz.routes.templates.BaseLayout
import me.henriquelluiz.utils.CacheManager
import org.koin.ktor.ext.inject

fun Application.configureTaskUiRouting() {
    val repository by inject<TaskRepository>()
    val cache by inject<CacheManager>()

    routing {
        staticResources("static", "static")

        get {
            call.respondHtmlTemplate(
                BaseLayout(
                    stylePath = "/static/css/style.css",
                    scriptPath = "/static/js/script.js",
                    documentTitle = "Home"
                )
            ) {
                header {
                    +"Home Page"
                }
            }
        }
    }
}