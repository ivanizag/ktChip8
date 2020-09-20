const val MEMORY_SIZE = 0x1000
@ExperimentalUnsignedTypes
const val MEMORY_MASK: UShort = 0xfffu
const val REGISTER_COUNT = 16
const val STACK_DEPTH = 16

@ExperimentalUnsignedTypes
class State {

    // TechRef 2.1 Memory
    private var mem = UByteArray(MEMORY_SIZE)

    fun memSet(address: UShort, value: UByte) {
        mem[(address and MEMORY_MASK).toInt()] = value
    }
    fun memSet(address: UInt, value: UByte) { memSet(address.toUShort(), value)}

    fun memByte(address: UShort) = mem[(address and MEMORY_MASK).toInt()]
    fun memByte(address: UInt) = memByte(address.toUShort())
    fun memWord(address: UShort) = memByte(address).toUShort()*256u +
            memByte((address+1u).toUShort()).toUShort()

    // TechRef 2.2 Registers
    var v = UByteArray(REGISTER_COUNT)
    var i: UShort = 0u
    var dt: UByte = 0u
    var st: UByte = 0u

    var pc: UShort = 0u
        private set
    fun jump(address: UShort) {
        pc = address and MEMORY_MASK
    }
    fun skip() {
        jump((pc+2u).toUShort())
    }

    private var stack = UShortArray(STACK_DEPTH)
    private var sp = 0
    fun push() {
        sp = mod(sp + 1, STACK_DEPTH)
        stack[sp] = pc
    }
    fun pop() {
        pc = stack[sp]
        sp = mod(sp - 1, STACK_DEPTH)
    }

}