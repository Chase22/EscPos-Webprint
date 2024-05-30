package de.chasenet

import com.github.anastaciocintra.escpos.EscPos
import com.github.anastaciocintra.escpos.Style
import com.github.anastaciocintra.escpos.image.BitonalOrderedDither
import com.github.anastaciocintra.escpos.image.CoffeeImageImpl
import com.github.anastaciocintra.escpos.image.EscPosImage
import com.github.anastaciocintra.escpos.image.RasterBitImageWrapper
import io.javalin.http.UploadedFile
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.Socket
import javax.imageio.ImageIO

private val bitImageWrapper = RasterBitImageWrapper()

private var socket = Socket("192.168.35.247", 9000)
val pos = TM88II(socket.getOutputStream())

fun printMessage(style: Tm88iiStyle, message: String) {
    checkSocket()

    try {
        pos.writeLF(style, message)
        pos.feed(6)
        pos.cut(EscPos.CutMode.FULL)
    } catch (e: IOException) {
        resetSocket()
        pos.writeLF(style, message)
        pos.feed(6)
        pos.cut(EscPos.CutMode.FULL)
    }
}

fun printImage(image: UploadedFile) {
    checkSocket()
    val convertedImage = ImageIO.read(image.content()).resize(512).let(::CoffeeImageImpl)
    try {
        pos.write(bitImageWrapper, EscPosImage(convertedImage, BitonalOrderedDither()))
    } catch (e: IOException) {
        resetSocket()
        pos.write(bitImageWrapper, EscPosImage(convertedImage, BitonalOrderedDither()))
    }
}

private fun checkSocket() {
    if (socket.isOutputShutdown || socket.isClosed || !socket.isConnected) {
        resetSocket()
    }
}

private fun resetSocket() {
    socket.close()
    socket = Socket("192.168.1.133", 9000)
    pos.outputStream = socket.getOutputStream()
}

private fun BufferedImage.resize(width: Int = -1, height: Int = -1) =
    getScaledInstance(width, height, BufferedImage.SCALE_DEFAULT).toBufferedImage()

private fun Image.toBufferedImage(): BufferedImage {
    val bi = BufferedImage(getWidth(null), getHeight(null), BufferedImage.TYPE_INT_ARGB)
    bi.createGraphics().apply {
        drawImage(this@toBufferedImage, 0, 0, null)
        dispose()
    }
    return bi
}