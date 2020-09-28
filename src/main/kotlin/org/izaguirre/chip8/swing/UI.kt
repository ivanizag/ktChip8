package org.izaguirre.chip8.swing

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JToolBar
import kotlin.system.exitProcess

class UI: JFrame() {
    val persistenceDisplay = PersistenceDisplay()
    val board = Board(persistenceDisplay)

    init {
        title = "Chip8"
        board.background = Color(100, 100, 100)
        add(board)
        //add(createToolBar(), BorderLayout.NORTH)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(64 * 20 + 30, 32 * 20 + 100)
        setLocationRelativeTo(null)
        isVisible = true
    }

    private fun createToolBar(): JToolBar {
        val toolBar = JToolBar()
        val loadButton = JButton("Load")
        toolBar.add(loadButton)

        val exitButton = JButton("Exit")
        toolBar.add(exitButton)
        exitButton.addActionListener {
            exitProcess(0)
        }

        return toolBar
    }

}