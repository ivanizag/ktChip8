package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.Display
import org.izaguirre.chip8.core.Machine
import java.awt.Color
import kotlin.properties.Delegates

class PersistenceDisplay() {
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

    fun getPixelColor(x: Int, y:Int) = decay[frameBuffer[y*width+x]]

    companion object {
        val NO_DECAY = Array<Color>(2) {Color(50, 50 + it*200, 50)}
        val DECAY = Array<Color>(11) { Color(50, 50 + it*20, 50)}
        val DECAY_LARGE = Array<Color>(51) { Color(50, 50 + it*4, 50)}
    }

}