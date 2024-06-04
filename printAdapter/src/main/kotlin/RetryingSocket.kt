import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class DelegatingInputStream(var delegate: InputStream) : InputStream() {
    override fun read(): Int = delegate.read()
}

class RetryingSocket(private val host: String, private val port: Int) : OutputStream() {
    private lateinit var socket: Socket

    private val delegatingInputStream: DelegatingInputStream by lazy {
        DelegatingInputStream(socket.getInputStream())
    }

    val inputStream: InputStream
        get() = delegatingInputStream

    private val outputStream
        get() = socket.getOutputStream()

    init {
        resetSocket()
    }

    private fun resetSocket() {
        socket = Socket(host, port)
        delegatingInputStream.delegate = socket.getInputStream()
    }

    override fun write(b: Int) {
        executeWithRetry { outputStream.write(b) }
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        executeWithRetry { outputStream.write(b, off, len) }
    }

    override fun write(b: ByteArray) = write(b, 0, b.size)

    private fun executeWithRetry(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            resetSocket()
            block()
        }
    }
}