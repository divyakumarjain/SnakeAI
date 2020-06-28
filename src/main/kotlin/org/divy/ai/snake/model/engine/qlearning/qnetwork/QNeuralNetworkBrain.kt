package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.divy.ai.snake.model.engine.DecisionEngine
import org.divy.ai.snake.model.snake.*


class QNeuralNetworkBrain(
    rlAlgorithm: QNAlgorithm
) : DecisionEngine
    , ActionProvider by rlAlgorithm {

    override fun output(observation: SnakeObservationModel): SnakeAction {
        return suggestAction(observation)
    }

}
