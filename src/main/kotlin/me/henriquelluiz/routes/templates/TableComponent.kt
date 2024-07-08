package me.henriquelluiz.routes.templates

import io.ktor.server.html.*
import kotlinx.html.*
import me.henriquelluiz.models.Task

class TableComponent(val tasks: List<Task>) : Template<FlowContent> {
    override fun FlowContent.apply() {
        table {
            classes = setOf(
                "table", "is-bordered", "is-striped",
                "is-narrow", "is-hoverable", "is-fullwidth"
            )
            thead {
                tr {
                    th { +"ID" }
                    th { +"Name" }
                    th { +"Note" }
                    th { +"Creation Date" }
                    th { +"Actions" }
                }
            }
            tbody {
                tasks.forEach { task ->
                    tr {
                        td { +task.id.toString() }
                        td { +task.name }
                        td { +task.note }
                        td { +task.createdAt.toString() }
                        td {
                            classes = setOf(
                                "is-flex", "is-justify-content-center",
                                "is-align-content-center", "icon-gap",
                                "is-clickable"
                            )
                            a {
                                classes = setOf("icon", "material-symbols-outlined", "has-text-warning")
                                href = "/edit?id=${task.id}"
                                +"edit"
                            }

                            a {
                                classes = setOf("icon", "material-symbols-outlined", "has-text-danger")
                                href = "/delete?id=${task.id}"
                                +"delete"
                            }
                        }
                    }
                }
            }
        }
    }
}