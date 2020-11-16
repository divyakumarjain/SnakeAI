package org.divy.ai.snake.model.engine.qlearning

import org.divy.ai.snake.model.engine.qlearning.qnetwork.SnakeBoardReward
import org.divy.ai.snake.model.snake.SnakeModel

class CumulativeRewardCalculator :RewardCalculator<Double> {
    override fun calculateReward(snake: SnakeModel): SnakeBoardReward<Double>  = SnakeBoardReward(snake.score.toDouble())
}
