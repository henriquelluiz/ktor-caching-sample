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
                }
            }
            tbody {
                tasks.forEach { task ->
                    tr {
                        td { +task.id.toString() }
                        td { +task.name }
                        td { +task.note }
                        td { +task.createdAt.toString() }
                    }
                }
            }
        }
    }
}