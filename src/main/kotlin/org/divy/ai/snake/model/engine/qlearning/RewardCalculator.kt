package org.divy.ai.snake.model.engine.qlearning

import org.divy.ai.snake.model.engine.qlearning.qnetwork.SnakeBoardReward
import org.divy.ai.snake.model.snake.SnakeModel

interface RewardCalculator<T> {
    fun calculateReward(snake: SnakeModel): SnakeBoardReward<T>
}
