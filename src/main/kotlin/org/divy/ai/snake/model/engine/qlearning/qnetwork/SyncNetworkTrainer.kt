package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeObservationModel
import java.util.*
import java.util.stream.IntStream

class SyncNetworkTrainer(val gamaRate: Float) : NetworkTrainer {

    var trainingRunning: Boolean = false

    override fun trainWithExperiences(
        trainingExperiences: Queue<SnakeBoardExperience<Double>>,
        passiveNeuralNetwork: MultiLayerNetwork,
        activeNeuralNetwork: MultiLayerNetwork
    ) {
        trainingRunning = true
        selectExperiencesForTraining(trainingExperiences)
            .forEach {

                val qValueForAction = calculateQValue(it.observation, it.action, activeNeuralNetwork)

                var newValue = calculateNewQValue(it, qValueForAction, activeNeuralNetwork)

                trainNetwork(it.observation, it.action, newValue, passiveNeuralNetwork)
            }

        trainingRunning = false
    }

    override fun waitForTraining() {
        throw IllegalStateException("Waiting is not allowed on Synchronous trainer")
    }

    override fun isTrainingRunning(): Boolean {
        return trainingRunning
    }

    private fun calculateNewQValue(
        experience: SnakeBoardExperience<Double>,
        qValueForAction: Double,
        neuralNetwork: MultiLayerNetwork
    ): Double {
        var newValue = if (isTerminalExperience(experience)) {
            experience.reward.value
        } else {
            qValueForAction + (experience.reward.value + gamaRate * maxQValueForObservations(
                experience.nextObservation,
                neuralNetwork
            ) - qValueForAction)
        }

        return newValue
    }

    private fun selectExperiencesForTraining(trainingExperiences: Queue<SnakeBoardExperience<Double>>): MutableSet<SnakeBoardExperience<Double>> {
        val selectedExperiences: MutableSet<SnakeBoardExperience<Double>> = mutableSetOf()

        for (i in IntStream.range(0, trainingExperiences.size / 2))
            selectedExperiences.add(trainingExperiences.random())
        return selectedExperiences
    }

    private fun isTerminalExperience(experience: SnakeBoardExperience<Double>): Boolean = experience.reward.value==-1.0


    private fun trainNetwork(observation: SnakeObservationModel
                             , action: SnakeAction
                             , newValue: Double
                             , neuralNetwork: MultiLayerNetwork) {
        val calculateQValuesForObservation = qValueVectorForObservation(observation,neuralNetwork)

        calculateQValuesForObservation[action.direction] = newValue

        trainNetwork(observation, calculateQValuesForObservation, neuralNetwork)

    }

    private fun trainNetwork(observation: SnakeObservationModel, actions: DoubleArray, neuralNetwork: MultiLayerNetwork) {
        neuralNetwork.fit(observation.toINDArray(), actions.toINDArray(actions.size))
    }

    private fun calculateQValue(
        observation: SnakeObservationModel,
        action: SnakeAction,
        neuralNetwork: MultiLayerNetwork
    ): Double {
        return qValueVectorForObservation(observation, neuralNetwork)[action.direction]
    }

    private fun maxQValueForObservations(
        observation: SnakeObservationModel
        , neuralNetwork: MultiLayerNetwork
    ): Double {
        return qValueVectorForObservation(observation, neuralNetwork).max()?:0.0
    }

    private fun qValueVectorForObservation(
        observation: SnakeObservationModel,
        neuralNetwork: MultiLayerNetwork
    ): DoubleArray =
        neuralNetwork.output(observation.toINDArray()).toDoubleVector()
}
