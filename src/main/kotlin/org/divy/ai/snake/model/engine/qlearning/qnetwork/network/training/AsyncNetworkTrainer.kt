package org.divy.ai.snake.model.engine.qlearning.qnetwork.network.training

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.divy.ai.snake.model.engine.qlearning.ExperienceBuffer
import org.divy.ai.snake.model.engine.qlearning.qnetwork.SnakeBoardExperience
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.NetworkTrainer
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask


class AsyncNetworkTrainer(gamaRate: Float
                          , private val trainer: SyncNetworkTrainer = SyncNetworkTrainer(
        (gamaRate)
    )
) : NetworkTrainer by trainer {

    private var task: ForkJoinTask<*>? = null

    var forkJoinPool = ForkJoinPool()

    override fun trainWithExperiences(
        trainingExperiences: ExperienceBuffer<SnakeBoardExperience<Double>>,
        neuralNetwork: MultiLayerNetwork) {
        this.task = forkJoinPool.submit {
            trainer.trainWithExperiences(trainingExperiences, neuralNetwork)
        }
    }

    override fun waitForTraining() {
        this.task?.join()
    }

    override fun isTrainingRunning(): Boolean = !isTrainingCompleted()

    private fun isTrainingCompleted() =
        this.task?.isCompletedNormally ?: true || this.task?.isCompletedAbnormally ?: true

}
