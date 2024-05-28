package de.chasenet

import com.github.anastaciocintra.escpos.EscPosConst.Justification
import com.github.anastaciocintra.escpos.Style
import io.javalin.Javalin
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
            val bold = ctx.formParam("bold") == "on" ?: false
            val underline = ctx.formParam("underline") == "on" ?: false
            val justification = when(ctx.formParam("justification")) {
                "center" -> Justification.Center
                "right" -> Justification.Right
                else -> Justification.Left_Default
            }

            val style = Style().apply {
                setBold(bold)
                setUnderline(if(underline) Style.Underline.OneDotThick else Style.Underline.None_Default)
                setJustification(justification)
            }

            printMessage(style, message)
            ctx.result("Printed Successfully")
        }.post("/image") { ctx ->
            ctx.uploadedFiles()
        }
        .start(8080)
}