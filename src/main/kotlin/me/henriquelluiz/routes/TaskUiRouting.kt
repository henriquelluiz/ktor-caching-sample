package me.henriquelluiz.routes

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import me.henriquelluiz.repositories.TaskRepository
import me.henriquelluiz.routes.templates.TLayout
import me.henriquelluiz.routes.templates.TableComponent
import org.koin.ktor.ext.inject

fun Application.configureTaskUiRouting() {
    val repository by inject<TaskRepository>()

    routing {
        staticResources("static", "static")

        get {
            call.respondHtmlTemplate(
                TLayout(
                    stylePath = "/static/css/style.css",
                    scriptPath = "/static/js/script.js",
                    pageTitle = "List of Tasks",
                    childTemplate = TableComponent(repository.getAll())
                )
            ) {
                header {
                    +"All Tasks"
                }
            }
        }
    }
}