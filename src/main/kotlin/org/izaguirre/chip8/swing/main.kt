package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.*
import java.awt.Font
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
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
    var cyclesPerFrame = CYCLES_PER_FRAME_DEFAULT

    var currentRom: String? = null
    //var currentRom = "/home/casa/code/kotlin/schip-games/SGAMES/ANT.ch8"
    //machine.loadRom(currentRom)

    // Setup UI
    val ui = UI()
    ui.addKeyListener(keyboard)
    val fc = JFileChooser().apply {
        font = Font("TimesRoman", Font.PLAIN, 100)
        //currentDirectory = File("/home/casa/code/kotlin/chip8Archive/moreroms")
    }

    ui.addKeyListener(object: KeyAdapter() {
        override fun keyReleased(e: KeyEvent?) {
            when (e?.keyCode) {
                KeyEvent.VK_F1 -> {
                    machine.reset()
                    currentRom?.let {
                        machine.loadRom(it)
                    }
                }
                KeyEvent.VK_F2 -> {
                    if (fc.showOpenDialog(ui) == JFileChooser.APPROVE_OPTION) {
                        fc.selectedFile?.let{
                            machine.reset()
                            currentRom = it.absolutePath
                            machine.loadRom(it.absolutePath)
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
                    cyclesPerFrame /= 2
                    if (cyclesPerFrame == 0) {
                        cyclesPerFrame = 1
                    }
                }
                KeyEvent.VK_F11 -> {
                    cyclesPerFrame = CYCLES_PER_FRAME_DEFAULT
                }
                KeyEvent.VK_F12 -> {
                    cyclesPerFrame *= 2
                    if (cyclesPerFrame > 1000 * CYCLES_PER_FRAME_DEFAULT) {
                        cyclesPerFrame = 1000 * CYCLES_PER_FRAME_DEFAULT
                    }
                }
            }
        }

    })

    var lastTime = Instant.now()

    Timer(MS_PER_FRAME) {
        machine.tickTimer()
        repeat (cyclesPerFrame) {
            machine.printStep()
            machine.tickCpu()
        }
        ui.persistenceDisplay.update(machine.display)
        ui.board.repaint()

        // Timing
        val newTime = Instant.now()
        val delta = Duration.between(lastTime, newTime)
        val freq = 1000 * cyclesPerFrame / delta.toMillis()
        lastTime = newTime
        ui.title = "Chip8 @ ${freq}Hz"
    }.start()
}