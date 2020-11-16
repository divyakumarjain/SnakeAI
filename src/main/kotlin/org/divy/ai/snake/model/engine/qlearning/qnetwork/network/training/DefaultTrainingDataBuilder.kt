package org.divy.ai.snake.model.engine.qlearning.qnetwork.network.training

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.divy.ai.snake.model.engine.qlearning.qnetwork.SnakeBoardExperience
import org.divy.ai.snake.model.engine.qlearning.qnetwork.toINDArray
import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeObservationModel
import org.nd4j.linalg.api.ndarray.INDArray

class DefaultTrainingDataBuilder(val gamaRate: Float) : TrainingDataBuilder {
    override fun map(
        experience: SnakeBoardExperience<Double>,
        neuralNetwork: MultiLayerNetwork
    ): Pair<INDArray, INDArray> {

        val qValueForAction = calculateQValue(experience.observation, experience.action, neuralNetwork)

        val newValue = calculateNewQValue(experience, qValueForAction, neuralNetwork)

        val calculatedQValuesForObservation = qValueVectorForObservation(experience.observation, neuralNetwork)

        calculatedQValuesForObservation[experience.action.direction] = newValue

        return Pair(experience.observation.toINDArray(), calculatedQValuesForObservation.toINDArray(
            calculatedQValuesForObservation.size))
    }


    private fun calculateNewQValue(
        experience: SnakeBoardExperience<Double>,
        qValueForAction: Double,
        neuralNetwork: MultiLayerNetwork
    ): Double {

        return if (isTerminalExperience(experience)) {
            experience.reward.value
        } else {
            qValueForAction + (experience.reward.value + gamaRate * maxQValueForObservations(
                experience.nextObservation,
                neuralNetwork
            ) - qValueForAction)
        }
    }

    private fun isTerminalExperience(experience: SnakeBoardExperience<Double>): Boolean = experience.reward.value==-1.0


    private fun calculateQValue(
        observation: SnakeObservationModel,
        action: SnakeAction,
        neuralNetwork: MultiLayerNetwork
    ): Double {
        return qValueVectorForObservation(observation, neuralNetwork)[action.direction]
    }

    private fun maxQValueForObservations(
        observation: SnakeObservationModel,
        neuralNetwork: MultiLayerNetwork
    ): Double {
        return qValueVectorForObservation(observation, neuralNetwork).max()?:0.0
    }

    private fun qValueVectorForObservation(
        observation: SnakeObservationModel,
        neuralNetwork: MultiLayerNetwork
    ): DoubleArray =
        neuralNetwork.output(observation.toINDArray()).toDoubleVector()

}
