package de.chasenet

import com.github.anastaciocintra.escpos.image.BitonalOrderedDither
import com.github.anastaciocintra.escpos.image.CoffeeImageImpl
import com.github.anastaciocintra.escpos.image.EscPosImage
import com.github.anastaciocintra.escpos.image.RasterBitImageWrapper
import io.javalin.http.UploadedFile
import qrcode.QRCode
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.net.Socket
import javax.imageio.ImageIO

private val bitImageWrapper = RasterBitImageWrapper()

private var socket = Socket(printerIp, 9000)
val pos = TM88II(socket.getOutputStream())

fun printMessage(style: Tm88iiStyle, message: String) {
    checkSocket()

    try {
        pos.writeLF(style, message)
    } catch (e: IOException) {
        resetSocket()
        pos.writeLF(style, message)
    }
}

fun printImage(image: ByteArray) {
    checkSocket()
    val convertedImage = readImage(ByteArrayInputStream(image))
    try {
        pos.write(bitImageWrapper, convertedImage)
    } catch (e: IOException) {
        resetSocket()
        pos.write(bitImageWrapper, convertedImage)
    }
}


fun printQr(message: String) {
    checkSocket()
    val qrCode = qrCode(message)
    val image = readImage(ByteArrayInputStream(qrCode.renderToBytes()))
    try {

        pos.write(bitImageWrapper, image)
    } catch (e: IOException) {
        resetSocket()
        pos.write(bitImageWrapper, image)
    }
}

private fun readImage(inputStream: InputStream) =
    ImageIO.read(inputStream).resize(512).let(::CoffeeImageImpl).let {
        EscPosImage(it, BitonalOrderedDither())
    }

private fun qrCode(message: String) = QRCode.ofSquares().build(message)

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