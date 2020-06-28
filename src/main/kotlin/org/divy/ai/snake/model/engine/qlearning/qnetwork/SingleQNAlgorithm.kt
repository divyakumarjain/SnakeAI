package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.divy.ai.snake.model.engine.qlearning.PreviousScoreReward
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeModel
import org.divy.ai.snake.model.snake.SnakeObservationModel
import org.divy.ai.snake.model.snake.event.SnakeMoveCompleted
import org.divy.ai.snake.model.snake.valueOfByInt
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

class SingleQNAlgorithm(
    learningRate: Double
    , gamaRate: Float
    , val experienceBufferSize: Int = 1000
    , val rewardCalculator: PreviousScoreReward
    , val randomActionProvider: RandomActionProvider
    , private val networkTrainer: NetworkTrainer = AsyncNetworkTrainer(gamaRate)
    , networkBuilder: NetworkBuilder = DefaultNetworkBuilder()
) : QNAlgorithm {
    val neuralNetwork: MultiLayerNetwork = networkBuilder.buildNetwork(learningRate)

    private lateinit var recentObservation: SnakeObservationModel
    private lateinit var  recentAction: SnakeAction

    private var lastLearningTime: LocalDateTime =
        LocalDateTime.now()

    private var movesInDay: Int = 0

    private val experienceBuffer: Queue<SnakeBoardExperience<Double>> =
        LinkedList()

    private fun retrieveActionWithMaxQValue(
        observation: SnakeObservationModel
    ): SnakeAction {
        return qValueForObservation(observation).maxBy { it.value }?.key?:randomAction()
    }

    private fun randomAction(): SnakeAction {
        return valueOfByInt(
            Random(
                System.nanoTime()
            ).nextInt(0, 4)
        )
    }

    override fun suggestAction(observation: SnakeObservationModel): SnakeAction {
        recentObservation = observation


        recentAction = if (randomActionProvider.shouldPlayRandomAction()) {
            randomActionProvider.suggestAction(observation)
        } else {
            retrieveActionWithMaxQValue(observation)
        }

        return recentAction
    }

    override fun handleEvent(event: Event) {
        if(event is SnakeMoveCompleted) {
            updateNetwork(event.snake, event.observation)
        }
    }

    private fun updateNetwork(
        snake: SnakeModel,
        observation: SnakeObservationModel
    ) {

        val reward = calculateReward(snake)

        rememberExperience(recentObservation, recentAction, reward, observation)

        ++movesInDay

        if(snake.dead) {
            sleepTraining()
        }
    }

    private fun calculateReward(snake: SnakeModel): SnakeBoardReward<Double> = rewardCalculator.calculateReward(snake)

    private fun sleepTraining() {

        if(networkTrainer.isTrainingRunning())
            networkTrainer.waitForTraining()

        networkTrainer.trainWithExperiences(experienceBuffer, this.neuralNetwork, this.neuralNetwork);

        var i=experienceBuffer.size

        while(i > experienceBufferSize) {
            experienceBuffer.remove()
            i--
        }
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
            nextObservation
        )
        if(!experienceBuffer.contains(experience)) {
            this.experienceBuffer.add(experience)
        }
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
