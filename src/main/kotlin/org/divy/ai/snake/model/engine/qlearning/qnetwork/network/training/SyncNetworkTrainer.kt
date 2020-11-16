package org.divy.ai.snake.model.engine.qlearning.qnetwork.network.training

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.divy.ai.snake.model.engine.qlearning.ExperienceBuffer
import org.divy.ai.snake.model.engine.qlearning.qnetwork.SnakeBoardExperience
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.NetworkTrainer
import org.nd4j.linalg.api.ndarray.INDArray

class SyncNetworkTrainer(private val gamaRate: Float) :
    NetworkTrainer {

    var trainingRunning: Boolean = false

    override fun trainWithExperiences(
        trainingExperiences: ExperienceBuffer<SnakeBoardExperience<Double>>,
        neuralNetwork: MultiLayerNetwork)
    {

        trainingRunning = true
        trainingExperiences.selectExperiencesForTraining()
            .forEach {
                val trainingData: Pair<INDArray, INDArray> = it.trainingData

                neuralNetwork.fit(trainingData.first, trainingData.second)
            }

        trainingRunning = false
    }

    override fun waitForTraining() {
        throw IllegalStateException("Waiting is not allowed on Synchronous trainer")
    }

    override fun isTrainingRunning(): Boolean {
        return trainingRunning
    }
}
