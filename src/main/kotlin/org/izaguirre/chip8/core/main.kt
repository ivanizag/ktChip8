package org.izaguirre.chip8.core

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
    val machine = Machine()
    machine.loadRom("src/test/resources/sctest/SCTEST.CH8")

    while (machine.state.pc != 0x450) {
        machine.printStep()
        machine.step()
    }
    machine.display.printScreen()
}
