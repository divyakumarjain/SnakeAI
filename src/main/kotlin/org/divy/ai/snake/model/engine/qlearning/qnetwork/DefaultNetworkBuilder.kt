package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.deeplearning4j.core.storage.StatsStorage
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.ui.api.UIServer
import org.deeplearning4j.ui.model.stats.StatsListener
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.AdaGrad
import org.nd4j.linalg.lossfunctions.LossFunctions

class DefaultNetworkBuilder : NetworkBuilder {
    override fun buildNetwork(learningRate: Double): MultiLayerNetwork {
        var layerCount = 0
        val conf = NeuralNetConfiguration.Builder()
            .seed(12345)
            .weightInit(WeightInit.XAVIER)
            .updater(AdaGrad(learningRate))
            .activation(Activation.IDENTITY)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .l2(0.0001)
            .list()
            .layer(layerCount++,  DenseLayer.Builder().nIn(24).nOut(300).weightInit(WeightInit.ZERO).activation(
                Activation.IDENTITY)
                .weightInit(WeightInit.XAVIER)
                .build())
            .layer(layerCount++,  DenseLayer.Builder().nIn(300).nOut(300).weightInit(WeightInit.ZERO).activation(
                Activation.IDENTITY)
                .weightInit(WeightInit.XAVIER)
                .build())
            .layer(layerCount++, OutputLayer.Builder().nIn(300).nOut(4).weightInit(WeightInit.ZERO).activation(
                Activation.IDENTITY)
                .weightInit(WeightInit.XAVIER)
                .lossFunction(LossFunctions.LossFunction.MSE)
                .build())
            .setInputType(org.deeplearning4j.nn.conf.inputs.InputType.feedForward(24))
            .build()

        val multiLayerNetwork = MultiLayerNetwork(conf)
        multiLayerNetwork.init()

        val uiServer: UIServer = UIServer.getInstance()

        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.

        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        val statsStorage: StatsStorage =
            InMemoryStatsStorage() //Alternative: new FileStatsStorage(File), for saving and loading later


        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized

        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage)

        //Then add the StatsListener to collect this information from the network, as it trains

        //Then add the StatsListener to collect this information from the network, as it trains
        multiLayerNetwork.setListeners(StatsListener(statsStorage))

        return multiLayerNetwork
    }

}
