package com.amerharb.atdate

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.util.Base64

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
				if (!isValidHex(it)) {
					call.respondText("Invalid hex input", status = HttpStatusCode.BadRequest)
					return@paramOrBadRequest
				}
				val bArray = getUByteArrayFromHex(it)
				val ad = decode(bArray)
				call.respondText(ad.getNotation())
			}
		}

		get("/decode/base64/{base64}") {
			call.paramOrBadRequest("base64") {
				if (!isValidBase64(it)) {
					call.respondText("Invalid base64 input", status = HttpStatusCode.BadRequest)
					return@paramOrBadRequest
				}
				val bArray = getUByteArrayFromBase64(it)
				val ad = decode(bArray)
				call.respondText(ad.getNotation())
			}
		}
	}
}

private fun isValidHex(hex: String): Boolean {
	val regex = Regex("^0x[0-9a-fA-F]{4,}$")
	return regex.matches(hex) && hex.length % 2 == 0
}

private fun getUByteArrayFromHex(hex: String): Array<UByte> {
	val byteList = hex
		.drop(2) // remove "0x" from input
		.chunked(2) // split into 2 chars where each chunk is a byte
		.map { it.toInt(16).toUByte() } // convert each chunk to UByte
	return byteList.toTypedArray()
}

private fun isValidBase64(base64: String): Boolean {
	val regex = Regex("^[0-9a-zA-Z+/]+={0,3}$")
	return regex.matches(base64) && base64.length % 4 == 0
}

private fun getUByteArrayFromBase64(base64: String): Array<UByte> {
	val byteList = Base64.getDecoder().decode(base64)
		.map { it.toUByte() }
	return byteList.toTypedArray()
}

private fun AtDate.getHex(): String {
	val hexList = this.getPayload().map { it.toString(16).padStart(2, '0') }
	return "0x${hexList.joinToString("")}"
}

private fun AtDate.getBase64(): String {
	val byteList: List<Byte> = this.getPayload().map { it.toByte() }
	val byteArr: ByteArray = byteList.toByteArray()
	return Base64.getEncoder().encodeToString(byteArr)
}

/**
 * check if param is existed then execute action,
 * otherwise return BadRequest 400 if param is Blank or Null
 * or return InternalServerError 500 if action throw exception
 */
private suspend fun ApplicationCall.paramOrBadRequest(paramName: String, action: suspend (String) -> Unit) {
	val param = parameters[paramName]
	if (param.isNullOrBlank()) {
		respondText("$param is empty", status = HttpStatusCode.BadRequest)
	} else {
		try {
			action(param)
		} catch (e: Exception) {
			respondText(
				text = e.message ?: "Error",
				contentType = ContentType.Text.Plain,
				status = HttpStatusCode.InternalServerError
			)
		}
	}
}
