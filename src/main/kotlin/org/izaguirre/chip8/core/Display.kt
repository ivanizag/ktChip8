package org.izaguirre.chip8.core

// TechRef 2.3
class Display {
    private var isHires = false
    var width = 64
        private set
    var height = 32
        private set
    private var frameBuffer = IntArray(height*width)

    // Secondary octo plane
    private var activePlanes = 1
    var hasPlanes = false
        private set

    fun cls() {
        for (i in frameBuffer.indices) {
            frameBuffer[i] = frameBuffer[i] and activePlanes.inv()
        }
    }

    private fun reset() {
        frameBuffer = IntArray(height*width)
        activePlanes = 1
        hasPlanes = false
    }

    fun lores() {
        isHires = false
        width = 64
        height = 32
        reset()
    }

    fun hires() {
        isHires = true
        width = 128
        height = 64
        reset()
    }

    fun activePlanes(planes: Int) {
        activePlanes = planes
        hasPlanes = true
    }

    fun multiPlaneSprite(s: State, i: Int, x: Int, y: Int, n: Int): Int {
        var collision = false
        var i = i
        if ((activePlanes and 1) !=0) {
            collision = sprite(s, i, x, y, n, 1)
            i += if (n == 0) 32 else n
        }

        if ((activePlanes and 2) != 0) {
            collision = collision || sprite(s, i, x, y, n, 2)
        }

        return if (collision) 1 else 0
    }

    private fun sprite(s: State, i: Int, x: Int, y: Int, n: Int, plane: Int): Boolean {
        if (n==0 /*&& isHires // sk8 does big sprites on lores */) {
            wideSprite(s, i, x, y, plane)
        }

        var collision = false
        for (h in 0 until n) {
           val pattern = s.memByte(i+h)
            for (w in 0..7) {
                val current = getPixel(x+w, y+h, plane)
                val new = (pattern shr (7-w) and 1) == 1
                setPixel(x+w, y+h, plane,current xor new)
                if (new == current) {
                    collision = collision || new // as new==current
                }
            }
        }
        return collision
    }

    private fun wideSprite(s: State, i: Int, x: Int, y: Int, plane:  Int): Boolean {
        var collision = false
        for (h in 0 until 16) {
            val pattern = s.memWord(i+2*h)
            for (w in 0 until 16) {
                val current = getPixel(x+w, y+h, plane)
                val new = (pattern shr (15-w) and 1) == 1
                setPixel(x+w, y+h, plane, current xor new)
                if (new == current) {
                    collision = collision || new // as new==current
                }
            }
        }
        return collision
    }


    fun scroll(deltaX: Int, deltaY: Int) {
        val newBuffer = frameBuffer.clone()
        for (x in 0 until width) {
            for (y in 0 until height) {
                val dst = y * width + x
                val src = (y - deltaY) * width + x - deltaX
                var oldValue = 0
                if ((y-deltaY) in 0 until height && (x-deltaX) in 0 until width) {
                    oldValue = frameBuffer[src] and activePlanes
                }
                newBuffer[dst] = (newBuffer[dst] and activePlanes.inv()) or oldValue
            }
        }
        frameBuffer = newBuffer
    }


    fun getPixelColor(x: Int, y: Int): Int {
        val pos = y.rem(height) * width + x.rem(width)
        return frameBuffer[pos]
    }

    private fun getPixel(x: Int, y: Int, plane: Int): Boolean {
        val pos = y.rem(height) * width + x.rem(width)
        return (frameBuffer[pos] and plane) != 0
    }

    private fun setPixel(x: Int, y: Int, plane: Int, v: Boolean) {
        val pos = y.rem(height) * width + x.rem(width)
        if (v) {
            frameBuffer[pos] = frameBuffer[pos] or plane
        } else {
            frameBuffer[pos] = frameBuffer[pos] and plane.inv()
        }
    }

    fun printScreen() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (getPixel(x, y, 1)) {
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

        const val LARGE_FONT_ADDRESS = 0x100
        const val LARGE_FONT_HEIGHT = 5
        val LARGE_FONT = intArrayOf(
                /* From octo */
                0x3C, 0x7E, 0xE7, 0xC3, 0xC3, 0xC3, 0xC3, 0xE7, 0x7E, 0x3C,
                0x18, 0x38, 0x58, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x3C,
                0x3E, 0x7F, 0xC3, 0x06, 0x0C, 0x18, 0x30, 0x60, 0xFF, 0xFF,
                0x3C, 0x7E, 0xC3, 0x03, 0x0E, 0x0E, 0x03, 0xC3, 0x7E, 0x3C,
                0x06, 0x0E, 0x1E, 0x36, 0x66, 0xC6, 0xFF, 0xFF, 0x06, 0x06,
                0xFF, 0xFF, 0xC0, 0xC0, 0xFC, 0xFE, 0x03, 0xC3, 0x7E, 0x3C,
                0x3E, 0x7C, 0xE0, 0xC0, 0xFC, 0xFE, 0xC3, 0xC3, 0x7E, 0x3C,
                0xFF, 0xFF, 0x03, 0x06, 0x0C, 0x18, 0x30, 0x60, 0x60, 0x60,
                0x3C, 0x7E, 0xC3, 0xC3, 0x7E, 0x7E, 0xC3, 0xC3, 0x7E, 0x3C,
                0x3C, 0x7E, 0xC3, 0xC3, 0x7F, 0x3F, 0x03, 0x03, 0x3E, 0x7C,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // no hex chars!
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

        )
    }
}
