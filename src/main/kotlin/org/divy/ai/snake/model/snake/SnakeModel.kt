package org.divy.ai.snake.model.snake

import org.divy.ai.snake.model.Position
import org.divy.ai.snake.model.engine.DecisionEngine
import org.divy.ai.snake.model.engine.RegeneratableDecisionEngine
import org.divy.ai.snake.model.food.FoodEvent
import org.divy.ai.snake.model.game.*
import org.divy.ai.snake.model.snake.event.SnakeDeadEvent
import org.divy.ai.snake.model.snake.event.SnakeMoveCompleted
import kotlin.collections.ArrayList
import kotlin.math.floor
import kotlin.math.pow


class SnakeModel(private val brain: DecisionEngine
                 , private val board: GameBoardModel
                 , headPosition: Position = Position(
        (Math.random() * board.boardWidth).toLong(),
        (Math.random() * board.boardHeight).toLong())) {
    var score: Long = 0
    var lifeLeft = 200 //amount of moves the snake can make before it dies
    var lifetime = 0 //amount of time the snake has been alive
    var dead = false

    private var xVel = 0
    private var yVel = 0

    var head: SnakeHeadModel = SnakeHeadModel(headPosition)
    var body: ArrayList<Position> = ArrayList() //snakes body

    val vision: SnakeVision = SnakeVision(this, board)

    private val eventRegistry: EventRegistry = EventRegistry(board.eventRegistry)

    private fun bodyCollide(x: Long, y: Long): Boolean {  //check if a position collides with the snakes body
        for (i in body.indices) {
            if (x == body[i].x && y == body[i].y) {
                println("Eaten My own body at $i")
                return true
            }
        }
        return false
    }

    private fun foodCollide(position: Position): Boolean {
        return board.isFoodDroppedAt(position)
    }

    private fun wallCollide(x: Long, y: Long): Boolean {  //check if a position collides with the wall
        return board.isOutSideBoard(x,y)
    }

    fun thinkAndMove() {  //move the snake
        val observations = vision.observations()
        val decision = think(observations)
        move(decision)
        raiseEvent(SnakeMoveCompleted(this, observations))
    }

    private fun move() {
        if (!dead) {
            lifetime++
            lifeLeft--

            shiftBody()

            dead = wallCollide(head.position.x, head.position.y)
                    || bodyCollide(head.position.x, head.position.y)
                    || isStarved()

            if (foodCollide(head.position)) {
                eat()
            }


            if (dead) {
                raiseEvent(SnakeDeadEvent(this))
            }
        }
    }

    private fun isStarved() = lifeLeft <= 0

    fun eat() {  //eat food

        raiseEvent(FoodEvent(EventType.FOOD_EATEN, head.position))

        score++
        val len = body.size - 1


        adjustLifeLeft()

        if (len >= 0) {
            body.add(Position(body[len].x, body[len].y))
        } else {
            body.add(Position(head.position.x, head.position.y))
        }
    }

    private fun raiseEvent(event: Event) {
        eventRegistry.raiseEvent(event)
    }

    private fun adjustLifeLeft() {
        if (lifeLeft < 500) {
            if (lifeLeft > 400) {
                lifeLeft = 500
            } else {
                lifeLeft += 100
            }
        }
    }

    private fun shiftBody() {  //shift the body to follow the head
        var tempPosition = head.position

        head.position =
            Position(head.position.x + xVel, head.position.y + yVel)

        var temp2Position: Position

        for (i in body.indices) {
            temp2Position = body[i]

            body[i] = tempPosition

            tempPosition = temp2Position
        }
    }

    fun crossover(parent: SnakeModel): SnakeModel {  //crossover the snake with another snake
        if(brain is RegeneratableDecisionEngine) {
            return SnakeModel(brain.crossover(parent.brain), board)
        } else {
            throw IllegalStateException("Brain of this Snake does not support Cross")
        }
    }

    fun mutate(mutationRate: Float) {  //mutate the snakes brain
        if(brain is RegeneratableDecisionEngine) {
            return brain.mutate(mutationRate)
        } else {
            throw IllegalStateException("Brain of this Snake does not support mutate")
        }
    }

    fun calculateFitness(): Double {  //calculateReward the fitness of the snake

        var fitness: Double
        if (score < 10) {
            fitness = floor(lifetime.toDouble() * lifetime) * 2.0.pow(score.toDouble())
        } else {
            fitness = floor(lifetime.toDouble() * lifetime) * 2.0.pow(10) * (score - 9).toFloat()
        }

        return fitness
    }

    private fun think(observations: SnakeObservationModel): SnakeAction {
        return brain.output(observations)
    }

    private fun moveUp() {
        if (yVel != 1) {
            xVel = 0
            yVel = -1
        }
    }

    private fun moveDown() {
        if (yVel != -1) {
            xVel = 0
            yVel = 1
        }
    }

    private fun moveLeft() {
        if (xVel != 1) {
            xVel = -1
            yVel = 0
        }
    }

    private fun moveRight() {
        if (xVel != -1) {
            xVel = 1
            yVel = 0
        }
    }

    fun hasBodyAtPosition(pos: Position): Boolean {
        for (i in body.indices) {
            if (body[i] == pos) {
                return true
            }
        }
        return false
    }

    fun move(action: SnakeAction) {
        when (action) {
            SnakeAction.UP -> moveUp()
            SnakeAction.DOWN -> moveDown()
            SnakeAction.LEFT -> moveLeft()
            SnakeAction.RIGHT -> moveRight()
        }
        move()
    }

    fun addEventListener(type:EventType, listener: GameEventListener) {
        eventRegistry.addEventListener(type, listener)
    }
}

