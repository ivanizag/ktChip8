package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.Display
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

class Board (
        private val display: Display,
        private val dotWidth: Int,
        private val dotHeight: Int,
) : JPanel() {
    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g == null) return

        for (y in 0 until display.height) {
            for (x in 0 until display.width) {
                if (display.getPixel(x, y)) {
                    g.color = Color.DARK_GRAY
                } else {
                    g.color = Color.LIGHT_GRAY
                }
                g.fillRect(x * dotWidth, y * dotHeight, dotWidth-1, dotHeight-1)
            }
        }
    }
}