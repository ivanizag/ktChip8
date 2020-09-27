package org.izaguirre.chip8.swing

import org.izaguirre.chip8.core.Keypad
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

class Keyboard : Keypad, KeyAdapter() {
    private val keyPressed = BooleanArray(KEY_COUNT)

    override fun keyPressed(e: KeyEvent?) {
        if (e != null) {
            val key = keyCodeToChip8(e.keyCode)
            if (key != null) {
                keyPressed[key] = true
            }
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        if (e != null) {
            val key = keyCodeToChip8(e.keyCode)
            if (key != null) {
                keyPressed[key] = false
            }
        }
    }

    private fun keyCodeToChip8(code: Int) = when (code) {
        // Chip 8 Reference 2.3
        KeyEvent.VK_1 -> 0x0
        KeyEvent.VK_2 -> 0x1
        KeyEvent.VK_3 -> 0x2
        KeyEvent.VK_4 -> 0xc

        KeyEvent.VK_Q -> 0x4
        KeyEvent.VK_W, KeyEvent.VK_UP -> 0x5
        KeyEvent.VK_E -> 0x6
        KeyEvent.VK_R -> 0xd

        KeyEvent.VK_A, KeyEvent.VK_LEFT -> 0x7
        KeyEvent.VK_S, KeyEvent.VK_DOWN -> 0x8
        KeyEvent.VK_D, KeyEvent.VK_RIGHT -> 0x9
        KeyEvent.VK_F -> 0xe

        KeyEvent.VK_Z -> 0xa
        KeyEvent.VK_X -> 0x0
        KeyEvent.VK_C -> 0xb
        KeyEvent.VK_V -> 0xf

        else -> null
    }

    override fun isKeyPressed(key: Int): Boolean {
        return keyPressed[key]
    }

    override fun nextKey(): Int {
        throw NotImplementedError()
    }

    companion object {
        const val KEY_COUNT = 16
    }

}