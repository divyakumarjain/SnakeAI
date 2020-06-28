package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import java.util.*

interface NetworkTrainer {
    fun trainWithExperiences(
        trainingExperiences: Queue<SnakeBoardExperience<Double>>,
        passiveNeuralNetwork: MultiLayerNetwork,
        activeNeuralNetwork: MultiLayerNetwork
    )

    fun waitForTraining()

    fun isTrainingRunning(): Boolean
}
