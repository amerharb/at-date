package com.amerharb.atdate

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

const val PORT = 8000

fun main() {
    embeddedServer(Netty, port = PORT, module = Application::module).start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }

        post("/encode/{notation}") {
            val notation = call.parameters["notation"]
            if (notation == null) {
                call.respondText("Notation is null")
                return@post
            }
            // if encoding throw error respond with error
            try {
                val ad = encode(notation)

                val res = """
                    hex: ${ad.getHex()}
                    base64: ${ad.getBase64()}
            """.trimIndent()
                call.respondText(res)
            } catch (e: Exception) {
                call.respondText(
                    text = e.message ?: "Error",
                    contentType = ContentType.Text.Plain,
                    status = HttpStatusCode.InternalServerError,
                )
                return@post
            }
        }

        post("/encode/{notation}/hex") {
            val notation = call.parameters["notation"]
            if (notation == null) {
                call.respondText("Notation is null")
                return@post
            }
            val ad = encode(notation)
            call.respondText(ad.getHex())
        }

        post("/encode/{notation}/base64") {
            val notation = call.parameters["notation"]
            if (notation == null) {
                call.respondText("Notation is null")
                return@post
            }
            val ad = encode(notation)
            call.respondText(ad.getBase64())
        }

        post("/decode/hex/{hex}") {
            val hex = call.parameters["hex"]
            if (hex == null) {
                call.respondText("Hex is null")
                return@post
            }
            val bArray = getUByteArrayFromHex(hex)
            val ad = decode(bArray)
            call.respondText(ad.getNotation())
        }

        post("/decode/base64/{base64}") {
            val base64 = call.parameters["base64"]
            if (base64 == null) {
                call.respondText("Base64 is null")
                return@post
            }
            val bArray = getUByteArrayFromBase64(base64)
            val ad = decode(bArray)
            call.respondText(ad.getNotation())
        }
    }
}

fun getUByteArrayFromHex(hex: String): Array<UByte> {
    val byteList = hex
        .drop(2)
        .chunked(2)
        .map { it.toInt(16).toUByte() }
    return byteList.toTypedArray()
}

fun getUByteArrayFromBase64(base64: String): Array<UByte> {
    val byteList = Base64.getDecoder().decode(base64)
        .map { it.toUByte() }
    return byteList.toTypedArray()
}

fun AtDate.getHex(): String {
    val hexList = this.getPayload().map { it.toString(16).padStart(2, '0') }
    return "0x${hexList.joinToString("")}"
}

fun AtDate.getBase64(): String {
    val byteList: List<Byte> = this.getPayload().map { it.toByte() }
    val byteArr: ByteArray = byteList.toByteArray()
    return Base64.getEncoder().encodeToString(byteArr)
}
