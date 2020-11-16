package org.divy.ai.snake.application

import com.github.ajalt.clikt.core.subcommands
import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.divy.ai.snake.animation.chart.ProgressChartController
import org.divy.ai.snake.animation.game.GameBoardAnimationFactory
import org.divy.ai.snake.animation.game.GameScreenController
import org.divy.ai.snake.application.command.AbstractLearningCommand
import org.divy.ai.snake.application.command.SnakeBoardRlCommand
import org.divy.ai.snake.application.command.qnetwork.SnakeBoardRlQNeuralNetworkCommand
import org.divy.ai.snake.application.command.table.SnakeBoardRlQTableCommand

class SnakeRLApplication: Application() {
    private val rlTableCommand =
        SnakeBoardRlQTableCommand()
    private val rlqNeuralNetworkCommand =
        SnakeBoardRlQNeuralNetworkCommand()
    private val rlCommand = SnakeBoardRlCommand()

    private lateinit var stage: Stage

    override fun start(theStage: Stage) {
        this.stage = theStage
        rlCommand.subcommands(rlTableCommand, rlqNeuralNetworkCommand).main(parameters.raw)
    }
}

fun main(args: Array<String>) = launch(SnakeRLApplication::class.java, *args)

class SnakeGameScreenImpl(private val rlCommand: AbstractLearningCommand, private val gameBoardAnimationFactory: Array<GameBoardAnimationFactory>) {

    fun loadProgressChart(): Parent {
        val fxmlLoader = FXMLLoader(SnakeAIApplication::class.java.classLoader.getResource("progressChart.fxml"))
        fxmlLoader.setController(ProgressChartController(rlCommand.environment))

        return fxmlLoader.load()
    }

    fun startGame() {

        val theStage = Stage()

        theStage.title = "Snake Game"

        val gameScreenFxmlLoader = FXMLLoader(SnakeAIApplication::class.java.classLoader.getResource("GameScreen.fxml"))
        gameScreenFxmlLoader.setController(GameScreenController(rlCommand))

        val bPane = gameScreenFxmlLoader.load() as BorderPane
        val theScene = Scene(bPane)
        theStage.scene = theScene

        val boardCanvas = bPane.center as Canvas

        boardCanvas.isFocusTraversable = true

        boardCanvas.width = (rlCommand.cellResolution * rlCommand.boardWidth)
        boardCanvas.height = (rlCommand.cellResolution * rlCommand.boardHeight)

        boardCanvas.addEventFilter(MouseEvent.ANY) { boardCanvas.requestFocus() }

        ((bPane.bottom as AnchorPane).children[0] as ScrollPane).content = loadProgressChart()

        gameBoardAnimationFactory.forEach { it.buildAnimationTimer(boardCanvas, rlCommand).start() }

        theStage.show()

    }

}
