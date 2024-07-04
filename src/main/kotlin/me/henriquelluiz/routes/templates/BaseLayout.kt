package me.henriquelluiz.routes.templates

import io.ktor.server.html.*
import kotlinx.html.*

class BaseLayout(
    val stylePath: String,
    val scriptPath: String,
    val documentTitle: String
) : Template<HTML> {
    val header = Placeholder<FlowContent>()
    override fun HTML.apply() {
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
            link(rel = "stylesheet", href = stylePath)
            script {
                src = "https://cdn.jsdelivr.net/npm/beercss@3.6.5/dist/cdn/beer.min.js"
                type = "module"
            }
            title(documentTitle)
        }
        body {
            h1 { insert(header) }
            script {
                src = scriptPath
                type = "text/javascript"
            }
        }
    }
}