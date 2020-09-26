package org.izaguirre.chip8.core

import java.io.File

/*
See:
    http://devernay.free.fr/hacks/chip8/C8TECH10.HTM
    http://devernay.free.fr/hacks/chip8/schip.txt
    https://github.com/mattmikolay/chip-8/wiki/CHIP%E2%80%908-Technical-Reference
    http://www.komkon.org/~dekogel/vision8.html

Games:
      https://github.com/JohnEarnest/chip8Archive/tree/master/roms

 */



fun main() {

    val state = State()
    val display = Display(64, 32)
    val keypad = DumbKeypad()
    state.loadRom("src/test/resources/sctest/SCTEST.CH8")

    while (state.pc != 0x450) {
        printStep(state)
        step(state, display, keypad)
    }
    display.printScreen()
}
