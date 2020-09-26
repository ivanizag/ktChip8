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
        val state = State()
        val display = Display(64, 32)
        val keyboard = DumbKeyboard()
        loadRom(state, "src/test/resources/corax89test/test_opcode.ch8")
        state.memRangeSet(FONT, FONT_ADDRESS)

        var okCount = 0
        var errCount = 0
        var cycles = 0
        state.jump(PC_START)
        while (state.pc != END_ADDRESS && cycles < TIMEOUT_CYCLES ) {
            if ((state.memWord(state.pc) and 0xf000) == 0xd000 /*DRW*/) {
                // We will draw
                if (state.i == OK_IMAGE_ADDRESS) okCount++
                if (state.i == ERR_IMAGE_ADDRESS) errCount++
            }
            step(state, display, keyboard)
            cycles++
        }

        display.printScreen()

        assert(cycles < TIMEOUT_CYCLES)
        assertEquals(okCount+errCount, TESTS_COUNT)
        assertEquals(errCount, 0)
    }
}