package com.amerharb.atdate

import io.ktor.server.application.*
import com.amerharb.atdate.plugins.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.yaml references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureTemplating()
}
