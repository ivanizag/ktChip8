package org.izaguirre.chip8.core

class State {
    // TechRef 2.1 Memory
    private var mem = IntArray(MEMORY_SIZE_OCTO)

    fun memSet(address: Int, value: Int) {
        mem[address and MEMORY_MASK] = value
    }
    fun memCopy(data: IntArray, address: Int) {
        data.copyInto(mem, address)
    }

    fun memByte(address: Int) = mem[address and MEMORY_MASK] and VALUE_MASK
    fun memWord(address: Int) = memByte(address)*256 +
            memByte(address+1)

    // TechRef 2.2 Registers
    var v = IntArray(REGISTER_COUNT) // Must be 0 to 255
    var v48 = IntArray(REGISTER48_COUNT) // Must be 0 to 255
    var i = 0
        set(value) {
            field = value and if (longI) MEMORY_MASK_OCTO else MEMORY_MASK
        }
    var longI = false // Support 16 bit i, instead of 12

    // TechRef 2.5 Timers
    var dt = 0
    var st = 0

    var pc = PC_START
        private set
    fun jump(address: Int) {
        pc = address and MEMORY_MASK
    }
    fun skip() {
        jump(pc+2)
    }
    fun unskip() {
        jump(pc-2)
    }
    fun skipConditional(condition: Boolean) {
        if (condition) {
            // If there is an Octo load address, jump over it
            if (memWord(pc) == 0xf000) skip()
            skip()
        }
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
        const val MEMORY_SIZE_OCTO = 0x10000
        const val MEMORY_MASK = 0xfff
        const val MEMORY_MASK_OCTO = 0xffff
        const val VALUE_MASK = 0xff
        const val REGISTER_COUNT = 16
        const val REGISTER48_COUNT = 8
        const val STACK_DEPTH = 16 // 12 in CHIP8, 16 in SCHIP
        const val PC_START = 0x200
    }
}