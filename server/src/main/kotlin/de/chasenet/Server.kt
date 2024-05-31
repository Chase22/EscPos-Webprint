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
        .post("/message", ::messageEndpoint)
        .post("/image") { ctx ->
            ctx.uploadedFiles()
        }
        .start(serverPort)
}