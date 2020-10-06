package org.izaguirre.chip8.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class BcTest {

    companion object {
        const val ERROR_ADDRESS = 0x310
        const val ERROR_REG_10 = 0x3
        const val ERROR_REG_1 = 0x4
        const val END_ADDRESS = 0x30e
        const val TIMEOUT_CYCLES = 10000
    }

    @Test
    fun run() {
        val machine = Machine()
        machine.loadRom("src/test/resources/bctest/BC_test.ch8")

        var success = false
        var error: Int? = null
        var cycles = 0
        while (machine.state.pc != END_ADDRESS && cycles < TIMEOUT_CYCLES ) {
            machine.tickCpu()
            machine.printStep()
            if (machine.state.pc == ERROR_ADDRESS) {
                error = machine.state.v[ERROR_REG_10] * 10 + machine.state.v[ERROR_REG_1]
            }
            cycles++
        }

        machine.display.printScreen()

        assert(cycles < TIMEOUT_CYCLES)
        assertEquals(null, error)
    }
}