package de.chasenet

import com.github.anastaciocintra.escpos.EscPosConst
import com.github.anastaciocintra.escpos.Style
import io.javalin.http.Context

fun messageEndpoint(ctx: Context) {
    val message = ctx.formParam("message")
    if (message == null) {
        ctx.status(400)
        ctx.result("No message provided")
        return
    }
    if (message.length > 100) {
        ctx.status(400)
        ctx.result("Message too long")
        return
    }
    val bold = ctx.booleanFormParam("bold")
    val underline = ctx.booleanFormParam("underline")

    val justification = when (ctx.formParam("justification")) {
        "center" -> EscPosConst.Justification.Center
        "right" -> EscPosConst.Justification.Right
        else -> EscPosConst.Justification.Left_Default
    }
    val fontWidth = ctx.fontSizeParam("font_width")
    val fontHeight = ctx.fontSizeParam("font_height")

    val style = Style().apply {
        setBold(bold)
        setUnderline(if (underline) Style.Underline.OneDotThick else Style.Underline.None_Default)
        setJustification(justification)
        setFontSize(fontWidth, fontHeight)
    }

    printMessage(style, message)
    ctx.result("Printed Successfully")
}

private fun Context.booleanFormParam(key: String) = formParam(key) == "on"

private fun Context.fontSizeParam(key: String) =
    formParam(key)?.let { Style.FontSize.valueOf("_$it") } ?: Style.FontSize._1