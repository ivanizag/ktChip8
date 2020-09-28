package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.*
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import kotlin.system.exitProcess

fun createToolBar(): JToolBar {
    var toolBar = JToolBar()
    var loadButton = JButton("Load")
    toolBar.add(loadButton)

    var exitButton = JButton("Exit")
    toolBar.add(exitButton)
    exitButton.addActionListener {
        exitProcess(0)
    }

    return toolBar
}

fun main() {
    val machine = Machine()
    val keyboard = Keyboard()
    machine.keypad = keyboard

    val ui = UI()
    ui.addKeyListener(keyboard)
    val fc = JFileChooser()
    fc.font = Font("TimesRoman", Font.PLAIN, 100)

    //loadRom(state, "src/test/resources/sctest/SCTEST.CH8")
    //state.loadRom("src/test/resources/corax89test/test_opcode.ch8")
    //machine.loadRom("/home/casa/code/kotlin/chip8Archive/roms/outlaw.ch8")
    machine.loadRom("/home/casa/code/kotlin/chip8Archive/moreroms/CONNECT4")

    ui.addKeyListener(object: KeyAdapter() {
        override fun keyReleased(e: KeyEvent?) {
            when (e?.keyCode) {
                KeyEvent.VK_F1 -> {
                    if (fc.showOpenDialog(ui) == JFileChooser.APPROVE_OPTION) {
                        val file = fc.selectedFile
                        if (file != null) {
                            machine.reset()
                            machine.loadRom(file.absolutePath)
                        }
                    }
                }
                KeyEvent.VK_F2 -> {
                    ui.persistenceDisplay.decay = PersistenceDisplay.NO_DECAY
                }
                KeyEvent.VK_F3 -> {
                    ui.persistenceDisplay.decay = PersistenceDisplay.DECAY
                }
                KeyEvent.VK_F4 -> {
                    ui.persistenceDisplay.decay = PersistenceDisplay.DECAY_LARGE
                }
            }
        }

    })


    Timer(40) {
        for (i in 0 until 20) {
            //machine.printStep()
            machine.tickCpu()
        }
    }.start()

    Timer(17) {
        machine.tickTimer()
        ui.persistenceDisplay.update(machine.display)
        ui.board.repaint()
    }.start()

}