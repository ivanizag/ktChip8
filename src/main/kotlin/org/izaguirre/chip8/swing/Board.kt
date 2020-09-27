package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.Machine
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

class Board (
        private val display: PersistanceDisplay,
        private val dotWidth: Int,
        private val dotHeight: Int,
) : JPanel() {
    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g == null) return

        for (y in 0 until display.height) {
            for (x in 0 until display.width) {
                g.color = display.getPixelColor(x, y)
                g.fillRect(x * dotWidth, y * dotHeight, dotWidth-1, dotHeight-1)
            }
        }
    }
}