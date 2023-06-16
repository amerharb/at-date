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

        get("/encode/{notation}") {
            val notation = call.parameters["notation"]
            if (notation.isNullOrBlank()) {
                call.respondText(text = "Notation is empty", status = HttpStatusCode.BadRequest)
                return@get
            }
            try { // if encoding throw error respond with 500
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
                return@get
            }
        }

        get("/encode/{notation}/hex") {
            val notation = call.parameters["notation"]
            if (notation.isNullOrBlank()) {
                call.respondText(text = "Notation is empty", status = HttpStatusCode.BadRequest)
                return@get
            }
            try { // if encoding throw error respond with 500
                val ad = encode(notation)
                call.respondText(ad.getHex())
            } catch (e: Exception) {
                call.respondText(
                    text = e.message ?: "Error",
                    contentType = ContentType.Text.Plain,
                    status = HttpStatusCode.InternalServerError,
                )
                return@get
            }
        }

        get("/encode/{notation}/base64") {
            val notation = call.parameters["notation"]
            if (notation.isNullOrBlank()) {
                call.respondText(text = "Notation is empty", status = HttpStatusCode.BadRequest)
                return@get
            }
            try { // if encoding throw error respond with 500
                val ad = encode(notation)
                call.respondText(ad.getBase64())
            } catch (e: Exception) {
                call.respondText(
                    text = e.message ?: "Error",
                    contentType = ContentType.Text.Plain,
                    status = HttpStatusCode.InternalServerError,
                )
                return@get
            }
        }

        get("/decode/hex/{hex}") {
            val hex = call.parameters["hex"]
            if (hex.isNullOrBlank()) {
                call.respondText(text = "Hex is empty", status = HttpStatusCode.BadRequest)
                return@get
            }
            val bArray = getUByteArrayFromHex(hex)
            val ad = decode(bArray)
            call.respondText(ad.getNotation())
        }

        get("/decode/base64/{base64}") {
            val base64 = call.parameters["base64"]
            if (base64.isNullOrBlank()) {
                call.respondText(text = "Base64 is empty", status = HttpStatusCode.BadRequest)
                return@get
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
