package org.divy.ai.snake.model.engine.qlearning.qnetwork.network

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.divy.ai.snake.model.engine.qlearning.ExperienceBuffer
import org.divy.ai.snake.model.engine.qlearning.qnetwork.SnakeBoardExperience

interface NetworkTrainer {
    fun trainWithExperiences(
        trainingExperiences: ExperienceBuffer<SnakeBoardExperience<Double>>,
        neuralNetwork: MultiLayerNetwork)

    fun waitForTraining()

    fun isTrainingRunning(): Boolean
}
