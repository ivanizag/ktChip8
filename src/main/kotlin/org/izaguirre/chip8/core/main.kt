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
    val keyboard = DumbKeyboard()
    loadRom(state, "src/test/resources/sctest/SCTEST.CH8")
    state.memRangeSet(FONT, FONT_ADDRESS)

    state.jump(PC_START)
    while (state.pc != 0x450) {
        step(state, display, keyboard)
    }
    display.printScreen()
}

fun loadRom(state: State, filename: String) {
    val f = File(filename)
    val data = f.readBytes()
    var address = 0x200
    for (b in data) {
        state.memSet(address, b.toInt())
        address++
    }
}
