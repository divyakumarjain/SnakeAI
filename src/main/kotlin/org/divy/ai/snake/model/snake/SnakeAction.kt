package org.divy.ai.snake.model.snake

import java.lang.IllegalArgumentException

enum class SnakeAction(val direction: Int) {
    LEFT(0),
    UP(1),
    RIGHT(2),
    DOWN(3)
}

fun valueOfByInt(value: Int): SnakeAction {
    return when (value) {
        0 -> SnakeAction.LEFT
        1 -> SnakeAction.UP
        2 -> SnakeAction.RIGHT
        3 -> SnakeAction.DOWN
        else -> throw  IllegalArgumentException("There is no action for value $value")
    }
}
