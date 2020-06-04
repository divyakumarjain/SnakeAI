package org.divy.ai.snake.application

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import org.divy.ai.snake.animation.GameBoardAnimationTimer
import org.divy.ai.snake.animation.chart.ProgressChartController
import org.divy.ai.snake.model.game.AIGameBoardModel


class SnakeAIApplication : Application() {
    private val boardWidth = 50L
    private val boardHeight = 50L
    private val cellResolution = 15.0

    private val canvasWidth = boardWidth * cellResolution
    private val canvasHeight = boardHeight * cellResolution

    override fun start(theStage: Stage) {
        val gameModel = startGame(theStage)
        showProgressGraph(gameModel)
    }

    private fun showProgressGraph(gameModel: AIGameBoardModel) {

        val fxmlLoader = FXMLLoader(SnakeAIApplication::class.java.classLoader.getResource("progressChart.fxml"))
        fxmlLoader.setController(ProgressChartController(gameModel))
        val root: Parent = fxmlLoader.load()

        val stage = Stage()
        stage.title = "Progress"
        stage.scene = Scene(root, 450.0, 450.0)
        stage.show()
    }

    private fun startGame(theStage: Stage): AIGameBoardModel {
        theStage.title = "Snake Game"

        val root = Group()
        val theScene = Scene(root)
        theStage.scene = theScene

        val scrollPane = ScrollPane()
        scrollPane.isFitToHeight = true
        root.children.add(scrollPane)

        val boardCanvas = Canvas(canvasWidth, canvasHeight)
        scrollPane.content = boardCanvas

        val gameModel = AIGameBoardModel(boardWidth, boardHeight, ArrayList())

        gameModel.start()

        boardCanvas.isFocusTraversable = true
        boardCanvas.addEventFilter(MouseEvent.ANY) { boardCanvas.requestFocus() }

        GameBoardAnimationTimer(boardCanvas.graphicsContext2D, gameModel, cellResolution).start()
        theStage.show()

        return gameModel
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(SnakeAIApplication::class.java, *args)
        }
    }
}
