package org.divy.ai.snake.model

data class Position(val x: Long, val y: Long, val z: Long = 0) {
    fun add(direction: Position) : Position {
        return Position(x+direction.x, y + direction.y, z + direction.z)
    }
}
