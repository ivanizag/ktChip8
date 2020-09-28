package org.izaguirre.chip8.swing

import java.awt.Graphics
import javax.swing.JPanel

class Board (
        private val display: PersistenceDisplay,
) : JPanel() {
    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g == null) return

        if (display.height == 0 || display.width ==0) return
        val dotHeight = height / display.height
        val dotWidth = width / display.width

        for (y in 0 until display.height) {
            for (x in 0 until display.width) {
                g.color = display.getPixelColor(x, y)
                g.fillRect(x * dotWidth, y * dotHeight, dotWidth-1, dotHeight-1)
            }
        }
    }
}