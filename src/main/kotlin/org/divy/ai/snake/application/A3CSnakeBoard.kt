package org.divy.ai.snake.application

import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete.A3CConfiguration
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscreteDense
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactorySeparateStdDense
import org.deeplearning4j.rl4j.policy.ACPolicy
import org.nd4j.linalg.learning.config.Adam
import java.io.IOException
import java.util.logging.Logger

/**
 *
 * A3C on cartpole
 * This example shows the classes in rl4j that implement the article here: https://arxiv.org/abs/1602.01783
 * Asynchronous Methods for Deep Reinforcement Learning. Mnih et al.
 *
 */
object A3CCartpole {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        A3CcartPole()
    }

    @Throws(IOException::class)
    private fun A3CcartPole() {

        val mdp = MDPGameBoardModel(numFoodDrops = 20)

        val snakeboardA3C = A3CConfiguration.builder()
            .seed(123)
            .maxEpochStep(2000)
            .maxStep(500000)
            .numThread(8)
            .nstep(20)
            .updateStart(10)
            .rewardFactor(1.0)
            .gamma(0.99)
            .errorClamp(1.0)
            .build()

        val snakeboardNetA3C =
            ActorCriticFactorySeparateStdDense.Configuration
                .builder()
                .updater(Adam(1e-2))
                .l2(0.0)
                .numHiddenNodes(16)
                .numLayer(3)
                .build()

        //define the training
        val a3c = A3CDiscreteDense(mdp, snakeboardNetA3C, snakeboardA3C)
        a3c.train() //start the training
        mdp.close()
        val pol = a3c.policy
        pol.save("/tmp/val1/", "/tmp/pol1")

        //reload the policy, will be equal to "pol", but without the randomness
        val pol2 =
            ACPolicy.load<MDPSnakeObservationModel>("/tmp/val1/", "/tmp/pol1")
        loadCartpole(pol2)
        println("sample finished.")
    }

    // pass in a generic policy and endID to allow access from other samples in this package..
    fun loadCartpole(pol: ACPolicy<MDPSnakeObservationModel>) {
        //use the trained agent on a new similar mdp (but render it this time)

        //define the mdp from gym (name, render)
        val mdp2 =
            MDPGameBoardModel(numFoodDrops = 20)

        val reward = pol.play(mdp2)
        Logger.getAnonymousLogger().info("Reward: $reward")

        mdp2.close()
    }
}
