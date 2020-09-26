// TechRef 2.4

interface Keyboard {
    fun isKeyPressed(key: Int): Boolean
    fun nextKey(): Int // Blocking

}

class DumbKeyboard: Keyboard {
    override fun isKeyPressed(key: Int) = false
    override fun nextKey() = 8
}