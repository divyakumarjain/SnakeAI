package org.divy.ai.snake.model.engine.qlearning

import org.divy.ai.snake.model.engine.qlearning.qnetwork.SnakeBoardReward
import org.divy.ai.snake.model.snake.SnakeModel

class StepRewardCalculator : RewardCalculator<Double> {
    private var previousScore: Double = 0.0

    override fun calculateReward(snake: SnakeModel): SnakeBoardReward<Double> {
        return if (snake.dead) {
            previousScore = 0.0
            SnakeBoardReward(0.0)
        } else{
            val result = snake.score - previousScore
            previousScore = snake.score.toDouble()
            SnakeBoardReward(result)
        }
    }
}
