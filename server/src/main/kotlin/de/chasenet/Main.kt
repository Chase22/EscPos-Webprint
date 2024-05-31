package de.chasenet

val serverPort = System.getenv("SERVER_PORT")?.toInt() ?: 8080
val printerIp = System.getenv("PRINTER_IP")?: "192.168.35.247"

fun main() {
    runServer(serverPort)
    Thread(PrinterThread()).start()
}