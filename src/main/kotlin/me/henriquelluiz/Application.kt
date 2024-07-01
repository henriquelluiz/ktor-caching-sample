package me.henriquelluiz

import io.ktor.server.application.*
import me.henriquelluiz.plugins.*
import me.henriquelluiz.routes.configureTaskRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureTaskRoutes()
}
