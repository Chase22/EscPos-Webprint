/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package de.chasenet

import com.github.anastaciocintra.escpos.EscPosConst
import com.github.anastaciocintra.escpos.EscPosConst.Justification
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Supply ESC/POS text style commands
 * Note: If your printer isn't compatible with this class, you can try to use PrintModeStyle class
 * @see PrintModeStyle
 */
class Style : EscPosConst {
    /**
     * Values of font name.
     *
     * @see .setFontName
     */
    enum class FontName(var value: Int) {
        Font_A_Default(48),
        Font_B(49),
        Font_C(50)
    }

    /**
     * Values of font size.
     *
     * @see .setFontSize
     */
    enum class FontSize(var value: Int) {
        _1(0),
        _2(1),
        _3(2),
        _4(3),
        _5(4),
        _6(5),
        _7(6),
        _8(7)
    }

    /**
     * values of underline style.
     *
     * @see .setUnderline
     */
    enum class Underline(var value: Int) {
        None_Default(48),
        OneDotThick(49),
        TwoDotThick(50)
    }

    /**
     * values of color mode background / foreground reverse.
     *
     * @see .setColorMode
     */
    enum class ColorMode(var value: Int) {
        BlackOnWhite_Default(0),
        WhiteOnBlack(1)
    }

    protected var fontName: FontName? = null
    protected var bold: Boolean = false
    protected var underline: Underline? = null
    protected var fontWidth: FontSize? = null
    protected var fontHeight: FontSize? = null
    protected var justification: Justification? = null
    protected var defaultLineSpacing: Boolean = false
    protected var lineSpacing: Int = 0
    protected var colorMode: ColorMode? = null

    /**
     * creates Style object with default values.
     */
    constructor() {
        reset()
    }

    /**
     * creates Style object with another Style instance values.
     *
     * @param another value to be copied.
     */
    constructor(another: Style) {
        setFontName(another.fontName)
        setBold(another.bold)
        setFontSize(another.fontWidth, another.fontHeight)
        setUnderline(another.underline)
        setJustification(another.justification)
        defaultLineSpacing = another.defaultLineSpacing
        setLineSpacing(another.lineSpacing)
        setColorMode(another.colorMode)
    }

    /**
     * Reset values to default.
     */
    fun reset() {
        fontName = FontName.Font_A_Default
        fontWidth = FontSize._1
        fontHeight = FontSize._1
        bold = false
        underline = Underline.None_Default
        justification = Justification.Left_Default
        resetLineSpacing()
        colorMode = ColorMode.BlackOnWhite_Default
    }

    /**
     * Set character font name.
     *
     * @param fontName used on ESC M n
     * @return this object
     * @see .getConfigBytes
     */
    fun setFontName(fontName: FontName?): Style {
        this.fontName = fontName
        return this
    }

    /**
     * Set emphasized mode on/off
     *
     * @param bold used on ESC E n
     * @return this object
     */
    fun setBold(bold: Boolean): Style {
        this.bold = bold
        return this
    }

    /**
     * set font size
     *
     * @param fontWidth value used on GS ! n
     * @param fontHeight value used on GS ! n
     * @return this object
     * @see .getConfigBytes
     */
    fun setFontSize(fontWidth: FontSize?, fontHeight: FontSize?): Style {
        this.fontWidth = fontWidth
        this.fontHeight = fontHeight
        return this
    }

    /**
     * Set underline mode.
     *
     * @param underline value used on ESC – n
     * @return this object
     * @see .getConfigBytes
     */
    fun setUnderline(underline: Underline?): Style {
        this.underline = underline
        return this
    }

    /**
     * Set Justification for text.
     *
     * @param justification value used on ESC a n
     * @return this object
     * @see .getConfigBytes
     */
    fun setJustification(justification: Justification?): Style {
        this.justification = justification
        return this
    }

    /**
     * Set line spacing.
     *
     * @param lineSpacing value used on ESC 3 n
     * @return this object
     * @throws IllegalArgumentException when lineSpacing is not between 0 and 255.
     * @see .getConfigBytes
     */
    @Throws(IllegalArgumentException::class)
    fun setLineSpacing(lineSpacing: Int): Style {
        require(!(lineSpacing < 0 || lineSpacing > 255)) { "lineSpacing must be between 0 and 255" }
        this.defaultLineSpacing = false
        this.lineSpacing = lineSpacing
        return this
    }

    /**
     * Reset line spacing to printer default used on ESC 2
     *
     * @return this object
     * @see .getConfigBytes
     */
    fun resetLineSpacing(): Style {
        this.defaultLineSpacing = true
        lineSpacing = 0
        return this
    }

    /**
     * set color mode background / foreground reverse.
     *
     * @param colorMode value used on GS B n
     * @return this object
     */
    fun setColorMode(colorMode: ColorMode?): Style {
        this.colorMode = colorMode
        return this
    }

    @get:Throws(IOException::class)
    val configBytes: ByteArray
        /**
         * Configure font Style.
         *
         *
         * Select character font.
         *
         *
         * ASCII ESC M n
         *
         *
         *
         * Turn emphasized(bold) mode on/off.
         *
         *
         * ASCII ESC E n
         *
         *
         *
         * set font size.
         *
         *
         * ASCII GS ! n
         *
         *
         *
         * select underline mode
         *
         *
         * ASCII ESC – n
         *
         *
         *
         * Select justification
         *
         *
         * ASCII ESC a n
         *
         *
         *
         * Select default line spacing
         *
         *
         * ASCII ESC 2
         *
         *
         *
         * Set line spacing
         *
         *
         * ASCII ESC 3 n
         *
         *
         *
         * Turn white/black reverse print mode on/off
         *
         *
         * ASCII GS B n
         *
         * @return ESC/POS commands to configure style
         * @exception IOException if an I/O error occurs.
         */
        get() {
            val bytes = ByteArrayOutputStream()
            //
            //bytes.write(EscPosConst.ESC)
            //bytes.write('M'.code)
            //bytes.write(fontName!!.value)
            //
            bytes.write(EscPosConst.ESC)
            bytes.write('E'.code)
            var n = if (bold) 1 else 0
            bytes.write(n)
            //
            n = fontWidth!!.value shl 4 or fontHeight!!.value
            bytes.write(EscPosConst.GS)
            bytes.write('!'.code)
            bytes.write(n)
            //
            bytes.write(EscPosConst.ESC)
            bytes.write('-'.code)
            bytes.write(underline!!.value)
            //
            bytes.write(EscPosConst.ESC)
            bytes.write('a'.code)
            bytes.write(justification!!.value)
            //
            if (defaultLineSpacing) {
                bytes.write(EscPosConst.ESC)
                bytes.write('2'.code)
            } else {
                bytes.write(EscPosConst.ESC)
                bytes.write('3'.code)
                bytes.write(lineSpacing)
            }
            //
            bytes.write(EscPosConst.GS)
            bytes.write('B'.code)
            bytes.write(colorMode!!.value)

            return bytes.toByteArray()
        }
}
