package org.divy.ai.snake.model.snake

import org.divy.ai.snake.model.game.GameBoardModel
import org.divy.ai.snake.model.Position

class SnakeVision (val body: SnakeModel, val board: GameBoardModel)  {

    val vision: FloatArray = FloatArray(24)

    fun see() : FloatArray{
        return vision //snakes vision
    }

    fun look() {
        var temp = lookInDirection(Position(-1, 0))
        vision[0] = temp[0]
        vision[1] = temp[1]
        vision[2] = temp[2]
        temp = lookInDirection(Position(-1, -1))
        vision[3] = temp[0]
        vision[4] = temp[1]
        vision[5] = temp[2]
        temp = lookInDirection(Position(0, -1))
        vision[6] = temp[0]
        vision[7] = temp[1]
        vision[8] = temp[2]
        temp = lookInDirection(Position(1, -1))
        vision[9] = temp[0]
        vision[10] = temp[1]
        vision[11] = temp[2]
        temp = lookInDirection(Position(1, 0))
        vision[12] = temp[0]
        vision[13] = temp[1]
        vision[14] = temp[2]
        temp = lookInDirection(Position(1, 1))
        vision[15] = temp[0]
        vision[16] = temp[1]
        vision[17] = temp[2]
        temp = lookInDirection(Position(0, 1))
        vision[18] = temp[0]
        vision[19] = temp[1]
        vision[20] = temp[2]
        temp = lookInDirection(Position(-1, 1))
        vision[21] = temp[0]
        vision[22] = temp[1]
        vision[23] = temp[2]
    }

    fun lookInDirection(direction: Position): FloatArray {  //look in a direction and check for food, body and wall
        val look = FloatArray(3)
        var pos = body.head.position
        var distance = 0f
        var foodFound = false
        var bodyFound = false
        pos = pos.add(direction)
        distance += 1f
        while (!board.isOutSideBoard(pos)) {
            if (!foodFound && board.isFoodDroppedAt(pos)) {
                foodFound = true
                look[0] = 1f
            }
            if (!bodyFound && body.hasBodyAtPosition(pos)) {
                bodyFound = true
                look[1] = 1f
            }
            pos = pos.add(direction)
            distance += 1f
        }
        look[2] = 1 / distance
        return look
    }
}
