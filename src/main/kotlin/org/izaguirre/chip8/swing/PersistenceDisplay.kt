package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.Display
import java.awt.Color

class PersistenceDisplay {
    var height = 0
        private set
    var width = 0
        private set
    private var frameBuffer = IntArray(0)
    var decay = DECAY
        set(value) {
            field = value
            frameBuffer.fill(0)
        }
    private var hasColors = false

    fun update(d: Display) {

        // Clear on resize
        if (d.width != width || d.height != height) {
            height = d.height
            width = d.width
            frameBuffer = IntArray(height * width)
        }

        hasColors = d.hasPlanes
        var i = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (hasColors) {
                    // Color, no persistence
                    frameBuffer[i] = d.getPixelColor(x, y)
                } else if (d.getPixelColor(x, y) != 0) {
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

    fun getPixelColor(x: Int, y:Int) =
        if (hasColors) COLORS[frameBuffer[y * width + x]]
        else decay[frameBuffer[y * width + x]]

    companion object {
        val NO_DECAY = Array(2) {Color(50, 50 + it*200, 50)}
        val DECAY = Array(6) { Color(50, 50 + it*20, 50)}
        val DECAY_LARGE = Array(51) { Color(50, 50 + it*4, 50)}

        val COLORS = arrayOf( // From Octo
            Color(0xf9ffb3),
            Color(0x3d8026),
            Color(0xabcc47),
            Color(0x00121a))
    }

}