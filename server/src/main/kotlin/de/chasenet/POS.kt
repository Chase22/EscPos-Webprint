package de.chasenet

import com.github.anastaciocintra.escpos.EscPos
import com.github.anastaciocintra.escpos.Style
import java.net.Socket
import java.nio.charset.Charset

val pos = EscPos(Socket("192.168.1.133", 9000).getOutputStream())
//val pos = EscPos(Socket("localhost", 1234).getOutputStream())

fun printMessage(style: Style, message: String) {
    pos.writeLF(style, message)
    pos.feed(6)
    pos.cut(EscPos.CutMode.FULL)
}