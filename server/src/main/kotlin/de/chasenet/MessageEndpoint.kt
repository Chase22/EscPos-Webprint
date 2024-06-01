package de.chasenet

import com.github.anastaciocintra.escpos.EscPosConst
import com.github.anastaciocintra.escpos.Style
import com.github.anastaciocintra.escpos.Style.ColorMode
import com.github.anastaciocintra.escpos.Style.Underline
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.javalin.http.Context
import io.javalin.http.UploadedFile
import java.time.Duration

enum class ImagePosition {
    ABOVE, BELOW
}

private val bucket = Bucket.builder()
    .addLimit(
        Bandwidth.builder()
            .capacity(30)
            .refillIntervally(
                30, Duration.ofMinutes(10)
            ).build()
    ).build()

fun messageEndpoint(ctx: Context) {
    if (!bucket.tryConsume(1)) {
        ctx.status(429)
        ctx.result("Too many prints. Please wait 10 minutes.\n")
        return
    }
    val message = ctx.formParam("message")
    if (message == null) {
        ctx.status(400)
        ctx.result("No message provided\n")
        return
    }
    if (message.length > 1000) {
        ctx.status(400)
        ctx.result("Message too long\n")
        return
    }
    val bold = ctx.booleanFormParam("bold")
    val underline = ctx.booleanFormParam("underline", Underline.OneDotThick, Underline.None_Default)
    val whiteOnBlack = ctx.booleanFormParam("white_on_black", ColorMode.WhiteOnBlack, ColorMode.BlackOnWhite_Default)
    val printAsQr = ctx.booleanFormParam("print_as_qr")

    val justification = when (ctx.formParam("justification")) {
        "center" -> EscPosConst.Justification.Center
        "right" -> EscPosConst.Justification.Right
        else -> EscPosConst.Justification.Left_Default
    }
    val fontWidth = ctx.fontSizeParam("font_width")
    val fontHeight = ctx.fontSizeParam("font_height")

    val style = Tm88iiStyle().apply {
        setBold(bold)
        setUnderline(underline)
        setJustification(justification)
        setFontSize(fontWidth, fontHeight)
        setColorMode(whiteOnBlack)
    }

    val image = ctx.uploadedFile("image")?.takeIf { it.size() > 0 }
    val imagePosition = ImagePosition.valueOf(ctx.formParam("image_position")?.uppercase() ?: "ABOVE")

    if (image != null) {
        enqueueImage(imagePosition, image, message, style)
    } else if (printAsQr) {
        enqueuePrintJob(PrintJob.PrintQrCode(message))
    } else {
        enqueuePrintJob(PrintJob.PrintMessage(message, style))
    }

    ctx.result("Print was enqueued. Printing may take a second. Check /stats for status of the queue\n")
}

private fun enqueueImage(
    imagePosition: ImagePosition,
    image: UploadedFile,
    message: String,
    style: Tm88iiStyle
) {
    if (imagePosition == ImagePosition.ABOVE) {
        enqueuePrintJob(
            PrintJob.PrintImage(image.toByteArray(), PrintJob.PrintMessage(message, style))
        )
    } else {
        enqueuePrintJob(
            PrintJob.PrintMessage(message, style, PrintJob.PrintImage(image.toByteArray()))
        )
    }
}

private fun UploadedFile.toByteArray(): ByteArray = content().readAllBytes()

private fun Context.booleanFormParam(key: String) = formParam(key).toBoolean()
private fun <T> Context.booleanFormParam(key: String, trueValue: T, falseValue: T): T =
    if (formParam(key).toBoolean()) trueValue else falseValue

private fun Context.fontSizeParam(key: String) =
    formParam(key)?.let { Style.FontSize.valueOf("_$it") } ?: Style.FontSize._1