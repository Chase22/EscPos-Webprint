package de.chasenet

import com.github.anastaciocintra.escpos.Style
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.staticfiles.Location

fun runServer() {
    Javalin.create { config ->
        config.staticFiles.add { staticFiles ->
            staticFiles.location = Location.CLASSPATH
            staticFiles.directory = "public"
            staticFiles.hostedPath = "/assets"
            staticFiles.precompress = true
        }
    }
        .get("/") { ctx -> ctx.sendHttp() }
        .post("/message", ::messageEndpoint)
        .post("/image") { ctx ->
            ctx.uploadedFiles()
        }
        .start(8080)
}