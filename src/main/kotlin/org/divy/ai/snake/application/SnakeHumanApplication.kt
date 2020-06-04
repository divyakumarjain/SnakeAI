package org.divy.ai.snake.application

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import org.divy.ai.snake.animation.GameBoardAnimationTimer
import org.divy.ai.snake.model.game.EventType
import org.divy.ai.snake.model.game.HumanGameBoardModel
import org.divy.ai.snake.model.game.events.NavigationEvent


class SnakeHumanApplication : Application() {
    private val boardWidth = 20L
    private val boardHeight = 40L
    private val cellResolution = 15.0

    private val canvasWidth = boardWidth * cellResolution
    private val canvasHeight = boardHeight * cellResolution

    override fun start(theStage: Stage) {
        theStage.title = "Snake Game"

        val root = Group()
        val theScene = Scene(root)
        theStage.scene = theScene

        val boardCanvas = Canvas(canvasWidth, canvasHeight)
        root.children.add(boardCanvas)

        val gameModel = HumanGameBoardModel(boardWidth, boardHeight)

        gameModel.start()
        boardCanvas.onKeyPressed = EventHandler { keyEvent ->
            if (keyEvent.code.isArrowKey) {
                gameModel.raiseEvent(NavigationEvent(EventType.NAVIGATION, keyEvent))
                keyEvent.consume()
            }
        }

        boardCanvas.isFocusTraversable = true
        boardCanvas.addEventFilter(MouseEvent.ANY) { boardCanvas.requestFocus() }

        GameBoardAnimationTimer(boardCanvas.graphicsContext2D, gameModel, cellResolution).start()
        theStage.show()

    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(SnakeHumanApplication::class.java, *args)
        }
    }
}
