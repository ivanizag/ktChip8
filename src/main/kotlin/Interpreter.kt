import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor
import kotlin.random.Random

@ExperimentalUnsignedTypes
const val PC_START: UShort = 0x200u

fun mod(x: Int, y: Int) = Math.floorMod(x, y)

@ExperimentalUnsignedTypes
fun step(s: State, d: Display, k: Keyboard) {
    // TechRef 3.0
    val opcode = s.memWord(s.pc)

    val nnn = (opcode and 0xfffu).toUShort()
    val n = (opcode and 0xfu).toUByte()
    val x = (opcode shr 8 and 0xfu).toInt()
    val y = (opcode shr 4 and 0xfu).toInt()
    val kk = (opcode shr 8 and 0xffu).toUByte()
    val c = opcode shr 12 and 0xfu
    /* Opcodes can be cnnn, cxkk or cxyn */

    println("%03x: %04x".format(s.pc.toInt(), opcode.toInt()))

    s.skip()

    // TechRef 3.1
    when (c.toInt()) {
        0x0 -> when (nnn.toInt()) {
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
        0x7 -> s.v[x] = (s.v[x] + kk).toUByte() // ADD Vx, byte
        0x8 -> when (n.toInt()) {
            0x0 -> s.v[x] = s.v[y] // LD Vx, Vy
            0x1 -> s.v[x] = s.v[x] or (s.v[y]) // OR Vx, Vy
            0x2 -> s.v[x] = s.v[x] and s.v[y] // AND Vx, Vy
            0x3 -> s.v[x] = s.v[x] xor s.v[y] // XOR Vx, Vy
            0x4 -> { // ADD Vx, Vy
                val r = s.v[x] + s.v[y]
                s.v[x] = r.toUByte()
                s.v[0xf] = (r shr 8).toUByte() // As in Vision8
            }
            0x5 -> { // SUB Vx, Vy
                val r = s.v[x] - s.v[y]
                s.v[x] = r.toUByte()
                s.v[0xf] = ((r shr 8)  + 1u).toUByte() // As in Vision8
            }
            0x6 -> { // SHR Vx
                s.v[0xf] = s.v[x] and 1u
                s.v[x] = (s.v[x].toUInt() shr 1).toUByte()
            }
            0x7 -> { // SUBN Vx, Vy
                val r = s.v[y] - s.v[x]
                s.v[x] = r.toUByte()
                s.v[0xf] = ((r shr 8) + 1u).toUByte() // As in Vision8
            }
            0xe -> { // SHL Vx
                s.v[0xf] = (s.v[x].toUInt() shr 7).toUByte()
                s.v[x] = (s.v[x].toUInt() shl 1).toUByte()
            }
            else -> println("Unknown opcode")
        }
        0x9 -> if (s.v[x] != s.v[y]) s.skip() // SNE Vx, Vy
        0xa -> s.i = nnn // LD I, addr
        0xb -> s.jump((nnn + s.v[0]).toUShort()) // JP V0, addr
        0xc -> s.v[x] = Random.nextInt().toUByte() and kk // RND Vx, byte
        0xd -> s.v[0xf] = d.sprite(s, s.i, x, y, n.toInt()) // DRW Vx, Vy, nibble
        0xe -> when (kk.toInt()) {
            0x9e -> if (k.isKeyPressed(s.v[x])) s.skip() // SKP Vx
            0xa1 -> if (!k.isKeyPressed(s.v[x])) s.skip() // SKNP Vx
            else -> println("Unknown opcode")
        }
        0xf -> when (kk.toInt()) {
            0x07 -> s.v[x] = s.dt // LD Vx, DT
            0x0a -> s.v[x] = k.nextKey() // LD Vx, K
            0x15 -> s.dt = s.v[x] // LD DT, Vx
            0x18 -> s.st = s.v[x] // LD ST, VX
            0x1e -> s.i = (s.i + s.v[x]).toUShort() // ADD I, Vx
            0x29 -> s.i = (s.v[x] * FONT_HEIGHT).toUShort() // LD F, Vx
            0x33 -> { // LD B, Vx
                var vx = s.v[x].toUInt()
                s.memSet(s.i+2u, (vx.rem(10u)).toUByte())
                vx = vx / 10u
                s.memSet(s.i+1u, (vx.rem(10u)).toUByte())
                vx = vx / 10u
                s.memSet(s.i, vx.toUByte())
            }
            0x55 -> { // LD [I], VX
                for (i in 0..x) {
                    s.memSet(s.i + i.toUInt(), s.v[i])
                }
            }
            0x65 -> { // LD VX, [I]
                for (i in 0..x) {
                    s.v[x] = s.memByte(s.i + i.toUInt())
                }
            }
        }
        else -> println("Unknown opcode")
    }
}