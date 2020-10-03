package org.izaguirre.chip8.swing

import java.awt.Color
import java.awt.Dimension
import javax.swing.JFrame

class UI: JFrame() {
    val persistenceDisplay = PersistenceDisplay()
    val board = Board(persistenceDisplay)

    init {
        title = "Chip8"
        board.background = Color(100, 100, 100)
        add(board)
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(64 * 20 + 30, 32 * 20 + 100)
        setLocationRelativeTo(null)
        isVisible = true
    }
}