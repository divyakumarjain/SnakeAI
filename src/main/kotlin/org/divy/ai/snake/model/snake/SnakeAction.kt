package org.divy.ai.snake.model.snake

import java.lang.IllegalArgumentException

enum class SnakeAction(val direction: Int) {
    UP(0),
    DOWN(1),
    LEFT(2),
    RIGHT(3)
}

fun valueOfByInt(value: Int): SnakeAction {
    return when (value) {
        0 -> SnakeAction.UP
        1 -> SnakeAction.DOWN
        2 -> SnakeAction.LEFT
        3 -> SnakeAction.RIGHT
        else -> throw  IllegalArgumentException("There is no action for value $value")
    }
}
