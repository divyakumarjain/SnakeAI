package org.divy.ai.snake.model.engine

import javafx.scene.input.KeyCode
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.GameEventListener
import org.divy.ai.snake.model.game.events.NavigationEvent
import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeObservationModel

class HumanDecisionEngine : DecisionEngine, GameEventListener {

    private var decision: SnakeAction = SnakeAction.RIGHT

    override fun output(observation: SnakeObservationModel): SnakeAction {
        return decision
    }

    override fun handleEvent(event: Event) {
        if(event is NavigationEvent) {
            when (event.keyEvent.code) {
                KeyCode.UP -> {
                    decision = SnakeAction.UP
                }
                KeyCode.DOWN -> {
                    decision = SnakeAction.DOWN
                }
                KeyCode.LEFT -> {
                    decision = SnakeAction.LEFT
                }
                KeyCode.RIGHT -> {
                    decision = SnakeAction.RIGHT
                }
            }
        }
    }
}
