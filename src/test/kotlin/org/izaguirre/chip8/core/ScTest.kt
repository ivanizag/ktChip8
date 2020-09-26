package org.izaguirre.chip8.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class ScTest {

    companion object {
        const val SUCCESS_ADDRESS = 0x3c4
        const val END_ADDRESS = 0x450
        const val BCD_ERROR_ADDRESS = 0x452
        const val NO_ERROR_CODE = 123
        const val TIMEOUT_CYCLES = 10000
    }

    @Test
    fun run() {
        val state = State()
        val display = Display(64, 32)
        val keyboard = DumbKeypad()
        state.loadRom("src/test/resources/sctest/SCTEST.CH8")

        var success = false
        var cycles = 0
        while (state.pc != END_ADDRESS && cycles < TIMEOUT_CYCLES ) {
            step(state, display, keyboard)
            if (state.pc == SUCCESS_ADDRESS) {
                success = true
            }
            cycles++
        }

        display.printScreen()

        assert(cycles < TIMEOUT_CYCLES)
        val error = state.memByte(BCD_ERROR_ADDRESS) * 100 +
                state.memByte(BCD_ERROR_ADDRESS +1) * 10 +
                state.memByte(BCD_ERROR_ADDRESS +2) * 1
        assertEquals(error, NO_ERROR_CODE)
        //assertEquals(error, 0)
        assert(success)
    }
}