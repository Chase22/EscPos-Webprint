package de.chasenet

import PrintJob
import java.awt.Color
import java.awt.image.BufferedImage

fun printTest() {
    (18.rangeTo(20)).forEach {
        val bufferedImage = BufferedImage(512, (it+1)*100, BufferedImage.TYPE_INT_ARGB)
        val graphics = bufferedImage.createGraphics()
        graphics.color = Color.BLACK
        graphics.fillRect(0,0, bufferedImage.width, bufferedImage.height)
        graphics.color = Color.white
        graphics.drawString(bufferedImage.height.toString(), 30, bufferedImage.height/2 )
        graphics.dispose()

        printerAdapter.enqueue(PrintJob.PrintBufferedImage(bufferedImage))
    }
}