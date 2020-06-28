package org.divy.ai.snake.model.engine.qlearning.qnetwork

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.float
import com.github.ajalt.clikt.parameters.types.int
import javafx.animation.AnimationTimer
import org.divy.ai.snake.animation.game.GameBoardAnimationFactoryImpl
import org.divy.ai.snake.application.SnakeGameScreenImpl
import org.divy.ai.snake.model.engine.qlearning.AbstractLearningCommand
import org.divy.ai.snake.model.engine.qlearning.EpisodeCompleted
import org.divy.ai.snake.model.engine.qlearning.PreviousScoreReward
import org.divy.ai.snake.model.engine.qlearning.animation.GameBoardTableDirectionIndicatorFactory
import org.divy.ai.snake.model.engine.qlearning.modifiable
import org.divy.ai.snake.model.engine.qlearning.qnetwork.dqn.DoubleQNAlgorithm
import org.divy.ai.snake.model.food.FoodEvent
import org.divy.ai.snake.model.food.RandomFoodDropper
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType
import org.divy.ai.snake.model.game.GameBoardModel
import org.divy.ai.snake.model.game.GameEventListener
import org.divy.ai.snake.model.snake.SnakeModel
import java.util.stream.IntStream

class SnakeBoardRlQNeuralNetworkCommand : AbstractLearningCommand(help="Start Snake Board RL Training using Neural Network") {

    private var experienceBufferSize by option(help = "Buffer size for experience learning").int().default(1000)
        .validate {
            require(it > 0) {
                "Buffer size for experience learning should be positive"
            }
        }.modifiable()

    private var randomDecay by option(help = "Random Decay rate").float().default(0.9999f)
        .validate {
            require(it > 0 && it <= 1) {
                "Random Decay rate for experience learning should be positive and less then equal to 1"
            }
        }.modifiable()

    private val doubleQN by option(help = "Should two network be used").flag()

    private lateinit var qNeuralNetworkBrain: QNeuralNetworkBrain

    private lateinit var qNAlgorithm: QNAlgorithm

    override fun run() {

        val randomActionProvider = RandomActionProvider(randomFactor)
        qNAlgorithm =  if(doubleQN) {
            DoubleQNAlgorithm(
                learningRate = learningRate.toDouble()
                , gamaRate = gamaRate
                , experienceBufferSize = experienceBufferSize
                , randomActionProvider = randomActionProvider
                , rewardCalculator = PreviousScoreReward())

        } else {
            SingleQNAlgorithm(
                learningRate = learningRate.toDouble()
                , gamaRate = gamaRate
                , experienceBufferSize = experienceBufferSize
                , randomActionProvider = randomActionProvider
                , rewardCalculator = PreviousScoreReward()
            )
        }



        qNeuralNetworkBrain = QNeuralNetworkBrain(rlAlgorithm = qNAlgorithm)
        environment = createBoard()
        environment.addEventListener(EventType.EPISODE_COMPLETED, randomActionProvider)
        start()
    }

    private fun start() {

        environment.addEventListener(EventType.FOOD_EATEN, object :
            GameEventListener {
            override fun handleEvent(event: Event) {
                if (event is FoodEvent && EventType.FOOD_EATEN == event.type) {
                    for (food in environment.internalFoodList) {
                        if (food.pos == event.position) {
                            environment.internalFoodList.remove(food)
                            break
                        }
                    }
                    updateFoodInBoard(environment)
                }
            }
        })

        this.episode = 1

        val animator: AnimationTimer = object : AnimationTimer() {
            override fun handle(arg0: Long) {
                runTraining()
            }
        }
        val snakeGameScreen = SnakeGameScreenImpl(
            this, arrayOf(
                GameBoardAnimationFactoryImpl(), GameBoardTableDirectionIndicatorFactory(qNAlgorithm)
            )
        )
        snakeGameScreen.startGame()
        animator.start()
    }

    private fun runTraining() {
        if (this.episode <= numEpisodes) {
            if (!environment.hasLiveSnake()) {
                environment.raiseEvent(
                    EpisodeCompleted(
                        episode
                    )
                )
                startNewEpisode()
            }
            environment.snakes.forEach { it.thinkAndMove() }
            environment.updateScores()
        }
    }

    private fun startNewEpisode() {
        environment.snakes.clear()

        episode++

        val snake = SnakeModel(board = environment, brain = qNeuralNetworkBrain)

        snake.addEventListener(EventType.SNAKE_MOVE_COMPLETED, qNAlgorithm)

        environment.internalFoodList.clear()
        updateFoodInBoard(environment)

        environment.addSnake(snake)
    }

    private fun createBoard(): GameBoardModel {
        val gameBoardModel = GameBoardModel(
            boardWidth = boardWidth.toLong(),
            boardHeight = boardHeight.toLong()
        )
        foodDropper = RandomFoodDropper(gameBoardModel)
        gameBoardModel.foodDropper = foodDropper
        updateFoodInBoard(gameBoardModel)
        return gameBoardModel
    }

    private fun updateFoodInBoard(gameBoardModel: GameBoardModel) {
        for (index in IntStream.range(
            0,
            numFoodDrops - gameBoardModel.internalFoodList.size
        ))
            foodDropper.drop()?.let { gameBoardModel.addFood(it) }
    }
}
