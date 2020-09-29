package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.*
import java.awt.Font
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.File
import javax.swing.*

fun main() {
    val machine = Machine()
    val keyboard = Keyboard()
    machine.keypad = keyboard

    val ui = UI()
    ui.addKeyListener(keyboard)
    val fc = JFileChooser().apply {
        font = Font("TimesRoman", Font.PLAIN, 100)
        currentDirectory = File("/home/casa/code/kotlin/chip8Archive/roms")
    }

    //machine.loadRom("src/test/resources/sctest/SCTEST.CH8")
    // Uses octo planes:
    //      machine.loadRom("/home/casa/code/kotlin/chip8Archive/roms/trucksimul8or.ch8")
    //      machine.loadRom("/home/casa/code/kotlin/chip8Archive/roms/t8nks.ch8")
    //      machine.loadRom("/home/casa/code/kotlin/chip8Archive/roms/superneatboy.ch8")
    //      machine.loadRom("/home/casa/code/kotlin/chip8Archive/roms/skyward.ch8")
    //      machine.loadRom("/home/casa/code/kotlin/chip8Archive/roms/sk8.ch8")
    machine.loadRom("/home/casa/code/kotlin/chip8Archive/roms/rockto.ch8")

    ui.addKeyListener(object: KeyAdapter() {
        override fun keyReleased(e: KeyEvent?) {
            when (e?.keyCode) {
                KeyEvent.VK_F1 -> {
                    if (fc.showOpenDialog(ui) == JFileChooser.APPROVE_OPTION) {
                        fc.selectedFile?.let{
                            machine.reset()
                            machine.loadRom(it.absolutePath)
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