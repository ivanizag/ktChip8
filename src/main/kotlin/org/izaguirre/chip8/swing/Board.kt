package org.izaguirre.chip8.swing

import java.awt.Graphics
import javax.swing.JPanel

class Board (
        private val display: PersistenceDisplay,
) : JPanel() {
    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        g?.let {
            if (display.height == 0 || display.width == 0) return
            val dotHeight = height / display.height + 1
            val dotWidth = width / display.width + 1

            for (y in 0 until display.height) {
                for (x in 0 until display.width) {
                    g.color = display.getPixelColor(x, y)
                    g.fillRect((x * width) / display.width,
                        (y * height) / display.height,
                        dotWidth, dotHeight)
                }
            }
        }
    }
}