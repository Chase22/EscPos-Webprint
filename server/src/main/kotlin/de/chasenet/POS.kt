package de.chasenet

import com.github.anastaciocintra.escpos.EscPos
import com.github.anastaciocintra.escpos.Style
import java.io.PipedOutputStream
import java.net.Socket
import java.nio.charset.Charset

private var socket = Socket("192.168.1.133", 9000)
val pos = EscPos(socket.getOutputStream())

fun printMessage(style: Style, message: String) {
    if(socket.isOutputShutdown || socket.isClosed || !socket.isConnected) {
        socket.close()
        socket = Socket("192.168.1.133", 9000)
        pos.outputStream = socket.getOutputStream()
    }

    pos.writeLF(style, message)
    pos.feed(6)
    pos.cut(EscPos.CutMode.FULL)
}