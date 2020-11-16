package org.divy.ai.snake.model.engine.qlearning.qnetwork.dqn

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.divy.ai.snake.model.engine.qlearning.StepRewardCalculator
import org.divy.ai.snake.model.engine.qlearning.qnetwork.*
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.DefaultNetworkBuilder
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.NetworkBuilder
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.NetworkTrainer
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.training.AsyncNetworkTrainer
import org.divy.ai.snake.model.game.EventRegistry
import org.divy.ai.snake.model.game.EventType
import org.nd4j.linalg.api.ndarray.INDArray

class DoubleQNAlgorithm(
    learningRate: Double
    , gamaRate: Float
    , experienceBufferSize: Int = 1000
    , rewardCalculator: StepRewardCalculator
    , randomActionProvider: RandomActionProvider
    , networkTrainer: NetworkTrainer = AsyncNetworkTrainer(
        gamaRate
    )
    , networkBuilder: NetworkBuilder = DefaultNetworkBuilder()
) : SingleQNAlgorithm(
    learningRate,
    gamaRate,
    experienceBufferSize,
    rewardCalculator,
    randomActionProvider,
    networkTrainer,
    networkBuilder
) {
    private var passiveNeuralNetwork: MultiLayerNetwork = networkBuilder.buildNetwork(learningRate)

    init {
        EventRegistry.instance
            .addEventListener(EventType.EPISODE_COMPLETED, this)
    }

    override fun episodeCompleted() {
        super.episodeCompleted()
        val temp = this.neuralNetwork
        this.neuralNetwork = passiveNeuralNetwork
        this.passiveNeuralNetwork = temp
    }

    override fun buildTrainingData(experience: SnakeBoardExperience<Double>): Pair<INDArray, INDArray> {
        return trainingDataBuilder.map(experience, passiveNeuralNetwork)
    }
}
