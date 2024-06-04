import com.github.anastaciocintra.escpos.EscPos
import com.github.anastaciocintra.escpos.image.BitonalOrderedDither
import com.github.anastaciocintra.escpos.image.CoffeeImageImpl
import com.github.anastaciocintra.escpos.image.EscPosImage
import com.github.anastaciocintra.escpos.image.RasterBitImageWrapper
import qrcode.QRCode
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.imageio.ImageIO

class EscPosAdapter(
    printerHost: String,
    printerPort: Int
) {
    private val bitImageWrapper = RasterBitImageWrapper()
    private val retrySocket = RetryingSocket(printerHost, printerPort)
    private val pos = TM88II(retrySocket)

    fun printMessage(style: Tm88iiStyle, message: String) {
        pos.writeLF(style, message)
    }

    fun printImage(image: ByteArray) {
        val convertedImage = readImage(ByteArrayInputStream(image))
        pos.write(bitImageWrapper, convertedImage)
    }

    fun printImage(image: BufferedImage) {
        val convertedImage = image.resize(512).let(::CoffeeImageImpl).let {
            EscPosImage(it, BitonalOrderedDither())
        }
        pos.write(bitImageWrapper, convertedImage)
    }

    fun printQr(message: String) {
        val qrCode = qrCode(message)
        val image = readImage(ByteArrayInputStream(qrCode.renderToBytes()))
        pos.write(bitImageWrapper, image)
    }

    fun feedAndCut(feed: Int = 6) {
        pos.feed(feed)
        pos.cut(EscPos.CutMode.FULL)
    }

    private fun readImage(inputStream: InputStream) =
        ImageIO.read(inputStream).resize(512).let(::CoffeeImageImpl).let {
            EscPosImage(it, BitonalOrderedDither())
        }

    private fun qrCode(message: String) = QRCode.ofSquares().build(message)

    private fun BufferedImage.resize(width: Int = -1, height: Int = -1) =
        getScaledInstance(
            this.width.coerceAtMost(width),
            height,
            BufferedImage.SCALE_DEFAULT
        ).toBufferedImage()

    private fun Image.toBufferedImage(): BufferedImage {
        val bi = BufferedImage(getWidth(null), getHeight(null), BufferedImage.TYPE_INT_ARGB)
        bi.createGraphics().apply {
            drawImage(this@toBufferedImage, 0, 0, null)
            dispose()
        }
        return bi
    }
}

