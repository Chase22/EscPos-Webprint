class PrintAdapter(
    private val printerIp: String,
    private val printerPort: Int = 9000
) {
    private val printerQueue = PrintQueue()
    private val printerAdapter = EscPosAdapter(printerIp, printerPort)
    private val printerRunnable = PrinterThread(printerAdapter, printerQueue)
    private val printerThread = Thread(printerRunnable)

    fun enqueue(printJob: PrintJob) {
        printerQueue.offer(printJob)
    }

    fun start() {
        if (!printerThread.isAlive) {
            printerThread.start()
        }
    }

    val stats
        get() = PrinterStats(
            printerThread.isAlive,
            printerQueue.size,
            printerRunnable.printedMessages,
            printerRunnable.printedImages,
            printerRunnable.printedCharacters,
        )
}

data class PrinterStats(
    val threadActive: Boolean,
    val queueSize: Int,
    val printedMessages: Int,
    val printedImages: Int,
    val printedCharacters: Int
)