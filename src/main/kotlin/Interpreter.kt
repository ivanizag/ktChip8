import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor
import kotlin.random.Random

@ExperimentalUnsignedTypes
const val PC_START = 0x200

fun mod(x: Int, y: Int) = Math.floorMod(x, y)

@ExperimentalUnsignedTypes
fun step(s: State, d: Display, k: Keyboard) {
    // TechRef 3.0
    val opcode = s.memWord(s.pc)

    val nnn = opcode and 0xfff
    val n = opcode and 0xf
    val x = opcode shr 8 and 0xf
    val y = opcode shr 4 and 0xf
    val kk = opcode and 0xff
    val c = opcode shr 12 and 0xf
    /* Opcodes can be cnnn, cxkk or cxyn */

    println("%03x: %04x".format(s.pc, opcode))

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
        0x7 -> s.v[x] = (s.v[x] + kk) and 0xff // ADD Vx, byte
        0x8 -> when (n) {
            0x0 -> s.v[x] = s.v[y] // LD Vx, Vy
            0x1 -> s.v[x] = s.v[x] or s.v[y] // OR Vx, Vy
            0x2 -> s.v[x] = s.v[x] and s.v[y] // AND Vx, Vy
            0x3 -> s.v[x] = s.v[x] xor s.v[y] // XOR Vx, Vy
            0x4 -> { // ADD Vx, Vy
                val r = s.v[x] + s.v[y]
                s.v[x] = r and 0xff
                s.v[0xf] = if (r > 255) 1 else 0
            }
            0x5 -> { // SUB Vx, Vy
                val r = s.v[x] - s.v[y]
                s.v[x] = r and 0xff
                s.v[0xf] = if (r < 0) 1 else 0
            }
            0x6 -> { // SHR Vx
                s.v[0xf] = s.v[x] and 1
                s.v[x] = s.v[x] shr 1
            }
            0x7 -> { // SUBN Vx, Vy
                val r = s.v[y] - s.v[x]
                s.v[x] = r and 0xff
                s.v[0xf] = if (r < 0) 1 else 0
            }
            0xe -> { // SHL Vx
                s.v[0xf] = if (s.v[x] > 127) 1 else 0
                s.v[x] = (s.v[x] shl 1) and 0xff
            }
            else -> println("Unknown opcode")
        }
        0x9 -> if (s.v[x] != s.v[y]) s.skip() // SNE Vx, Vy
        0xa -> s.i = nnn // LD I, addr
        0xb -> s.jump(nnn + s.v[0]) // JP V0, addr
        0xc -> s.v[x] = Random.nextInt() and kk // RND Vx, byte
        0xd -> s.v[0xf] = d.sprite(s, s.i, x, y, n) // DRW Vx, Vy, nibble
        0xe -> when (kk) {
            0x9e -> if (k.isKeyPressed(s.v[x])) s.skip() // SKP Vx
            0xa1 -> if (!k.isKeyPressed(s.v[x])) s.skip() // SKNP Vx
            else -> println("Unknown opcode")
        }
        0xf -> when (kk) {
            0x07 -> s.v[x] = s.dt // LD Vx, DT
            0x0a -> s.v[x] = k.nextKey() // LD Vx, K
            0x15 -> s.dt = s.v[x] // LD DT, Vx
            0x18 -> s.st = s.v[x] // LD ST, VX
            0x1e -> s.i = (s.i + s.v[x]) and MEMORY_MASK // ADD I, Vx
            0x29 -> s.i = (FONT_ADDRESS + s.v[x] * FONT_HEIGHT) and MEMORY_MASK // LD F, Vx
            0x33 -> { // LD B, Vx
                var vx = s.v[x]
                s.memSet(s.i+2, vx.rem(10))
                vx /= 10
                s.memSet(s.i+1, vx.rem(10))
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
        }
        else -> println("Unknown opcode")
    }
}