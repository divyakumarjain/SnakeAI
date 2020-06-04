package org.divy.ai.snake.model.engine

import javafx.scene.input.KeyCode
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.GameEventListener
import org.divy.ai.snake.model.game.events.NavigationEvent
import org.divy.ai.snake.model.snake.SnakeVision

class HumanDecisionEngine : DecisionEngine, GameEventListener {

    val decision = FloatArray(4)

    override fun output(vision: SnakeVision): FloatArray {
        return decision
    }

    override fun handle(event: Event) {
        if(event is NavigationEvent) {
            if (event.keyEvent.code == KeyCode.UP) {
                decision[0] = 1.0F
                decision[1] = 0.0F
                decision[2] = 0.0F
                decision[3] = 0.0F
            } else if (event.keyEvent.code == KeyCode.DOWN) {
                decision[0] = 0.0F
                decision[1] = 1.0F
                decision[2] = 0.0F
                decision[3] = 0.0F
            } else if (event.keyEvent.code == KeyCode.LEFT) {
                decision[0] = 0.0F
                decision[1] = 0.0F
                decision[2] = 1.0F
                decision[3] = 0.0F
            }else if (event.keyEvent.code == KeyCode.RIGHT) {
                decision[0] = 0.0F
                decision[1] = 0.0F
                decision[2] = 0.0F
                decision[3] = 1.0F
            }
        }
    }
}
