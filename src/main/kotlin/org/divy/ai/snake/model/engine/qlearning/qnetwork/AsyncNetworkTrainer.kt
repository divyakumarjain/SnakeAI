package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import java.util.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask


class AsyncNetworkTrainer(gamaRate: Float
                          , private val trainer: SyncNetworkTrainer = SyncNetworkTrainer((gamaRate))
) : NetworkTrainer by trainer {

    private var task: ForkJoinTask<*>? = null

    var forkJoinPool = ForkJoinPool()

    override fun trainWithExperiences(
        trainingExperiences: Queue<SnakeBoardExperience<Double>>,
        passiveNeuralNetwork: MultiLayerNetwork,
        activeNeuralNetwork: MultiLayerNetwork
    ) {
        this.task = forkJoinPool.submit {
            trainer.trainWithExperiences(trainingExperiences, passiveNeuralNetwork, activeNeuralNetwork)
        }
    }

    override fun waitForTraining() {
        this.task?.join()
    }

    override fun isTrainingRunning(): Boolean = !isTrainingCompleted()

    private fun isTrainingCompleted() =
        this.task?.isCompletedNormally ?: true || this.task?.isCompletedAbnormally ?: true

}
