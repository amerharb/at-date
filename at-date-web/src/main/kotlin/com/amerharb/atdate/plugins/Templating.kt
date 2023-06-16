package com.amerharb.atdate.plugins

import io.ktor.server.html.*
import kotlinx.html.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureTemplating() {
    routing {
        get("/") {
            call.respondHtml {
                head {
                    title { +"@DATE Encoder/Decoder" }
                }
                body {
                    h1 { +"@DATE Encoder/Decoder" }
                    form(action = "/convert", method = FormMethod.post) {
                        label {
                            +"Enter a date notation: "
                            textInput(name = "dateInput")
                        }
                        br
                        button { +"Convert to Hex" }
                    }
                }
            }
        }
    }
}
