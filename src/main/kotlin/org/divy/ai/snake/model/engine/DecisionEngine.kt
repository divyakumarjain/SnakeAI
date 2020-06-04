package org.divy.ai.snake.model.engine

import org.divy.ai.snake.model.snake.SnakeVision

interface DecisionEngine {
    fun output(vision: SnakeVision): FloatArray
}
