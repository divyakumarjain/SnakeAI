package org.divy.ai.snake.model.engine.qlearning.qnetwork.network.training

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.divy.ai.snake.model.engine.qlearning.qnetwork.SnakeBoardExperience
import org.nd4j.linalg.api.ndarray.INDArray

interface TrainingDataBuilder {
    fun map(
        experience: SnakeBoardExperience<Double>,
        neuralNetwork: MultiLayerNetwork
    ): Pair<INDArray, INDArray>

}
