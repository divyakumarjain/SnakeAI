package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork

interface NetworkBuilder {
    fun buildNetwork(learningRate: Double): MultiLayerNetwork
}
