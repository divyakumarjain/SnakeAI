package org.divy.ai.snake.model.game

import org.divy.ai.snake.model.Position
import org.divy.ai.snake.model.snake.SnakeModel
import org.divy.ai.snake.model.engine.NeuralNetDecisionEngine
import org.divy.ai.snake.model.food.RandomFoodDropper
import org.divy.ai.snake.model.game.factory.SnakeFactory
import org.divy.ai.snake.model.snake.EightDirectionSnakeVision
import org.divy.ai.snake.model.snake.event.SnakeDeadEvent
import java.lang.Math.random

class AIGameBoardModel(boardWidth: Long, boardHeight: Long, snakes: MutableList<SnakeModel>)
    : GameBoardModel(boardWidth, boardHeight, snakes ) {

    private val populationSize = 50

    override fun start() {

        foodDropper = RandomFoodDropper(this)

        for(i in 1..populationSize/10) {
            foodDropper.drop()?.let { internalFoodList.add(it) }
        }


        val population = buildPopulation(populationSize)
        addSnake(population.snakes)

        addEventListener(EventType.SNAKE_DEAD, object: GameEventListener {
            override fun handleEvent(event: Event) {
                if(event is SnakeDeadEvent){
                    snakes.remove(event.snakeModel)
                    snakes.add(population.naturalSelection())
                }
            }

        })

        super.start()
    }

    private val snakeFactory: SnakeFactory
        get() {
            return object : SnakeFactory {
                override fun get(): SnakeModel {
                    val snakesForBoard = ArrayList<SnakeModel>(1)
                    val vision = EightDirectionSnakeVision(board = this@AIGameBoardModel)
                    val snakeModel = SnakeModel(
                        brain = NeuralNetDecisionEngine(),
                        board = this@AIGameBoardModel,
                        headPosition = Position(
                            (random() * boardWidth).toLong(),
                            (random() * boardHeight).toLong()
                        ),
                        vision = vision
                    )

                    vision.snake = snakeModel

                    snakesForBoard.add(snakeModel)

                    return snakeModel
                }

            }
        }

    private fun buildPopulation(populationSize: Int): Population {
        return Population(mutationRate = 0.05f, snakeFactory = snakeFactory, size = populationSize)
    }
}
