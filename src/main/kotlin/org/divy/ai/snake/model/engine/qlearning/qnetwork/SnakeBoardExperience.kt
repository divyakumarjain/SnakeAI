package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeObservationModel
import org.nd4j.linalg.api.ndarray.INDArray

data class SnakeBoardExperience<R>(
    val observation: SnakeObservationModel,
    val action: SnakeAction,
    val reward: SnakeBoardReward<R>,
    val nextObservation: SnakeObservationModel
) {
    lateinit var trainingData: Pair<INDArray, INDArray>
}
