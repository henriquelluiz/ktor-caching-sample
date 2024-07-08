package me.henriquelluiz.routes.templates

import io.ktor.server.html.*
import kotlinx.html.*

class TLayout(
    val stylePath: String,
    val scriptPath: String,
    val pageTitle: String,
    val childTemplate: Template<FlowContent>,
) : Template<HTML> {
    val header = Placeholder<FlowContent>()
    val content = TemplatePlaceholder<Template<FlowContent>>()
    override fun HTML.apply() {
        classes = setOf("theme-light")
        head {
            lang = "en"
            meta { charset = "utf-8" }
            meta {
                name = "viewport"
                content =
                    "width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0"
            }
            meta {
                httpEquiv = "X-UA-Compatible"
                content = "IE=edge"
            }
            link(href = stylePath, rel = "stylesheet", type = "text/css")
            title(pageTitle)
        }
        body {
            h1 {
                classes = setOf("is-size-2", "is-family-code", "has-text-weight-bold", "has-text-centered", "mt-6")
                insert(header)
            }
            div {
                classes = setOf(
                    "container", "is-flex", "container-init-gap",
                    "is-justify-content-center", "is-flex-direction-column",
                    "is-align-content-center", "mt-6"
                )
                div {
                    classes = setOf("")
                    button {
                        classes = setOf("button", "is-primary", "is-medium")
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
                insert(childTemplate, content)
            }
            script {
                src = scriptPath
                type = "text/javascript"
            }
        }
    }
}