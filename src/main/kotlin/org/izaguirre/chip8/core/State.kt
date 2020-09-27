package org.izaguirre.chip8.core

import org.izaguirre.chip8.core.Display.Companion.FONT
import org.izaguirre.chip8.core.Display.Companion.FONT_ADDRESS

class State {
    // TechRef 2.1 Memory
    private var mem = IntArray(MEMORY_SIZE)

    init {
        FONT.copyInto(mem, FONT_ADDRESS)
    }

    fun memSet(address: Int, value: Int) {
        mem[address and MEMORY_MASK] = value
    }

    fun memByte(address: Int) = mem[address and MEMORY_MASK] and VALUE_MASK
    fun memWord(address: Int) = memByte(address)*256 +
            memByte(address+1)

    // TechRef 2.2 Registers
    var v = IntArray(REGISTER_COUNT) // Must be 0 to 255
    var v48 = IntArray(REGISTER48_COUNT) // Must be 0 to 255
    var i = 0

    // TechRef 2.5 Timers
    var dt = 0
    var st = 0
    var timerCycles = 0

    var pc = PC_START
        private set
    fun jump(address: Int) {
        pc = address and MEMORY_MASK
    }
    fun skip() {
        jump(pc+2)
    }

    private var stack = IntArray(STACK_DEPTH)
    private var sp = 0
    fun push() {
        sp = Math.floorMod(sp + 1, STACK_DEPTH)
        stack[sp] = pc
    }
    fun pop() {
        pc = stack[sp]
        sp = Math.floorMod(sp - 1, STACK_DEPTH)
    }

    companion object {
        const val MEMORY_SIZE = 0x1000
        const val MEMORY_MASK = 0xfff
        const val VALUE_MASK = 0xff
        const val REGISTER_COUNT = 16
        const val REGISTER48_COUNT = 8
        const val STACK_DEPTH = 16
        const val PC_START = 0x200
    }
}