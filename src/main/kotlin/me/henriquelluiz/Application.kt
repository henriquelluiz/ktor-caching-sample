package me.henriquelluiz

import io.ktor.server.application.*
import me.henriquelluiz.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
}
