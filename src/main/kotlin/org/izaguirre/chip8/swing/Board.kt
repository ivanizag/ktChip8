package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.Machine
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

class Board (
        private val machine: Machine,
        private val dotWidth: Int,
        private val dotHeight: Int,
) : JPanel() {
    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (g == null) return

        for (y in 0 until machine.display.height) {
            for (x in 0 until machine.display.width) {
                if (machine.display.getPixel(x, y)) {
                    g.color = Color.DARK_GRAY
                } else {
                    g.color = Color.LIGHT_GRAY
                }
                g.fillRect(x * dotWidth, y * dotHeight, dotWidth-1, dotHeight-1)
            }
        }
    }
}