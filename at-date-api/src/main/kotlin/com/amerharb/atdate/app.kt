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
            call.respondText("Hello! I am @Date")
        }

        get("/encode/{notation}") {
            call.paramOrBadRequest("notation") {
                val ad = encode(it)
                val res = """
                    hex: ${ad.getHex()}
                    base64: ${ad.getBase64()}
                    """.trimIndent()
                call.respondText(res)
            }
        }

        get("/encode/{notation}/hex") {
            call.paramOrBadRequest("notation") {
                val ad = encode(it)
                call.respondText { ad.getHex() }
            }
        }

        get("/encode/{notation}/base64") {
            call.paramOrBadRequest("notation") {
                val ad = encode(it)
                call.respondText(ad.getBase64())
            }
        }

        get("/decode/hex/{hex}") {
            call.paramOrBadRequest("hex") {
                val bArray = getUByteArrayFromHex(it)
                val ad = decode(bArray)
                call.respondText(ad.getNotation())
            }
        }

        get("/decode/base64/{base64}") {
            call.paramOrBadRequest("base64") {
                val bArray = getUByteArrayFromBase64(it)
                val ad = decode(bArray)
                call.respondText(ad.getNotation())
            }
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

/**
 * check if param is existed then execute action,
 * otherwise return BadRequest 400 if param is Blank or Null
 * or return InternalServerError 500 if action throw exception
 */
suspend fun ApplicationCall.paramOrBadRequest(name: String, action: suspend (String) -> Unit) {
    val param = parameters[name]
    if (param.isNullOrBlank()) {
        respondText("$name is empty", status = HttpStatusCode.BadRequest)
    } else {
        try {
            action(param)
        } catch (e: Exception) {
            respondText(
                text = e.message ?: "Error",
                contentType = ContentType.Text.Plain,
                status = HttpStatusCode.InternalServerError,
            )
        }
    }
}
