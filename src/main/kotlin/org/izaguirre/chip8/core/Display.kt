package org.izaguirre.chip8.core

// TechRef 2.3
class Display (
        val width: Int,
        val height: Int,
) {
    var changed = false

    private var frameBuffer = Array(height) {BooleanArray(width)}

    fun cls() {
        frameBuffer = Array(height) {BooleanArray(width)}
    }

    fun sprite(s: State, i: Int, x: Int, y: Int, n: Int): Int {
        var collision = false
        for (h in 0 until n) {
           val pattern = s.memByte(i+h)
            for (w in 0..7) {
                val current = getPixel(x+w, y+h)
                val new = (pattern shr (7-w) and 1) == 1
                setPixel(x+w, y+h, current xor new)
                if (new == current) {
                    collision = collision or new // as new==current
                }
            }
        }
        changed = true
        return if (collision) 1 else 0
    }

    fun getPixel(x: Int, y: Int) = frameBuffer[y.rem(height)][x.rem(width)]
    private fun setPixel(x: Int, y: Int, v: Boolean) {
        frameBuffer[y.rem(height)][x.rem(width)] = v
    }

    fun printScreen() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (getPixel(x, y)) {
                    print("#")
                } else {
                    print(" ")
                }
            }
            println()
        }
        println()
    }

    companion object {
        const val FONT_ADDRESS = 0x50
        const val FONT_HEIGHT = 5
        val FONT = intArrayOf (
                0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
                0x20, 0x60, 0x20, 0x20, 0x70, // 1
                0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
                0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
                0x90, 0x90, 0xF0, 0x10, 0x10, // 4
                0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
                0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
                0xF0, 0x10, 0x20, 0x40, 0x40, // 7
                0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
                0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
                0xF0, 0x90, 0xF0, 0x90, 0x90, // A
                0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
                0xF0, 0x80, 0x80, 0x80, 0xF0, // C
                0xE0, 0x90, 0x90, 0x90, 0xE0, // D
                0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
                0xF0, 0x80, 0xF0, 0x80, 0x80  // F
        )
    }
}
