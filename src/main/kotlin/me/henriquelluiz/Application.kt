package me.henriquelluiz

import io.ktor.server.application.*
import io.ktor.server.netty.*
import me.henriquelluiz.plugins.configureDI
import me.henriquelluiz.plugins.configureHTTP
import me.henriquelluiz.routes.configureTaskRoutes
import me.henriquelluiz.routes.configureTaskUiRouting

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureHTTP()
    configureDI()
    configureTaskRoutes()
    configureTaskUiRouting()
}
