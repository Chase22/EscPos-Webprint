package de.chasenet

val serverPort = System.getenv("SERVER_PORT")?.toInt() ?: 8080
val printerIp = System.getenv("PRINTER_IP") ?: "192.168.35.247"
lateinit var printerThread: Thread


fun main() {
    runServer(serverPort)
    printerThread = Thread(PrinterThread())
    printerThread.start()
}