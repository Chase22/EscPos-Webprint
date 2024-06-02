import com.github.anastaciocintra.escpos.Style
import java.io.ByteArrayOutputStream

class Tm88iiStyle: Style() {
    override fun getConfigBytes(): ByteArray {
        val bytes = ByteArrayOutputStream()

        bytes.write(ESC)
        bytes.write('E'.code)
        var n = if (bold) 1 else 0
        bytes.write(n)

        //
        n = fontWidth.value shl 4 or fontHeight.value
        bytes.write(GS)
        bytes.write('!'.code)
        bytes.write(n)

        //
        bytes.write(ESC)
        bytes.write('-'.code)
        bytes.write(underline.value)

        //
        bytes.write(ESC)
        bytes.write('a'.code)
        bytes.write(justification.value)

        //
        if (defaultLineSpacing) {
            bytes.write(ESC)
            bytes.write('2'.code)
        } else {
            bytes.write(ESC)
            bytes.write('3'.code)
            bytes.write(lineSpacing)
        }

        //
        bytes.write(GS)
        bytes.write('B'.code)
        bytes.write(colorMode.value)

        return bytes.toByteArray()
    }
}