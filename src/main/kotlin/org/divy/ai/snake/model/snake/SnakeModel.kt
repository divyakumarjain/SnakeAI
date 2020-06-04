package org.divy.ai.snake.model.snake

import org.divy.ai.snake.model.Position
import org.divy.ai.snake.model.engine.DecisionEngine
import org.divy.ai.snake.model.engine.RegeneratableDecisionEngine
import org.divy.ai.snake.model.food.FoodEvent
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType
import org.divy.ai.snake.model.game.GameBoardModel
import kotlin.collections.ArrayList
import kotlin.math.floor
import kotlin.math.pow


class SnakeModel(private val brain: DecisionEngine
                 , private val board: GameBoardModel
                 , headPosition: Position = Position(
        (Math.random() * board.boardWidth).toLong(),
        (Math.random() * board.boardHeight).toLong())
) {
    var score: Long = 0
    var lifeLeft = 200 //amount of moves the snake can make before it dies
    var lifetime = 0 //amount of time the snake has been alive
    var dead = false

    private var xVel = 0
    private var yVel = 0

    private var decision : FloatArray = FloatArray(4) //snakes decision

    var head: SnakeHeadModel = SnakeHeadModel(headPosition)
    var body: ArrayList<Position> = ArrayList() //snakes body

    val vision: SnakeVision = SnakeVision(this, board)

    private fun bodyCollide(x: Long, y: Long): Boolean {  //check if a position collides with the snakes body
        for (i in body.indices) {
            if (x == body[i].x && y == body[i].y) {
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

    fun move() {  //move the snake
        if (!dead) {
            lifetime++
            lifeLeft--
            if (foodCollide(head.position)) {
                eat()
            }
            shiftBody()

            dead = wallCollide(head.position.x, head.position.y)
                    || bodyCollide(head.position.x, head.position.y)
                    || isStarved()
        }
        if(dead) {
            raiseEvent(SnakeDeadEvent(this))
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
        board.raiseEvent(event)
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

    fun calculateFitness(): Double {  //calculate the fitness of the snake

        var fitness: Double
        if (score < 10) {
            fitness = floor(lifetime.toDouble() * lifetime) * 2.0.pow(score.toDouble())
        } else {
            fitness = floor(lifetime.toDouble() * lifetime)
            fitness *= 2.0.pow(10)
            fitness *= (score - 9).toFloat()
        }

        return fitness
    }

    fun look() {  //look in all 8 directions and check for food, body and wall
        vision.look()
    }

    fun think() {  //think about what direction to move
        decision = brain.output(vision)
        var maxIndex = 0
        var max = 0f
        for (i in decision.indices) {
            if (decision[i] > max) {
                max = decision[i]
                maxIndex = i
            }
        }
        when (maxIndex) {
            0 -> moveUp()
            1 -> moveDown()
            2 -> moveLeft()
            3 -> moveRight()
        }
    }

    fun moveUp() {
        if (yVel != 1) {
            xVel = 0
            yVel = -1
        }
    }

    fun moveDown() {
        if (yVel != -1) {
            xVel = 0
            yVel = 1
        }
    }

    fun moveLeft() {
        if (xVel != 1) {
            xVel = -1
            yVel = 0
        }
    }

    fun moveRight() {
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

    fun clone(): SnakeModel {

        return SnakeModel(brain, board)
    }
}

