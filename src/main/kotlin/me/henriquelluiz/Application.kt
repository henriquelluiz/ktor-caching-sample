package me.henriquelluiz

import io.ktor.server.application.*
import io.ktor.server.netty.*
import me.henriquelluiz.plugins.configureDI
import me.henriquelluiz.routes.configureTaskRoutes

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureDI()
    configureTaskRoutes()
}
