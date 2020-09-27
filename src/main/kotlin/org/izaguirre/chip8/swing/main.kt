package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.*
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.Timer

fun main() {
    val machine = Machine()
    val keyboard = Keyboard()
    machine.keypad = keyboard
    val board = Board(machine, 20, 20)
    board.addKeyListener(keyboard)

    //loadRom(state, "src/test/resources/sctest/SCTEST.CH8")
    //state.loadRom("src/test/resources/corax89test/test_opcode.ch8")
    val rom = "BRIX"
    machine.loadRom("/home/casa/code/kotlin/chip8Archive/moreroms/$rom")

    val frame = JFrame()
    frame.title = "Chip8"
    frame.add(board)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.size = Dimension(64 * 20 + 30, 32 * 20 + 100)
    frame.setLocationRelativeTo(null)
    frame.isVisible = true
    frame.addKeyListener(keyboard)

    Timer(40) {
        for (i in 0 until 20) {
            machine.printStep()
            machine.tickCpu()
        }
    }.start()

    Timer(17) {
        machine.tickTimer()
        if (machine.display.changed) {
            board.repaint()
            machine.display.changed = false
        }
    }.start()

}