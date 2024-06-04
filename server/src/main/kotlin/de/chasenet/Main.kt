package de.chasenet

import PrintAdapter

val serverPort = System.getenv("SERVER_PORT")?.toInt() ?: 8080
val printerIp = System.getenv("PRINTER_IP") ?: "192.168.35.247"

val printerAdapter = PrintAdapter(printerIp)


fun main() {
    runServer(serverPort)
    printerAdapter.start()
}