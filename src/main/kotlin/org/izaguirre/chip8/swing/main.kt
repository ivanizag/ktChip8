package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.*
import java.awt.Font
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.File
import java.time.Duration
import java.time.Instant
import javax.swing.JFileChooser
import javax.swing.Timer

const val CYCLES_PER_FRAME_DEFAULT = 17
const val MS_PER_FRAME =  17 // 60 Hz

fun main() {
    //  Setup machine
    val machine = Machine()
    val keyboard = Keyboard()
    machine.keypad = keyboard
    var cycles_per_frame = CYCLES_PER_FRAME_DEFAULT

    //machine.loadRom("src/test/resources/sctest/SCTEST.CH8")
    // Weird behaviour:
    //    var currentRom = "/home/casa/code/kotlin/chip8Archive/roms/t8nks.ch8"
    // Uses scroll (fc, cx):
    //    var currentRom = "/home/casa/code/kotlin/chip8Archive/roms/sk8.ch8"
    //    var currentRom = "/home/casa/code/kotlin/chip8Archive/roms/skyward.ch8"
    var currentRom = "/home/casa/code/kotlin/chip8Archive/roms/t8nks.ch8"
    machine.loadRom(currentRom)

    // Setup UI
    val ui = UI()
    ui.addKeyListener(keyboard)
    val fc = JFileChooser().apply {
        font = Font("TimesRoman", Font.PLAIN, 100)
        currentDirectory = File("/home/casa/code/kotlin/chip8Archive/roms")
    }

    ui.addKeyListener(object: KeyAdapter() {
        override fun keyReleased(e: KeyEvent?) {
            when (e?.keyCode) {
                KeyEvent.VK_F1 -> {
                    machine.reset()
                    machine.loadRom(currentRom)
                }
                KeyEvent.VK_F2 -> {
                    if (fc.showOpenDialog(ui) == JFileChooser.APPROVE_OPTION) {
                        fc.selectedFile?.let{
                            machine.reset()
                            currentRom = it.absolutePath
                            machine.loadRom(currentRom)
                        }
                    }
                }
                KeyEvent.VK_F3 -> {
                    ui.persistenceDisplay.decay = PersistenceDisplay.NO_DECAY
                }
                KeyEvent.VK_F4 -> {
                    ui.persistenceDisplay.decay = PersistenceDisplay.DECAY
                }
                KeyEvent.VK_F5 -> {
                    ui.persistenceDisplay.decay = PersistenceDisplay.DECAY_LARGE
                }
                KeyEvent.VK_F10 -> {
                    cycles_per_frame /= 2
                    if (cycles_per_frame == 0) {
                        cycles_per_frame = 1
                    }
                }
                KeyEvent.VK_F11 -> {
                    cycles_per_frame = CYCLES_PER_FRAME_DEFAULT
                }
                KeyEvent.VK_F12 -> {
                    cycles_per_frame *= 2
                    if (cycles_per_frame > 1000 * CYCLES_PER_FRAME_DEFAULT) {
                        cycles_per_frame = 1000 * CYCLES_PER_FRAME_DEFAULT
                    }
                }
            }
        }

    })

    var lastTime = Instant.now()

    Timer(MS_PER_FRAME) {
        machine.tickTimer()
        repeat (cycles_per_frame) {
            machine.printStep()
            machine.tickCpu()
        }
        ui.persistenceDisplay.update(machine.display)
        ui.board.repaint()

        // Timing
        val newTime = Instant.now()
        val delta = Duration.between(lastTime, newTime)
        val freq = 1000 * cycles_per_frame / delta.toMillis()
        lastTime = newTime
        ui.title = "Chip8 @ ${freq}Hz"
    }.start()
}