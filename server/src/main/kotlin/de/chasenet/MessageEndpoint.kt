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
    val whiteOnBlack = ctx.booleanFormParam("white_on_black")

    val justification = when (ctx.formParam("justification")) {
        "center" -> EscPosConst.Justification.Center
        "right" -> EscPosConst.Justification.Right
        else -> EscPosConst.Justification.Left_Default
    }
    val fontWidth = ctx.fontSizeParam("font_width")
    val fontHeight = ctx.fontSizeParam("font_height")

    val style = Tm88iiStyle().apply {
        setBold(bold)
        setUnderline(if (underline) Style.Underline.OneDotThick else Style.Underline.None_Default)
        setJustification(justification)
        setFontSize(fontWidth, fontHeight)
        setColorMode(if (whiteOnBlack) Style.ColorMode.WhiteOnBlack else Style.ColorMode.BlackOnWhite_Default)
    }

    ctx.uploadedFile("image")?.let {
        if (it.size() > 0) {
            printImage(it)
        }
    }

    printMessage(style, message)
    ctx.result("Printed Successfully")
}

private fun Context.booleanFormParam(key: String) = formParam(key).toBoolean()

private fun Context.fontSizeParam(key: String) =
    formParam(key)?.let { Style.FontSize.valueOf("_$it") } ?: Style.FontSize._1