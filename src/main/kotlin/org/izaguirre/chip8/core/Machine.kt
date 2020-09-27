package org.izaguirre.chip8.core

import org.izaguirre.chip8.core.Display.Companion.FONT_ADDRESS
import org.izaguirre.chip8.core.Display.Companion.FONT_HEIGHT
import org.izaguirre.chip8.core.State.Companion.MEMORY_MASK
import org.izaguirre.chip8.core.State.Companion.VALUE_MASK
import java.io.File
import kotlin.random.Random

class Machine {
    val state = State()
    val display = Display(64, 32)
    var keypad: Keypad = DumbKeypad()

    fun loadRom(filename: String) {
        val f = File(filename)
        val data = f.readBytes()
        var address = 0x200
        for (b in data) {
            state.memSet(address, b.toInt())
            address++
        }
    }

    fun step() {
        // TechRef 2.5 Timers
        state.timerCycles++
        if (state.timerCycles == CYCLES_PER_TIMER) {
            state.timerCycles = 0
            if (state.dt > 0) state.dt--
            if (state.st > 0) state.st--
        }

        // TechRef 3.0
        val opcode = state.memWord(state.pc)
        val nnn = opcode and 0xfff
        val n = opcode and 0xf
        val x = opcode shr 8 and 0xf
        val y = opcode shr 4 and 0xf
        val kk = opcode and 0xff
        val c = opcode shr 12 and 0xf
        /* Opcodes can be cnnn, cxkk or cxyn */

        // TechRef 3.1
        state.skip()
        when (c) {
            0x0 -> when (nnn) {
                0x0e0 -> display.cls() // CLS
                0x0ee -> state.pop() // RET
                else -> throw Exception("SYS $opcode not supported") // SYS
            }
            0x1 -> state.jump(nnn) // JP addr
            0x2 -> { // CALL addr
                state.push()
                state.jump(nnn)
            }
            0x3 -> if (state.v[x] == kk) state.skip() // SE Vx, byte
            0x4 -> if (state.v[x] != kk) state.skip() // SNE Vx, byte
            0x5 -> if (state.v[x] == state.v[y]) state.skip() // SE Vx, Vy
            0x6 -> state.v[x] = kk // LD Vx, byte
            0x7 -> state.v[x] = (state.v[x] + kk) and VALUE_MASK // ADD Vx, byte
            0x8 -> when (n) {
                0x0 -> state.v[x] = state.v[y] // LD Vx, Vy
                0x1 -> state.v[x] = state.v[x] or state.v[y] // OR Vx, Vy
                0x2 -> state.v[x] = state.v[x] and state.v[y] // AND Vx, Vy
                0x3 -> state.v[x] = state.v[x] xor state.v[y] // XOR Vx, Vy
                0x4 -> { // ADD Vx, Vy
                    val r = state.v[x] + state.v[y]
                    state.v[x] = r and VALUE_MASK
                    state.v[0xf] = if (r > VALUE_MASK) 1 else 0
                }
                0x5 -> { // SUB Vx, Vy
                    val r = state.v[x] - state.v[y]
                    state.v[x] = r and VALUE_MASK
                    state.v[0xf] = if (r >= 0) 1 else 0
                }
                0x6 -> { // SHR Vx
                    state.v[0xf] = state.v[x] and 1
                    state.v[x] = state.v[x] shr 1
                }
                0x7 -> { // SUBN Vx, Vy
                    val r = state.v[y] - state.v[x]
                    state.v[x] = r and VALUE_MASK
                    state.v[0xf] = if (r >= 0) 1 else 0
                }
                0xe -> { // SHL Vx
                    state.v[0xf] = if (state.v[x] > 127) 1 else 0
                    state.v[x] = (state.v[x] shl 1) and VALUE_MASK
                }
                else -> throw Exception("Unknown opcode")
            }
            0x9 -> if (state.v[x] != state.v[y]) state.skip() // SNE Vx, Vy
            0xa -> state.i = nnn // LD I, addr
            0xb -> state.jump(nnn + state.v[0]) // JP V0, addr
            0xc -> state.v[x] = Random.nextInt() and kk // RND Vx, byte
            0xd -> state.v[0xf] = display.sprite(state, state.i, state.v[x], state.v[y], n) // DRW Vx, Vy, nibble
            0xe -> when (kk) {
                0x9e -> if (keypad.isKeyPressed(state.v[x])) state.skip() // SKP Vx
                0xa1 -> if (!keypad.isKeyPressed(state.v[x])) state.skip() // SKNP Vx
                else -> throw Exception("Unknown opcode")
            }
            0xf -> when (kk) {
                0x07 -> state.v[x] = state.dt // LD Vx, DT
                0x0a -> state.v[x] = keypad.nextKey() // LD Vx, K
                0x15 -> state.dt = state.v[x] // LD DT, Vx
                0x18 -> state.st = state.v[x] // LD ST, VX
                0x1e -> { // ADD I, Vx
                     val r = state.i + state.v[x]
                     state.i = r and MEMORY_MASK
                     state.v[0xf] = if (r > MEMORY_MASK) 1 else 0
                }
                0x29 -> state.i = (FONT_ADDRESS + state.v[x] * FONT_HEIGHT) and MEMORY_MASK // LD F, Vx
                0x33 -> { // LD B, Vx
                    var vx = state.v[x]
                    state.memSet(state.i + 2, vx.rem(10))
                    vx /= 10
                    state.memSet(state.i + 1, vx.rem(10))
                    vx /= 10
                    state.memSet(state.i, vx)
                }
                0x55 -> { // LD [I], VX
                    for (i in 0..x) {
                        state.memSet(state.i + i, state.v[i])
                    }
                }
                0x65 -> { // LD VX, [I]
                    for (i in 0..x) {
                        state.v[i] = state.memByte(state.i + i)
                    }
                }
                0x75 -> { // LD R, VX  (schip)
                    for (i in 0..(x and 7)) {
                        state.v48[i] = state.v[i]
                    }
                }
                0x85 -> { // LD VX, R  (schio)
                    for (i in 0..(x and 7)) {
                        state.v[i] = state.v48[i]
                    }
                }
                else -> throw Exception("Unknown opcode")
            }
            else -> throw Exception("Unknown opcode")
        }
    }

