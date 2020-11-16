package org.divy.ai.snake.model.engine.qlearning.qnetwork.dqn

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType

class ActivateNeuralNetwork(val activeNeuralNetwork: MultiLayerNetwork) : Event(EventType.ACTIVATE_NETWORK) {

}
