package org.divy.ai.snake.model.engine.qlearning.qnetwork.network

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.util.ModelSerializer

class SavedNetworkBuilder(private val path: String) : NetworkBuilder {
    override fun buildNetwork(learningRate: Double): MultiLayerNetwork {
        return ModelSerializer.restoreMultiLayerNetwork(path)
    }
}
