package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.divy.ai.snake.model.engine.qlearning.EpisodeCompleted
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.GameEventListener
import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeObservationModel
import org.divy.ai.snake.model.snake.valueOfByInt
import kotlin.random.Random

class RandomActionProvider(var randomFactor: Float,val randomDecay: Float=0.99999f) : ActionProvider, GameEventListener {
    private var episodeCount: Int = 0

    override fun suggestAction(observation: SnakeObservationModel?): SnakeAction {
        return valueOfByInt(Random.nextInt(0, SnakeAction.values().size))
    }

    fun shouldPlayRandomAction() = Random.nextInt(0,100) <= randomFactor * 100

    override fun handleEvent(event: Event) {
        if(event is EpisodeCompleted) {
            episodeCount++
            randomFactor *= randomDecay

            println("After episode $episodeCount the random factor is $randomFactor")
        }
    }
}
