package de.chasenet

import io.javalin.Javalin
import io.javalin.http.staticfiles.Location

fun runServer() {
    Javalin.create { config ->
        config.staticFiles.add { staticFiles ->
            staticFiles.location = Location.CLASSPATH
            staticFiles.directory = "public"
            staticFiles.hostedPath = "/"
            staticFiles.precompress = true
        }
    }
        .post("/message") { ctx ->
            val message = ctx.formParam("message")
            if (message == null) {
                ctx.status(400)
                ctx.result("No message provided")
                return@post
            }
            if (message.length > 100) {
                ctx.status(400)
                ctx.result("Message too long")
                return@post
            }

            printMessage(message)
            ctx.result("Printed Successfully")
        }.post("/image") { ctx ->
            ctx.uploadedFiles()
        }
        .start(8080)
}