package de.chasenet

import io.javalin.Javalin
import io.javalin.http.staticfiles.Location

fun runServer(serverPort: Int) {
    Javalin.create { config ->
        config.staticFiles.add { staticFiles ->
            staticFiles.location = Location.CLASSPATH
            staticFiles.directory = "public"
            staticFiles.hostedPath = "/assets"
            staticFiles.precompress = true
        }
        config.http.prefer405over404 = true

    }
        .get("/") { ctx -> ctx.sendHttp() }
        .get("/stats") { ctx ->
            ctx.result(
                listOf(
                    "Printer Thread active: ${printerThread.isAlive}",
                    "Printer Queue: $printQueuesize",
                    "Printed Messages: $printedMessages",
                    "Printed Images: $printedImages",
                    "Printed Characters: $printedCharacters"
                ).joinToString("\n")
            )
        }
        .post("/message", ::messageEndpoint)
        .post("/image") { ctx ->
            ctx.uploadedFiles()
        }
        .start(serverPort)
}