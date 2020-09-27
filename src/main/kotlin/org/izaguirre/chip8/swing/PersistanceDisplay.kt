package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.Display
import org.izaguirre.chip8.core.Machine
import java.awt.Color

class PersistanceDisplay() {
    var height = 0
        private set
    var width = 0
        private set
    private var frameBuffer = IntArray(0)
    var decay = DECAY

    fun update(d: Display) {

        // Clear on resize
        if (d.width != width || d.height != height) {
            height = d.height
            width = d.width
            frameBuffer = IntArray(height * width)
        }

        var i = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (d.getPixel(x, y)) {
                    // Refresh phosphor
                    frameBuffer[i] = decay.size - 1
                } else if (frameBuffer[i] > 0) {
                    // Decay
                    frameBuffer[i]--
                }
                i++
            }
        }
    }

    fun getPixelColor(x: Int, y:Int): Color {
        return decay[frameBuffer[y*width+x]]
        /*
        val v = frameBuffer[y*width+x]
        return when {
            v > 9 -> Color(12, 240, 20)
            v > 7 -> Color(12, 240, 20)
            v > 5 -> Color(12, 120, 20)
            v > 2 -> Color(12, 60, 20)
            else -> Color(12,20,20)
        }*/
    }

    companion object {
        const val MAX_BRIGHTNESS = 10
        val NO_DECAY = Array<Color>(2) {Color(50, 50 + (1-it)*200, 50)}
        val DECAY = Array<Color>(10) { Color(50, 50 + (10-it)*20, 50)}
        val DECAY_LARGE = Array<Color>(25) { Color(50, 50 + (25-it)*4, 50)}
    }

}