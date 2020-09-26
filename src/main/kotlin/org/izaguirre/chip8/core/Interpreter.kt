package org.izaguirre.chip8.core

import org.izaguirre.chip8.core.Display.Companion.FONT_ADDRESS
import org.izaguirre.chip8.core.Display.Companion.FONT_HEIGHT
import org.izaguirre.chip8.core.State.Companion.MEMORY_MASK
import org.izaguirre.chip8.core.State.Companion.VALUE_MASK
import kotlin.random.Random

fun mod(x: Int, y: Int) = Math.floorMod(x, y)

fun step(s: State, d: Display, k: Keypad) {
    // TechRef 3.0
    val opcode = s.memWord(s.pc)

    val nnn = opcode and 0xfff
    val n = opcode and 0xf
    val x = opcode shr 8 and 0xf
    val y = opcode shr 4 and 0xf
    val kk = opcode and 0xff
    val c = opcode shr 12 and 0xf
    /* Opcodes can be cnnn, cxkk or cxyn */

    s.skip()

    // TechRef 3.1
    when (c) {
        0x0 -> when (nnn) {
            0xe0 -> d.cls() // CLS
            0xee -> s.pop() // RET
            else -> throw Exception("SYS not supported") // SYS
        }
        0x1 -> s.jump(nnn) // JP addr
        0x2 -> { // CALL addr
            s.push()
            s.jump(nnn)
        }
        0x3 -> if (s.v[x] == kk) s.skip() // SE Vx, byte
        0x4 -> if (s.v[x] != kk) s.skip() // SNE Vx, byte
        0x5 -> if (s.v[x] == s.v[y]) s.skip() // SE Vx, Vy
        0x6 -> s.v[x] = kk // LD Vx, byte
        0x7 -> s.v[x] = (s.v[x] + kk) and VALUE_MASK // ADD Vx, byte
        0x8 -> when (n) {
            0x0 -> s.v[x] = s.v[y] // LD Vx, Vy
            0x1 -> s.v[x] = s.v[x] or s.v[y] // OR Vx, Vy
            0x2 -> s.v[x] = s.v[x] and s.v[y] // AND Vx, Vy
            0x3 -> s.v[x] = s.v[x] xor s.v[y] // XOR Vx, Vy
            0x4 -> { // ADD Vx, Vy
                val r = s.v[x] + s.v[y]
                s.v[x] = r and VALUE_MASK
                s.v[0xf] = if (r > VALUE_MASK) 1 else 0
            }
            0x5 -> { // SUB Vx, Vy
                val r = s.v[x] - s.v[y]
                s.v[x] = r and VALUE_MASK
                s.v[0xf] = if (r >= 0) 1 else 0
            }
            0x6 -> { // SHR Vx
                s.v[0xf] = s.v[x] and 1
                s.v[x] = s.v[x] shr 1
            }
            0x7 -> { // SUBN Vx, Vy
                val r = s.v[y] - s.v[x]
                s.v[x] = r and VALUE_MASK
                s.v[0xf] = if (r >= 0) 1 else 0
            }
            0xe -> { // SHL Vx
                s.v[0xf] = if (s.v[x] > 127) 1 else 0
                s.v[x] = (s.v[x] shl 1) and VALUE_MASK
            }
            else -> throw Exception("Unknown opcode")
        }
        0x9 -> if (s.v[x] != s.v[y]) s.skip() // SNE Vx, Vy
        0xa -> s.i = nnn // LD I, addr
        0xb -> s.jump(nnn + s.v[0]) // JP V0, addr
        0xc -> s.v[x] = Random.nextInt() and kk // RND Vx, byte
        0xd -> s.v[0xf] = d.sprite(s, s.i, s.v[x], s.v[y], n) // DRW Vx, Vy, nibble
        0xe -> when (kk) {
            0x9e -> if (k.isKeyPressed(s.v[x])) s.skip() // SKP Vx
            0xa1 -> if (!k.isKeyPressed(s.v[x])) s.skip() // SKNP Vx
            else -> throw Exception("Unknown opcode")
        }
        0xf -> when (kk) {
            0x07 -> s.v[x] = s.dt // LD Vx, DT
            0x0a -> s.v[x] = k.nextKey() // LD Vx, K
            0x15 -> s.dt = s.v[x] // LD DT, Vx
            0x18 -> s.st = s.v[x] // LD ST, VX
            0x1e -> { // ADD I, Vx
                 val r = s.i + s.v[x]
                 s.v[x] = r and MEMORY_MASK
                 s.v[0xf] = if (r > MEMORY_MASK) 1 else 0
            }
            0x29 -> s.i = (FONT_ADDRESS + s.v[x] * FONT_HEIGHT) and MEMORY_MASK // LD F, Vx
            0x33 -> { // LD B, Vx
                var vx = s.v[x]
                s.memSet(s.i + 2, vx.rem(10))
                vx /= 10
                s.memSet(s.i + 1, vx.rem(10))
                vx /= 10
                s.memSet(s.i, vx)
            }
            0x55 -> { // LD [I], VX
                for (i in 0..x) {
                    s.memSet(s.i + i, s.v[i])
                }
            }
            0x65 -> { // LD VX, [I]
                for (i in 0..x) {
                    s.v[i] = s.memByte(s.i + i)
                }
            }
            0x75 -> { // LD R, VX  (schip)
                for (i in 0..(x and 7)) {
                    s.v48[i] = s.v[i]
                }
            }
            0x85 -> { // LD VX, R  (schio)
                for (i in 0..(x and 7)) {
                    s.v[i] = s.v48[i]
                }
            }
            else -> throw Exception("Unknown opcode")
        }
        else -> throw Exception("Unknown opcode")
    }
}

fun printStep(s: State) {
    val opcode = s.memWord(s.pc)
    print("%03x: %04x   %-20s".format(s.pc, opcode, disasm(opcode)))
    for (i in 0..15) {
        print("%02x ".format(s.v[i]))
    }
    println("%03x".format(s.i))

}

fun disasm(opcode: Int): String {

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
            0xe0 -> "CLS"
            0xee -> "RET"
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
