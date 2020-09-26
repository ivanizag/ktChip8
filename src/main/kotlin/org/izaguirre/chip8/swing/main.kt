package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.Display
import org.izaguirre.chip8.core.State
import org.izaguirre.chip8.core.step
import java.awt.Dimension
import java.awt.event.ActionListener
import javax.swing.JFrame
import javax.swing.Timer

fun main() {
    val state = State()
    val display = Display(64, 32)
    val keyboard = Keyboard()
    val board = Board(display, 20, 20)
    board.addKeyListener(keyboard)

    //loadRom(state, "src/test/resources/sctest/SCTEST.CH8")
    state.loadRom("src/test/resources/corax89test/test_opcode.ch8")

    val frame = JFrame()
    frame.title = "Chip8"
    frame.add(board)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.size = Dimension(64*20 + 30,32*20 + 100)
    frame.setLocationRelativeTo(null)
    frame.isVisible = true

    val timer = Timer(2, ActionListener {
        step(state, display, keyboard)
        if (display.changed) {
            board.repaint()
            display.changed = false
        }
    })
    timer.start()
}