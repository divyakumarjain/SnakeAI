package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeModel

class SnakeBoardValidMoveProvider() : ValidMoveProvider {

    override fun getValidMoves(snake: SnakeModel): List<SnakeAction> {

        if(snake.body.size>0) {
            val validActions = ArrayList<SnakeAction>(3)
            val neck = snake.body[0]
            val head = snake.head.position

            if(neck.x == head.x) {
                validActions.add(SnakeAction.LEFT)
                validActions.add(SnakeAction.RIGHT)
                if(neck.y > head.y)
                    validActions.add(SnakeAction.UP)
                else
                    validActions.add(SnakeAction.DOWN)
            } else {
                validActions.add(SnakeAction.UP)
                validActions.add(SnakeAction.DOWN)
                if(neck.x > head.x)
                    validActions.add(SnakeAction.LEFT)
                else
                    validActions.add(SnakeAction.RIGHT)
            }
            return validActions
        } else {
            return listOf(SnakeAction.RIGHT, SnakeAction.LEFT, SnakeAction.DOWN, SnakeAction.UP)
        }
    }
}
