package org.divy.ai.snake.application.command.table

import javafx.animation.AnimationTimer
import org.divy.ai.snake.animation.game.GameBoardAnimationFactoryImpl
import org.divy.ai.snake.application.SnakeGameScreenImpl
import org.divy.ai.snake.application.command.AbstractLearningCommand
import org.divy.ai.snake.model.engine.qlearning.EpisodeCompleted
import org.divy.ai.snake.model.engine.qlearning.StepRewardCalculator
import org.divy.ai.snake.animation.GameBoardTableDirectionIndicatorFactory
import org.divy.ai.snake.model.engine.qlearning.table.QTableBrain
import org.divy.ai.snake.model.food.FoodEvent
import org.divy.ai.snake.model.food.RandomFoodDropper
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType
import org.divy.ai.snake.model.game.GameBoardModel
import org.divy.ai.snake.model.game.GameEventListener
import org.divy.ai.snake.model.snake.EightDirectionSnakeVision
import org.divy.ai.snake.model.snake.SnakeModel
import java.util.stream.IntStream

class SnakeBoardRlQTableCommand : AbstractLearningCommand(help="Start Snake Board RL Training using QTable") {

    lateinit var qTableBrain: QTableBrain

    override fun run() {
        environment = createBoard()
        qTableBrain = QTableBrain(
            gamaRate = gamaRate,
            learningRate = learningRate,
            randomFactor = randomFactor,
            rewardCalculator = StepRewardCalculator()
        )
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
        SnakeGameScreenImpl(this, arrayOf(GameBoardAnimationFactoryImpl(),
            GameBoardTableDirectionIndicatorFactory(
                qTableBrain
            )
        )).startGame()
        animator.start()
    }

    private fun runTraining() {
        if (this.episode <= numEpisodes) {
            if (!environment.hasLiveSnake()) {
                environment.raiseEvent(
                    EpisodeCompleted(
                        episode,
                        buildStat()
                    )
                )
                startNewEpisode()
            }
            environment.snakes.forEach { it.thinkAndMove() }
            environment.updateScores()
        }
    }

    private fun buildStat(): Map<String, Number> {
        return mapOf()
    }

    private fun startNewEpisode() {
        environment.snakes.clear()

        episode++

        val vision = EightDirectionSnakeVision(board = environment)
        val snake = SnakeModel(board = environment, brain = qTableBrain, vision = vision)
        vision.snake = snake

        snake.addEventListener(EventType.SNAKE_MOVE_COMPLETED, qTableBrain)

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
