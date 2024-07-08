package me.henriquelluiz.routes.templates

import io.ktor.server.html.*
import kotlinx.html.*
import me.henriquelluiz.models.Task

class FormComponent(
    val task: Task?,
    val inlineStringFunc: String
) : Template<FlowContent> {
    override fun FlowContent.apply() {
        div {
            classes = setOf("is-flex", "is-justify-content-center", "is-flex-direction-column")

            div(classes = "field") {
                label(classes = "label") { +"Name" }
                div(classes = "control") {
                    input(classes = "input") {
                        id = "nameField"
                        type = InputType.text
                        name = "nameField"
                        placeholder = "Name"
                        value = task?.name ?: ""
                    }
                }
            }

            div(classes = "field") {
                label(classes = "label") { +"Note" }
                div(classes = "control") {
                    input(classes = "input") {
                        id = "noteField"
                        type = InputType.text
                        name = "noteField"
                        placeholder = "Note"
                        value = task?.note ?: ""
                    }
                }
            }

            if (task != null) {
                div(classes = "field") {
                    label(classes = "label") { +"Created At" }
                    div(classes = "control") {
                        input(classes = "input") {
                            id = "dateField"
                            disabled = true
                            type = InputType.text
                            name = "dateField"
                            placeholder = "Created At"
                            value = task.createdAt.toString()
                        }
                    }
                }
            }

            button {
                classes = setOf("button", "is-primary", "is-medium")
                onClick = inlineStringFunc
                span {
                    classes = setOf("icon")
                    i {
                        classes = setOf("material-symbols-outlined")
                        +"check"
                    }
                }
                span { +"Confirm" }
            }
        }
    }
}