// TechRef 2.3
const val FONT_HEIGHT = 5u

class Display (
        private val width: Int,
        private val height: Int,
) {
    private var frameBuffer = Array(height) {BooleanArray(width)}

    public fun cls() {
        frameBuffer = Array(height) {BooleanArray(width)}
    }

    fun sprite(s: State, i: UShort, x: Int, y: Int, n: Int): UByte {
        var collision = false
        for (h in 0..n-1) {
           val pattern = s.memByte(i+h.toUShort()).toInt()
            for (w in 0..7) {
                val current = getPixel(x+w, y+h)
                val new = (pattern shr (7-w) and 1) == 1
                if (new != current) {
                    setPixel(x+w, y+h, new)
                } else {
                    collision = collision or new // as new==current
                }
            }
        }
        return if (collision) 1u else 0u
    }

    fun getPixel(x: Int, y: Int) = frameBuffer[y.rem(height)][x.rem(width)]
    fun setPixel(x: Int, y: Int, v: Boolean) {
        frameBuffer[y.rem(height)][x.rem(width)] = v
    }

    fun print() {
        for (x in 0..height) {
            for (y in 0..width) {
                if (getPixel(x, y)) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
        println()
    }
}
