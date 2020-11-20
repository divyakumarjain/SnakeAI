package org.divy.ai.snake.application.command.qnetwork

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.int
import javafx.animation.AnimationTimer
import org.divy.ai.snake.animation.game.GameBoardAnimationFactoryImpl
import org.divy.ai.snake.animation.game.SnakeGameScreenImpl
import org.divy.ai.snake.application.command.AbstractLearningCommand
import org.divy.ai.snake.model.engine.qlearning.EpisodeCompleted
import org.divy.ai.snake.model.engine.qlearning.StepRewardCalculator
import org.divy.ai.snake.animation.GameBoardTableDirectionIndicatorFactory
import org.divy.ai.snake.model.engine.qlearning.modifiable
import org.divy.ai.snake.model.engine.qlearning.qnetwork.*
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.ConvolutionNetworkBuilder
import org.divy.ai.snake.model.engine.qlearning.qnetwork.dqn.DoubleQNAlgorithm
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.DefaultNetworkBuilder
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.NetworkBuilder
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.SavedNetworkBuilder
import org.divy.ai.snake.model.food.FoodEvent
import org.divy.ai.snake.model.food.RandomFoodDropper
import org.divy.ai.snake.model.game.*
import org.divy.ai.snake.model.snake.*
import org.divy.ai.snake.model.snake.event.SnakeDeadEvent
import org.divy.ai.snake.model.snake.event.SnakeDeathType
import java.util.stream.IntStream

class SnakeBoardRlQNeuralNetworkCommand : AbstractLearningCommand(help="Start Snake Board RL Training using Neural Network") {

    private var experienceBufferSize by option(help = "Buffer size for experience learning").int().default(1000)
        .validate {
            require(it > 0) {
                "Buffer size for experience learning should be positive"
            }
        }.modifiable()

    private var useSavedModel by option(help = "use saved model").modifiable()

    private val doubleQN by option(help = "Should two network be used").flag()

    private lateinit var qNeuralNetworkBrain: QNeuralNetworkBrain

    private lateinit var qNAlgorithm: QNAlgorithm

    private object Statistics: GameEventListener {
        var wallCollisionCount: Double = 0.0
        var bodyCollisionCount: Double = 0.0
        var starvedCount: Double = 0.0
        init {
            EventRegistry.instance.addEventListener(EventType.SNAKE_DEAD, this)
        }
        override fun handleEvent(event: Event) {
            if(event is SnakeDeadEvent) {
                when(event.deathType) {
                    SnakeDeathType.BODY_COLLIDE ->  bodyCollisionCount++
                    SnakeDeathType.STARVED -> starvedCount++
                    SnakeDeathType.WALL_COLLIDE -> wallCollisionCount++
                }
            }
        }
    }

    override fun run() {

        val randomActionProvider =
            RandomActionProvider(
                randomFactor = randomFactor,
                randomDecay = randomDecay
            )
        qNAlgorithm =  if(doubleQN) {
            DoubleQNAlgorithm(
                learningRate = learningRate.toDouble()
                , gamaRate = gamaRate
                , experienceBufferSize = experienceBufferSize
                , randomActionProvider = randomActionProvider
                , networkBuilder = identifyNetworkBuilder()
                , rewardCalculator = StepRewardCalculator())

        } else {
            SingleQNAlgorithm(
                learningRate = learningRate.toDouble()
                , gamaRate = gamaRate
                , experienceBufferSize = experienceBufferSize
                , randomActionProvider = randomActionProvider
                , networkBuilder = identifyNetworkBuilder()
                , rewardCalculator = StepRewardCalculator()
            )
        }



        qNeuralNetworkBrain =
            QNeuralNetworkBrain(rlAlgorithm = qNAlgorithm)
        environment = createBoard()
        environment.addEventListener(EventType.EPISODE_COMPLETED, randomActionProvider)
        start()
    }

    private fun identifyNetworkBuilder() : NetworkBuilder {

        if(useSavedModel!=null && "" != useSavedModel)
            return SavedNetworkBuilder(useSavedModel!!)

        return when {
            this.useFourDirection  || this.useEightDirection-> {
                DefaultNetworkBuilder(
                    calculateObservationSize()
                )
            }
            else -> {
                ConvolutionNetworkBuilder(
                    this.boardHeight.toLong(),
                    this.boardWidth.toLong()
                )
            }
        }

    }

    private fun generateVision(): SnakeVision {
        return when {
            this.useFourDirection -> FourDirectionSnakeVision(null, environment)
            this.useEightDirection -> EightDirectionSnakeVision(null, environment)
            else -> {
                BoardSnakeVision(environment)
            }
        }
    }

    private fun calculateObservationSize(): Int {
        return when {
            this.useFourDirection -> 4 * OBJECT_TYPE_COUNT
            this.useEightDirection -> 8 * OBJECT_TYPE_COUNT
            else -> this.boardWidth * this.boardHeight * (OBJECT_TYPE_COUNT)
        }
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
                GameBoardAnimationFactoryImpl(),
                GameBoardTableDirectionIndicatorFactory(qNAlgorithm)
            )
        )
        snakeGameScreen.startGame()
        animator.start()
    }

    private fun runTraining() {
        if (this.episode <= numEpisodes) {
            if (!environment.hasLiveSnake()) {
                environment.raiseEvent(
                    EpisodeCompleted(episode, buildStats(qNAlgorithm))
                )
                startNewEpisode()
            }
            environment.snakes.forEach { it.thinkAndMove() }
            environment.updateScores()
        }
    }

    private fun buildStats(qNAlgorithm: QNAlgorithm): Map<String, Number> {
        val randomActionRatio =
            if (qNAlgorithm.actionCountInEpisode == 0) 0.0 else qNAlgorithm.randomActionCountInEpisode.toDouble() / qNAlgorithm.actionCountInEpisode.toDouble()

        return mapOf(
            "Score" to this.environment.highScore,
            "Random Action Count" to qNAlgorithm.randomActionCountInEpisode,
            "Random Action Ratio" to randomActionRatio,
            "Fitness" to this.environment.highestFitness,
            "Action Count" to qNAlgorithm.actionCountInEpisode,
            "Wall Collisions Count" to Statistics.wallCollisionCount,
            "Body Collision Count" to Statistics.bodyCollisionCount,
            "Starved Count" to Statistics.starvedCount
        )
    }

    private fun startNewEpisode() {
        environment.snakes.clear()

        episode++

        val snake = SnakeModel(board = environment, brain = qNeuralNetworkBrain, vision = generateVision())

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
