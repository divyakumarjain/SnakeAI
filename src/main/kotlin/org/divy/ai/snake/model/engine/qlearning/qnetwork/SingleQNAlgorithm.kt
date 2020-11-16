package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.divy.ai.snake.model.engine.qlearning.EpisodeCompleted
import org.divy.ai.snake.model.engine.qlearning.DefaultExperienceBuffer
import org.divy.ai.snake.model.engine.qlearning.StepRewardCalculator
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.DefaultNetworkBuilder
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.NetworkBuilder
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.NetworkTrainer
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.training.AsyncNetworkTrainer
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.training.DefaultTrainingDataBuilder
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.training.TrainingDataBuilder
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventRegistry
import org.divy.ai.snake.model.game.EventType
import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeModel
import org.divy.ai.snake.model.snake.SnakeObservationModel
import org.divy.ai.snake.model.snake.event.SnakeMoveCompleted
import org.nd4j.linalg.api.ndarray.INDArray
import java.time.LocalDateTime

open class SingleQNAlgorithm(
    learningRate: Double
    , gamaRate: Float
    , val experienceBufferSize: Int = 1000
    , val rewardCalculator: StepRewardCalculator
    , private val randomActionProvider: RandomActionProvider
    , private val networkTrainer: NetworkTrainer = AsyncNetworkTrainer(
        gamaRate
    )
    , networkBuilder: NetworkBuilder = DefaultNetworkBuilder()
) : QNAlgorithm {

    private var episodeCount: Int = 0
    var neuralNetwork: MultiLayerNetwork = networkBuilder.buildNetwork(learningRate)

    private lateinit var recentObservation: SnakeObservationModel
    private lateinit var  recentAction: SnakeAction

    private var lastLearningTime: LocalDateTime = LocalDateTime.now()

    override var actionCountInEpisode: Int = 0
    override var randomActionCountInEpisode: Int = 0

    var trainingDataBuilder: TrainingDataBuilder = DefaultTrainingDataBuilder(gamaRate = gamaRate )

    private val experienceBuffer: DefaultExperienceBuffer<SnakeBoardExperience<Double>> = DefaultExperienceBuffer(experienceBufferSize)

    init {
        EventRegistry.instance.addEventListener(EventType.EPISODE_COMPLETED, this)
        EventRegistry.instance.addEventListener(EventType.SNAKE_MOVE_COMPLETED, this)
    }

    private fun retrieveActionWithMaxQValue(
        observation: SnakeObservationModel
    ): SnakeAction {
        return qValueForObservation(observation).maxBy { it.value }?.key?:randomAction(observation)
    }

    private fun randomAction(observation: SnakeObservationModel): SnakeAction {
        randomActionCountInEpisode++
        return randomActionProvider.suggestAction(observation)
    }

    override fun suggestAction(observation: SnakeObservationModel?): SnakeAction {
        return if(observation!=null) {
            recentObservation = observation

            recentAction = if (randomActionProvider.shouldPlayRandomAction()) {
                randomAction(observation)
            } else {
                retrieveActionWithMaxQValue(observation)
            }

            actionCountInEpisode++

            recentAction
        } else
            SnakeAction.LEFT

    }

    override fun handleEvent(event: Event) {
        when (event) {
            is SnakeMoveCompleted -> {
                rememberAction(event.snake, event.observation)
            }
            is EpisodeCompleted -> {
                episodeCompleted()
            }
        }
    }

    protected open fun episodeCompleted() {
        ++episodeCount
        sleepTraining()
        this.randomActionCountInEpisode = 0
        this.actionCountInEpisode = 0
    }

    private fun rememberAction(
        snake: SnakeModel,
        observation: SnakeObservationModel
    ) {
        val reward = calculateReward(snake)
        rememberExperience(recentObservation, recentAction, reward, observation)
    }

    private fun calculateReward(snake: SnakeModel): SnakeBoardReward<Double> = rewardCalculator.calculateReward(snake)

    private fun sleepTraining() {

        if(networkTrainer.isTrainingRunning())
            networkTrainer.waitForTraining()

        networkTrainer.trainWithExperiences(experienceBuffer, this.neuralNetwork)

        lastLearningTime = LocalDateTime.now()
    }

    private fun rememberExperience(
        observation: SnakeObservationModel,
        action: SnakeAction,
        reward: SnakeBoardReward<Double>,
        nextObservation: SnakeObservationModel
    ) {
        val experience = SnakeBoardExperience(
            observation,
            action,
            reward,
            nextObservation)

        val result = buildTrainingData(experience)

        experience.trainingData = result

        experienceBuffer.add(experience)
    }

    protected open fun buildTrainingData(experience: SnakeBoardExperience<Double>): Pair<INDArray, INDArray> {
        return trainingDataBuilder.map(experience, neuralNetwork)
    }

    override fun qValueForObservation(observation: SnakeObservationModel): Map<SnakeAction, Double> {
        return qValueVectorForObservation(observation,this.neuralNetwork)
            .foldToMap()
    }

    private fun qValueVectorForObservation(
        observation: SnakeObservationModel,
        neuralNetwork: MultiLayerNetwork
    ): DoubleArray =
        neuralNetwork.output(observation.toINDArray()).toDoubleVector()

    override fun hasUpdatedSince(time: LocalDateTime): Boolean = time.isBefore(lastLearningTime)

}
