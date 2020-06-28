package org.divy.ai.snake.model.engine.qlearning

import org.divy.ai.snake.model.engine.qlearning.qnetwork.SnakeBoardReward
import org.divy.ai.snake.model.snake.SnakeModel

class PreviousScoreReward : RewardCalculator<Double> {
    private var previousScore: Double = 0.0

    override fun calculateReward(snake: SnakeModel): SnakeBoardReward<Double> {
        return if (snake.dead) {
            var reward = - previousScore
            previousScore = 0.0
            SnakeBoardReward(reward)
        } else{
            val result = snake.score - previousScore
            previousScore = snake.score.toDouble()
            SnakeBoardReward(result.toDouble())
        }
    }
}
