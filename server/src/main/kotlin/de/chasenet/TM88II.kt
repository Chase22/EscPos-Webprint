package de.chasenet

import com.github.anastaciocintra.escpos.EscPos
import java.io.OutputStream

class TM88II(outputStream: OutputStream) : EscPos(outputStream) {
    init {
        this.style = Tm88iiStyle()
    }
}