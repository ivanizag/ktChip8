package org.izaguirre.chip8.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class Korax89Test {

    companion object {
        const val END_ADDRESS = 0x3dc
        const val TIMEOUT_CYCLES = 10000
        const val OK_IMAGE_ADDRESS = 0x202
        const val ERR_IMAGE_ADDRESS = 0x206
        const val TESTS_COUNT = 18
    }

    @Test
    fun run() {
        var machine = Machine()
        machine.loadRom("src/test/resources/corax89test/test_opcode.ch8")

        var okCount = 0
        var errCount = 0
        var cycles = 0
        while (machine.state.pc != END_ADDRESS && cycles < TIMEOUT_CYCLES ) {
            if ((machine.state.memWord(machine.state.pc) and 0xf000) == 0xd000 /*DRW*/) {
                // We will draw
                if (machine.state.i == OK_IMAGE_ADDRESS) okCount++
                if (machine.state.i == ERR_IMAGE_ADDRESS) errCount++
            }
            machine.step()
            cycles++
        }

        machine.display.printScreen()

        assert(cycles < TIMEOUT_CYCLES)
        assertEquals(okCount+errCount, TESTS_COUNT)
        assertEquals(errCount, 0)
    }
}