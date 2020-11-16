package org.divy.ai.snake.model.snake

import org.divy.ai.snake.model.game.GameBoardModel
import org.divy.ai.snake.model.Position
import java.lang.IllegalArgumentException


enum class ObjectType(val value: Int) {
    FOOD(0),
    BODY(1),
    WALL(2)
}

enum class Direction(val value: Int, val relativePosition: Position) {
    LEFT(0, Position(-1, 0)),
    UP(1, Position(0, -1)),
    RIGHT(2, Position(1, 0)),
    DOWN(3, Position(0, 1)),
    LEFT_UP(4, Position(-1, -1)),
    LEFT_DOWN(5, Position(-1, 1)),
    RIGHT_UP(6, Position(1, -1)),
    RIGHT_DOWN(7, Position(1, 1))
}

fun directionValueOfByInt(value: Int): Direction {
    return when (value) {
        0 -> Direction.LEFT
        1 -> Direction.UP
        2 -> Direction.RIGHT
        3 -> Direction.DOWN
        4 -> Direction.LEFT_UP
        5 -> Direction.LEFT_DOWN
        6 -> Direction.RIGHT_UP
        7 -> Direction.RIGHT_DOWN
        else -> throw  IllegalArgumentException("There is no action for value $value")
    }
}

val OBJECT_TYPE_COUNT: Int = ObjectType.values().size
val DIRECTION_TYPE_COUNT: Int = Direction.values().size

class EightDirectionSnakeVision (snake: SnakeModel? = null, board: GameBoardModel) : DirectionSnakeVision(8, snake, board) {
    override fun clone(): SnakeVision {
        return EightDirectionSnakeVision(board = this.board)
    }
}

class FourDirectionSnakeVision (snake: SnakeModel? = null, board: GameBoardModel) : DirectionSnakeVision(4, snake, board) {
    override fun clone(): SnakeVision {
        return FourDirectionSnakeVision(board = this.board)
    }
}
class BoardSnakeVision(val board: GameBoardModel, val width: Long = board.boardWidth, val height: Long = board.boardHeight, override var snake: SnakeModel?=null) : SnakeVision {

    override fun clone(): SnakeVision {
        return BoardSnakeVision(board = this.board, width = width, height = height)
    }

    override fun observations(): SnakeObservationModel {
        val foodObservation = FloatArray((width*height).toInt()) {0.0f}
        val bodyObservation = FloatArray((width*height).toInt()) {0.0f}
        val headObservation = FloatArray((width*height).toInt()) {0.0f}

        for(xIndex in 0 until width) {
            for(yIndex in 0 until height) {
                if(board.isFoodDroppedAt(Position(xIndex, yIndex)))
                    foodObservation[(xIndex*height+yIndex).toInt()] = 1.0f
            }
        }

        if (snake != null) {

            val position = snake!!.head.position
            if(position.x in 0 until height && position.y in 0 until width)
                headObservation[(position.x * height + position.y).toInt()] = 1.0f

            for(xIndex in 0 until width) {
                for(yIndex in 0 until height) {
                    if(snake!!.hasBodyAtPosition(Position(xIndex, yIndex)))
                        bodyObservation[(xIndex*height+yIndex).toInt()] = 1.0f
                }
            }
        }

        return SnakeObservationModel((width*height).toInt(), foodObservation = foodObservation, bodyObservation = bodyObservation, headObservation = headObservation)
    }
}

interface SnakeVision {
    fun observations(): SnakeObservationModel
    var snake: SnakeModel?
    fun clone(): SnakeVision
}

abstract class DirectionSnakeVision (private val directionCount: Int, override var snake: SnakeModel?, val board: GameBoardModel) :SnakeVision {

    override fun observations(): SnakeObservationModel {
        return if(snake!= null) {
            observationFrom(fromPosition = snake!!.head.position
                , snake = snake!!
                , board = board)
        } else {
            SnakeObservationModel()
        }
    }

    fun observationFrom(
        fromPosition: Position,
        snake: SnakeModel,
        board: GameBoardModel
    ): SnakeObservationModel {

        var directionResult = mutableListOf<FloatArray>()
        val foodObservation = FloatArray(directionCount)
        val bodyObservation = FloatArray(directionCount)
        val wallObservation = FloatArray(directionCount)

        for(directionIndex in 0 until directionCount) {
            val result = lookInDirectionFrom(
                fromPosition = fromPosition,
                snake = snake,
                board = board,
                direction = directionValueOfByInt(directionIndex).relativePosition
            )
            directionResult.add(directionIndex, result)

            foodObservation[directionIndex] = result[ObjectType.FOOD.value]
            bodyObservation[directionIndex] = result[ObjectType.BODY.value]
            wallObservation[directionIndex] = result[ObjectType.WALL.value]
        }

        return SnakeObservationModel(
            directionCount = directionCount
            , foodObservation = foodObservation
            , bodyObservation = bodyObservation
            , wallObservation = wallObservation
        )
    }

    private fun lookInDirectionFrom(
        fromPosition: Position,
        direction: Position,
        snake: SnakeModel,
        board: GameBoardModel
    ): FloatArray {
        var pos1 = fromPosition
        val look = FloatArray(3)
        var distance = 1f
        var foodFound = false
        var bodyFound = false
        pos1 = pos1.add(direction)
        while (!board.isOutSideBoard(pos1)) {
            if (!foodFound && board.isFoodDroppedAt(pos1)) {
                foodFound = true
                look[ObjectType.FOOD.value] = 1f/distance
            }
            if (!bodyFound && snake.hasBodyAtPosition(pos1)) {
                bodyFound = true
                look[ObjectType.BODY.value] = 1f/distance
            }
            pos1 = pos1.add(direction)
            distance += 1f
        }
        look[2] = 1 / distance
        return look
    }
}
