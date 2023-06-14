import com.amerharb.atdate.decode
import com.amerharb.atdate.encode
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
            val ad = encode(notation)
            if (ad == null) {
                call.respondText("Notation is invalid")
            } else {
                //TODO: return json
                call.respondText(ad.toString())
            }
        }

        post("/decode/{hex}") {
            val hex = call.parameters["hex"]
            if (hex == null) {
                call.respondText("Hex is null")
                return@post
            }
            // convert hex text into Array[UByte]
            val bArray = getUByteArray(hex)
            val ad = decode(bArray)
            //TODO: return json
            call.respondText(ad.toString())
        }
    }
}

fun getUByteArray(hex: String): Array<UByte> {
    // convert hex start with 0x into Array[UByte]
    val byteList = hex
        .drop(2)
        .chunked(2)
        .map { it.toInt(16).toUByte() }
    return byteList.toTypedArray()
}
