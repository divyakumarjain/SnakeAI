package org.divy.ai.snake.application

import com.github.ajalt.clikt.core.subcommands
import javafx.application.Application
import javafx.application.Application.launch
import javafx.stage.Stage
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

