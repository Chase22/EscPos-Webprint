package de.chasenet

import com.github.anastaciocintra.escpos.EscPos
import org.eclipse.jetty.util.BlockingArrayQueue
import org.slf4j.LoggerFactory

sealed class PrintJob(open val child: PrintJob? = null) {
    data class PrintMessage(val message: String, val style: Tm88iiStyle, override val child: PrintJob? = null) : PrintJob()
    class PrintImage(val image: ByteArray, child: PrintJob? = null) : PrintJob(child)
    data class PrintQrCode(val data: String, override val child: PrintJob? = null): PrintJob()
}

val printQueuesize: Int
    get() = printQueue.size

var printedImages: Int = 0
var printedMessages: Int = 0
var printedCharacters: Int = 0

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
                processJob(job)
                pos.feed(6)
                pos.cut(EscPos.CutMode.FULL)
            } catch(e:Exception) {
                LOGGER.error("Error while printing", e)
            }
        }
    }

    private fun processJob(job: PrintJob) {
        when (job) {
            is PrintJob.PrintMessage -> {
                printedMessages.inc()
                printedCharacters += job.message.length
                printMessage(job.style, job.message)
            }

            is PrintJob.PrintImage -> {
                printedImages.inc()
                printImage(job.image)
            }

            is PrintJob.PrintQrCode -> {
                printQr(job.data)
            }
        }
        job.child?.let(this::processJob)
    }

}
