package org.divy.ai.snake.model.snake

import org.divy.ai.snake.model.game.GameBoardModel
import org.divy.ai.snake.model.Position

class SnakeVision (val snake: SnakeModel?, val board: GameBoardModel)  {

    val vision: FloatArray = FloatArray(24)

    enum class ObjectType(val value: Int) {
        FOOD(0),
        BODY(1),
        WALL(2)
    }

    enum class Direction(val value: Int, val relativePosition: Position) {
        LEFT(0, Position(-1, 0)),
        LEFT_UP(1, Position(-1, -1)),
        UP(2, Position(0, -1)),
        RIGHT_UP(3, Position(1, -1)),
        RIGHT(4, Position(1, 0)),
        RIGHT_DOWN(5, Position(1, 1)),
        DOWN(6, Position(0, 1)),
        LEFT_DOWN(7, Position(-1, 1))
    }

    fun observations(): SnakeObservationModel {
        return if(snake!= null) {
            observationFrom(fromPosition = snake.head.position
                , snake = snake
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
        val left = lookInDirectionFrom(fromPosition=fromPosition,
            snake = snake,
            board = board,
            direction = Direction.LEFT.relativePosition)
        val leftDown = lookInDirectionFrom(fromPosition=fromPosition,
            snake = snake,
            board = board,
            direction = Direction.LEFT_DOWN.relativePosition)
        val down = lookInDirectionFrom(fromPosition=fromPosition,
            snake = snake,
            board = board,
            direction = Direction.DOWN.relativePosition)
        val right = lookInDirectionFrom(fromPosition=fromPosition,
            snake = snake,
            board = board,
            direction = Direction.RIGHT.relativePosition)
        val leftUp = lookInDirectionFrom(fromPosition=fromPosition,
            snake = snake,
            board = board,
            direction = Direction.LEFT_UP.relativePosition)
        val rightDown = lookInDirectionFrom(fromPosition=fromPosition,
            snake = snake,
            board = board,
            direction = Direction.RIGHT_DOWN.relativePosition)
        val rightUp = lookInDirectionFrom(fromPosition=fromPosition,
            snake = snake,
            board = board,
            direction = Direction.RIGHT_UP.relativePosition)
        val up = lookInDirectionFrom(fromPosition=fromPosition,
            snake = snake,
            board = board,
            direction = Direction.UP.relativePosition)


        val foodObservation = DirectionalObservation(
            left = left[ObjectType.FOOD.value]
            , leftDown = leftDown[ObjectType.FOOD.value]
            , down = down[ObjectType.FOOD.value]
            , right = right[ObjectType.FOOD.value]
            , leftUp = leftUp[ObjectType.FOOD.value]
            , rightDown = rightDown[ObjectType.FOOD.value]
            , rightUp = rightUp[ObjectType.FOOD.value]
            , up = up[ObjectType.FOOD.value]
        )
        val bodyObservation = DirectionalObservation(
            left = left[ObjectType.BODY.value]
            , leftDown = leftDown[ObjectType.BODY.value]
            , down = down[ObjectType.BODY.value]
            , right = right[ObjectType.BODY.value]
            , leftUp = leftUp[ObjectType.BODY.value]
            , rightDown = rightDown[ObjectType.BODY.value]
            , rightUp = rightUp[ObjectType.BODY.value]
            , up = up[ObjectType.BODY.value]
        )
        val wallObservation = DirectionalObservation(
            left = left[ObjectType.WALL.value]
            , leftDown = leftDown[ObjectType.WALL.value]
            , down = down[ObjectType.WALL.value]
            , right = right[ObjectType.WALL.value]
            , leftUp = leftUp[ObjectType.WALL.value]
            , rightDown = rightDown[ObjectType.WALL.value]
            , rightUp = rightUp[ObjectType.WALL.value]
            , up = up[ObjectType.WALL.value]
        )
        return SnakeObservationModel(
            foodObservation = foodObservation
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
