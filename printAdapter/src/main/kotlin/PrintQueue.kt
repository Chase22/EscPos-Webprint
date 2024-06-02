import org.slf4j.LoggerFactory
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

sealed class PrintJob(open val child: PrintJob? = null) {
    data class PrintMessage(val message: String, val style: Tm88iiStyle, override val child: PrintJob? = null) :
        PrintJob()

    class PrintImage(val image: ByteArray, child: PrintJob? = null) : PrintJob(child)
    data class PrintQrCode(val data: String, override val child: PrintJob? = null) : PrintJob()
}

typealias BlockingPrintJobQueue = BlockingQueue<PrintJob>

class PrintQueue(
    private val storage: BlockingPrintJobQueue = LinkedBlockingQueue()
) : BlockingPrintJobQueue by storage {

    override fun offer(job: PrintJob): Boolean {
        LOGGER.info("Enqueued ${job.javaClass.simpleName}")
        return storage.offer(job)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PrintQueue::class.java)
    }
}

class PrinterThread(
    private val printAdapter: EscPosAdapter,
    private val queue: BlockingPrintJobQueue
) : Runnable {
    var printedImages: Int = 0
    var printedMessages: Int = 0
    var printedCharacters: Int = 0


    override fun run() {
        while (true) {
            try {
                val job = queue.take()
                LOGGER.info("Dequeued ${job.javaClass.simpleName}")
                processJob(job)
                printAdapter.feedAndCut(6)
            } catch (e: Exception) {
                LOGGER.error("Error while printing", e)
            }
        }
    }

    private fun processJob(job: PrintJob) {
        when (job) {
            is PrintJob.PrintMessage -> {
                printedMessages.inc()
                printedCharacters += job.message.length
                printAdapter.printMessage(job.style, job.message)
            }

            is PrintJob.PrintImage -> {
                printedImages.inc()
                printAdapter.printImage(job.image)
            }

            is PrintJob.PrintQrCode -> {
                printAdapter.printQr(job.data)
            }
        }
        job.child?.let(this::processJob)
    }

    companion object {
        val LOGGER = LoggerFactory.getLogger(PrinterThread::class.java)
    }
}
