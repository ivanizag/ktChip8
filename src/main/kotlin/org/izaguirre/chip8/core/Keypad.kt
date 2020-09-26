package org.izaguirre.chip8.core

interface Keypad {
    // TechRef 2.4
    fun isKeyPressed(key: Int): Boolean
    fun nextKey(): Int // Blocking, see https://retrocomputing.stackexchange.com/questions/358/how-are-held-down-keys-handled-in-chip-8

}

class DumbKeypad: Keypad {
    override fun isKeyPressed(key: Int) = false
    override fun nextKey() = 8
}