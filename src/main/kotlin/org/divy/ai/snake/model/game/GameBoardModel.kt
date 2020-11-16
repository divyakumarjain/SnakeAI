package org.divy.ai.snake.model.game

import org.divy.ai.snake.model.Position
import org.divy.ai.snake.model.snake.SnakeModel
import org.divy.ai.snake.model.food.FoodDropper
import org.divy.ai.snake.model.food.FoodModel
import org.divy.ai.snake.model.game.events.StartGameEvent
import kotlinx.coroutines.*
import org.divy.ai.snake.model.food.FoodEvent

open class GameBoardModel(val boardWidth: Long = 30
                          , val boardHeight: Long = 30
                          , val snakes: MutableList<SnakeModel> = ArrayList()
                          , var score: Long = 1) {

    var highestFitness: Double = 0.0
    var averageFitness: Double = 0.0
    var lowScore: Long = 0
    var highScore: Long = 0
    var averageScore: Double = 0.0
    val eventRegistry: EventRegistry = EventRegistry.instance
    val internalFoodList: ArrayList<FoodModel> = ArrayList()

    lateinit var foodDropper: FoodDropper

    val foodList: List<FoodModel>
        get() {
            return internalFoodList.toList()
        }

    fun addFood(food:FoodModel) {
        internalFoodList.add(food)
    }

    open fun start() {
        initializeForStart()
        runGameLoop()
    }

    fun initializeForStart() {
        addEventListener(EventType.FOOD_EATEN, object : GameEventListener {
            override fun handleEvent(event: Event) {
                if (event is FoodEvent && EventType.FOOD_EATEN == event.type) {
                    for (food in internalFoodList) {
                        if (food.pos == event.position) {
                            internalFoodList.remove(food)
                            break;
                        }
                    }
                    foodDropper.drop()?.let { internalFoodList.add(it) }
                }
            }
        })
        raiseEvent(StartGameEvent())
    }

    private fun runGameLoop() {
        GlobalScope.launch {
            var hasLivingSnake = true

            while (hasLivingSnake) {

                var delayInMillis = 0L

                if (averageScore < 100) {
                    delayInMillis = (10 - averageScore * 10).toLong()
                }

                delay(delayInMillis)

                hasLivingSnake = snakes.map {
                    it.thinkAndMove()
                    !it.dead
                }.fold(false, { a,b -> a || b})
            }
        }
    }

    fun updateScores() {
        var scoreTotal = 0L
        var currentHighScore = 0L
        var currentLowScore = Long.MAX_VALUE
        var currentTotalFitness = 0.0
        var currentHighestFitness = 0.0

        val unMutableSnakes: List<SnakeModel> = snakes.toList()

        for (snake in unMutableSnakes) {

            if (snake.score > currentHighScore) {
                currentHighScore = snake.score
            }

            if (snake.score < currentLowScore) {
                currentLowScore = snake.score
            }

            scoreTotal += snake.score
            val calculateFitness = snake.calculateFitness()

            if (calculateFitness > currentHighestFitness) {
                currentHighestFitness = calculateFitness
            }

            currentTotalFitness += calculateFitness
        }
        averageScore = scoreTotal.toDouble() / snakes.size
        averageFitness = currentTotalFitness / snakes.size
        highScore = currentHighScore
        lowScore = currentLowScore
        highestFitness = currentHighestFitness
    }

    fun addSnake(snakeModel: SnakeModel) {
        snakes.add(snakeModel)
    }

    fun addSnake(newSnakes: java.util.ArrayList<SnakeModel>) {
        snakes.addAll(newSnakes)
    }

    fun isOutSideBoard(x: Long, y: Long): Boolean {
        return x >= boardWidth || x < 0 || y >= boardHeight|| y < 0
    }

    fun isEmptyPosition(pos: Position): Boolean {
        for (snake in snakes) {
            if(snake.hasBodyAtPosition(pos))
                return false
        }

        return true
    }

    fun raiseEvent(event: Event) {
        eventRegistry.raiseEvent(event)
    }

    fun addEventListener(type:EventType, listener: GameEventListener) {
        eventRegistry.addEventListener(type, listener)
    }

    fun isOutSideBoard(position: Position): Boolean {
        return  isOutSideBoard(position.x, position.y)
    }

    fun isFoodDroppedAt(pos: Position): Boolean {
        for (food in internalFoodList) {
            if (food.pos == pos)
                return true
        }

        return false
    }

    fun hasLiveSnake() = snakes.map { !it.dead }.fold(false, {result, next ->  result || next})
}
