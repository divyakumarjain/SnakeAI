package org.divy.ai.snake.model.engine.qlearning.qnetwork.network

import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.divy.ai.snake.model.engine.qlearning.qnetwork.network.NetworkBuilder
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.AdaGrad
import org.nd4j.linalg.lossfunctions.LossFunctions

class ConvolutionNetworkBuilder(
    private val height: Long,
    private val width: Long
) : NetworkBuilder {
    override fun buildNetwork(learningRate: Double): MultiLayerNetwork {

        val padding = 1
        val stride = 2

        val widthOutput = (width + 2 * padding - width/10)/ stride
        val heightOutput = (height + 2 * padding - width/10)/ stride
        val channels = 3L

        var layerCount = 0

        val conf = NeuralNetConfiguration.Builder()
            .seed(12345)
            .weightInit(WeightInit.XAVIER)
            .updater(AdaGrad(learningRate))
            .activation(Activation.IDENTITY)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .l2(0.0001)
            .list()
            .layer(
                layerCount++, ConvolutionLayer.Builder()
                    .kernelSize((width/5).toInt(), (height/5).toInt())
                    .stride(stride, stride)
                    .padding(padding, padding)
                    .nIn(channels)
                    .units(16)
                    .activation(Activation.RELU)
                    .weightInit(WeightInit.XAVIER)
                    .name("Convolution1")
                    .build()
            )
            .layer(layerCount++, DenseLayer.Builder()
                .units(300)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .build())
            .layer(layerCount++, DenseLayer.Builder()
                .units(300)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .build())
            .layer(
                layerCount++, OutputLayer.Builder()
                    .units(4)
                    .activation(Activation.RELU)
                    .weightInit(WeightInit.XAVIER)
                    .lossFunction(LossFunctions.LossFunction.MSE)
                    .build()
            )
            .setInputType(org.deeplearning4j.nn.conf.inputs.InputType.convolutionalFlat(this.height, this.width, channels))
            .build()

        val multiLayerNetwork = MultiLayerNetwork(conf)
        multiLayerNetwork.init()

        return multiLayerNetwork
    }

}
