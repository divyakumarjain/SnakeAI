package org.divy.ai.snake.model.engine.qlearning.qnetwork.network

import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.NetworkBuilder
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.AdaGrad
import org.nd4j.linalg.lossfunctions.LossFunctions

class DefaultNetworkBuilder(private val observationLength: Int = 24) :
    NetworkBuilder {
    override fun buildNetwork(learningRate: Double): MultiLayerNetwork {
        var layerCount = 0
        val conf = NeuralNetConfiguration.Builder()
            .seed(System.currentTimeMillis())
            .weightInit(WeightInit.XAVIER)
            .updater(AdaGrad(learningRate))
            .activation(Activation.IDENTITY)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .l2(0.0001)
            .list()
            .layer(layerCount++,  DenseLayer.Builder().nIn(observationLength).units(30).activation(
                Activation.IDENTITY)
                .weightInit(WeightInit.XAVIER)
                .build())
            .layer(layerCount++,  DenseLayer.Builder().units(30).nIn(30).activation(
                Activation.IDENTITY)
                .weightInit(WeightInit.XAVIER)
                .build())
            .layer(layerCount++, OutputLayer.Builder().nIn(30).units(4).activation(
                Activation.IDENTITY)
                .weightInit(WeightInit.XAVIER)
                .lossFunction(LossFunctions.LossFunction.MSE)
                .build())
            .setInputType(org.deeplearning4j.nn.conf.inputs.InputType.feedForward(observationLength.toLong()))
            .build()

        val multiLayerNetwork = MultiLayerNetwork(conf)
        multiLayerNetwork.init()



        return multiLayerNetwork
    }

}
