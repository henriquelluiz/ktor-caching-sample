package me.henriquelluiz.routes

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import kotlinx.html.*
import me.henriquelluiz.repositories.TaskRepository
import me.henriquelluiz.routes.templates.FormComponent
import me.henriquelluiz.routes.templates.TLayout
import me.henriquelluiz.routes.templates.TableComponent
import org.bson.types.ObjectId
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
                    +"YOUR TASKS"
                }
                button {
                    div {
                        a {
                            classes = setOf("button", "is-secondary", "is-medium")
                            href = "/add"
                            span {
                                classes = setOf("icon")
                                i {
                                    classes = setOf("material-symbols-outlined")
                                    +"add_task"
                                }
                            }
                            span { +"New task" }
                        }
                    }
                }
            }
        }

        get("/add") {
            call.respondHtmlTemplate(
                TLayout(
                    stylePath = "/static/css/style.css",
                    scriptPath = "/static/js/script.js",
                    pageTitle = "New Task",
                    childTemplate = FormComponent(null, "addTask()"),
                )
            ) {
                header {
                    +"ADD TASK"
                }
                button {
                    div {
                        a {
                            classes = setOf("button", "is-secondary", "is-medium")
                            href = "/"
                            span {
                                classes = setOf("icon")
                                i {
                                    classes = setOf("material-symbols-outlined")
                                    +"arrow_back"
                                }
                            }
                            span { +"Back" }
                        }
                    }
                }
            }
        }

        get("/edit{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondHtml {
                body {
                    h1 {
                        +"ID NOT FOUND"
                    }
                }
            }

            val task = repository.getById(ObjectId(id)) ?: return@get call.respondHtml {
                body {
                    h1 {
                        +"TASK NOT FOUND"
                    }
                }
            }

            call.respondHtmlTemplate(
                TLayout(
                    stylePath = "/static/css/style.css",
                    scriptPath = "/static/js/script.js",
                    pageTitle = "Edit Task",
                    childTemplate = FormComponent(task, "editTask('${id}')"),
                )
            ) {
                header {
                    +"EDIT TASK"
                }
                button {
                    div {
                        a {
                            classes = setOf("button", "is-secondary", "is-medium")
                            href = "/"
                            span {
                                classes = setOf("icon")
                                i {
                                    classes = setOf("material-symbols-outlined")
                                    +"arrow_back"
                                }
                            }
                            span { +"Back" }
                        }
                    }
                }
            }
        }

        get("/delete{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondHtml {
                body {
                    h1 {
                        +"ID NOT FOUND"
                    }
                }
            }

            val task = repository.getById(ObjectId(id)) ?: return@get call.respondHtml {
                body {
                    h1 {
                        +"TASK NOT FOUND"
                    }
                }
            }

            call.respondHtmlTemplate(
                TLayout(
                    stylePath = "/static/css/style.css",
                    scriptPath = "/static/js/script.js",
                    pageTitle = "Edit Task",
                    childTemplate = FormComponent(task, "deleteTask('${id}')"),
                )
            ) {
                header {
                    +"DELETE TASK"
                }
                button {
                    div {
                        a {
                            classes = setOf("button", "is-secondary", "is-medium")
                            href = "/"
                            span {
                                classes = setOf("icon")
                                i {
                                    classes = setOf("material-symbols-outlined")
                                    +"arrow_back"
                                }
                            }
                            span { +"Back" }
                        }
                    }
                }
            }
        }
    }
}