package de.chasenet

import com.github.anastaciocintra.escpos.EscPos
import io.javalin.http.UploadedFile
import org.eclipse.jetty.util.BlockingArrayQueue
import org.slf4j.LoggerFactory

sealed class PrintJob {
    data class PrintMessage(val message: String, val style: Tm88iiStyle) : PrintJob()
    class PrintImage(val image: ByteArray, val imagePosition: ImagePosition, val printMessage: PrintMessage) : PrintJob()
    data class PrintQrCode(val data: String): PrintJob()
}

private val LOGGER = LoggerFactory.getLogger("PrinterQueue")


private val printQueue = BlockingArrayQueue<PrintJob>()

fun enqueuePrintJob(job: PrintJob) {
    LOGGER.info("Enqueued ${job.javaClass.simpleName}")
    printQueue.offer(job)
}

class PrinterThread: Runnable {
    override fun run() {
        while (true) {
            try {
                val job = printQueue.take()
                LOGGER.info("Dequeued ${job.javaClass.simpleName}")
                when (job) {
                    is PrintJob.PrintMessage -> {
                        printMessage(job.style, job.message)
                    }
                    is PrintJob.PrintImage -> {
                        if (job.imagePosition == ImagePosition.above) {
                            printImage(job.image)
                        }
                        printMessage(job.printMessage.style, job.printMessage.message)
                        if (job.imagePosition == ImagePosition.below) {
                            printImage(job.image)
                        }
                    }
                    is PrintJob.PrintQrCode -> {
                        printQr(job.data)
                    }
                }
                pos.feed(6)
                pos.cut(EscPos.CutMode.FULL)
            } catch(e:Exception) {
                LOGGER.error("Error while printing", e)
            }
        }
    }

}