    fun printStep() {
        val opcode = state.memWord(state.pc)
        print("%03x: %04x   %-20s".format(state.pc, opcode, disasm(opcode)))
        for (i in 0..15) {
            print("%02x ".format(state.v[i]))
        }
        println("%03x".format(state.i))

    }

    private fun disasm(opcode: Int): String {

        val nnn = opcode and 0xfff
        val snnn = nnn.toString(16).toUpperCase()
        val n = opcode and 0xf
        val sn = n.toString(16).toUpperCase()
        val x = opcode shr 8 and 0xf
        val sx = x.toString(16).toUpperCase()
        val y = opcode shr 4 and 0xf
        val sy = y.toString(16).toUpperCase()
        val kk = opcode and 0xff
        val skk = kk.toString(16).toUpperCase()
        val c = opcode shr 12 and 0xf
        /* Opcodes can be cnnn, cxkk or cxyn */

        // TechRef 3.1
        return when (c) {
            0x0 -> when (nnn) {
                0x0e0 -> "CLS"
                0x0ee -> "RET"
                else -> "SYS $snnn"
            }
            0x1 -> "JP $snnn"
            0x2 -> "CALL $snnn"
            0x3 -> "SE V${sx}, $skk"
            0x4 -> "SNE V${sx}, $skk"
            0x5 -> "SE V${sx}, V${sy}"
            0x6 -> "LD V${sx}, $skk"
            0x7 -> "ADD V${sx}, $skk"
            0x8 -> when (n) {
                0x0 -> "LD V${sx}, V${sy}"
                0x1 -> "OR V${sx}, V${sy}"
                0x2 -> "AND V${sx}, V${sy}"
                0x3 -> "XOR V${sx}, V${sy}"
                0x4 -> "ADD V${sx}, V${sy}"
                0x5 -> "SUB V${sx}, V${sy}"
                0x6 -> "SHR V${sx}"
                0x7 -> "SUBN V${sx}, V${sy}"
                0xe -> "SHL V${sx}"
                else -> "??? V${sx}, V${sy}"
            }
            0x9 -> "SE V$sx, V$sy"
            0xa -> "LD I, $snnn"
            0xb -> "JP V0, $snnn"
            0xc -> "RND V${sx}, $skk"
            0xd -> "DRW V${sx}, V${sy}, $sn"
            0xe -> when (kk) {
                0x9e -> "SKP V${sx}"
                0xa1 -> "SKNP V${sx}"
                else -> "???"
            }
            0xf -> when (kk) {
                0x07 -> "LD V${sx}, DT"
                0x0a -> "LD V${sx}, K"
                0x15 -> "LD DT, V${sx}"
                0x18 -> "LD ST, V${sx}"
                0x1e -> "ADD I, V${sx}"
                0x29 -> "LD F, V${sx}"
                0x33 -> "LD B, V${sx}"
                0x55 -> "LD [I], V${sx}"
                0x65 -> "LD V${sx}, [I]"
                0x75 -> "LD R, V${sx}"
                0x85 -> "LD V${sx}, R"
                else -> "???"
            }
            else -> "???"
        }
    }

    companion object {
        const val CYCLE_MS = 2 // Chip8 runs at 500Hz, 2ms per cycle
        const val CYCLES_PER_TIMER = 8 // Should be 60Hz we approx to 62,5Hz=1/(2*8ms)
    }

}
