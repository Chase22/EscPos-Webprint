package de.chasenet

import com.github.anastaciocintra.escpos.EscPos
import com.github.anastaciocintra.escpos.Style
import java.net.Socket
import java.nio.charset.Charset

val pos = EscPos(Socket("192.168.1.133", 9000).getOutputStream())
//val pos = EscPos(Socket("localhost", 1234).getOutputStream())

fun printMessage(message: String) {
    pos.outputStream.write(de.chasenet.Style().configBytes)
    pos.outputStream.write(message.toByteArray(Charset.forName(pos.defaultCharsetName)))
    //pos.writeLF(Style().apply { /*setLineSpacing(0)*/ }, message)
    repeat(6) { pos.write(EscPos.LF) }
    pos.cut(EscPos.CutMode.FULL)
}