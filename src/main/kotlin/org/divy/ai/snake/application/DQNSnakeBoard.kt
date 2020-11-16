package org.divy.ai.snake.application

import org.deeplearning4j.gym.StepReply
import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense
import org.deeplearning4j.rl4j.mdp.MDP
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration
import org.deeplearning4j.rl4j.policy.DQNPolicy
import org.deeplearning4j.rl4j.space.DiscreteSpace
import org.deeplearning4j.rl4j.space.Encodable
import org.deeplearning4j.rl4j.space.ObservationSpace
import org.divy.ai.snake.model.engine.qlearning.RewardCalculator
import org.divy.ai.snake.model.engine.qlearning.StepRewardCalculator
import org.divy.ai.snake.model.engine.qlearning.qnetwork.toINDArray
import org.divy.ai.snake.model.food.RandomFoodDropper
import org.divy.ai.snake.model.game.GameBoardModel
import org.divy.ai.snake.model.snake.*
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.learning.config.RmsProp
import java.util.logging.Logger
import java.util.stream.IntStream


object DQNSnakeBoard {

    @JvmStatic
    fun main(args: Array<String>) {
        cartPole() //get a trained agent to play the game.
        loadSnakeBoard() //show off the trained agent.
    }

    private fun cartPole() {

        // Q learning configuration. Note that none of these are specific to the cartpole problem.
        val snakeBoardQL: QLearningConfiguration = QLearningConfiguration.builder()
            .seed(123) //Random seed (for reproducability)
            .maxEpochStep(3000) // Max step By epoch
            .maxStep(15000) // Max step
            .expRepMaxSize(150000) // Max size of experience replay
            .batchSize(128) // size of batches
            .targetDqnUpdateFreq(500) // target update (hard)
            .updateStart(10) // num step noop warmup
            .rewardFactor(1.0) // reward scaling
            .gamma(0.99) // gamma
            .errorClamp(1.0) // /td-error clipping
            .minEpsilon(0.1) // min epsilon
            .epsilonNbStep(1000) // num step for eps greedy anneal
            .doubleDQN(true) // double DQN
            .build()

        // The neural network used by the agent. Note that there is no need to specify the number of inputs/outputs.
        // These will be read from the gym environment at the start of training.
        val snakeBoardNet: DQNDenseNetworkConfiguration = DQNDenseNetworkConfiguration.builder()
            .l2(0.0)
            .updater(RmsProp(0.000025))
            .numHiddenNodes(300)
            .build()

        val mdp: MDP<MDPSnakeObservationModel, Int, DiscreteSpace> = MDPGameBoardModel(numFoodDrops = 20)
        val dql: QLearningDiscreteDense<MDPSnakeObservationModel> = QLearningDiscreteDense<MDPSnakeObservationModel>(mdp, snakeBoardNet, snakeBoardQL)

        dql.train()
        mdp.close()

        dql.policy.save("./DQNSnakeBoard.model")
    }

    // pass in a generic policy and endID to allow access from other samples in this package..
    private fun loadSnakeBoard() {
        val pol = DQNPolicy.load<MDPSnakeObservationModel>("./DNSnakeBoard.model")
        val mdp2: MDP<MDPSnakeObservationModel, Int, DiscreteSpace> = MDPGameBoardModel(numFoodDrops = 20)

        val reward = pol.play(mdp2)
        Logger.getAnonymousLogger().info("Reward: $reward")

        mdp2.close()
    }
}

class MDPGameBoardModel(
    val board: GameBoardModel = GameBoardModel(30, 30),
    private val numFoodDrops: Int
) :
    MDP<MDPSnakeObservationModel, Int, DiscreteSpace> {

    private val foodDropper: RandomFoodDropper = RandomFoodDropper(board)
    private val rewardCalculator: RewardCalculator<Double> = StepRewardCalculator()

    init {
        board.foodDropper = foodDropper
        updateFoodInBoard(board)
    }

    override fun getActionSpace(): DiscreteSpace {
        return DiscreteSpace(4)
    }

    override fun getObservationSpace(): ObservationSpace<MDPSnakeObservationModel> {
        return object: ObservationSpace<MDPSnakeObservationModel> {
            override fun getLow(): INDArray {
                TODO("Not yet implemented")
            }

            override fun getHigh(): INDArray {
                TODO("Not yet implemented")
            }

            override fun getName(): String {
                return "SnakeObservationSpace"
            }

            override fun getShape(): IntArray {
                return IntArray(1) {24}
            }
        }
    }

    override fun isDone(): Boolean = !board.hasLiveSnake()

    override fun newInstance(): MDP<MDPSnakeObservationModel, Int, DiscreteSpace> {

        val gameBoardModel = GameBoardModel(boardWidth = board.boardWidth, boardHeight = board.boardHeight)
        gameBoardModel.foodDropper = foodDropper
        updateFoodInBoard(gameBoardModel)


        val newGameBoardModel = GameBoardModel(board.boardWidth, board.boardHeight)
        val vision = BoardSnakeVision(board = gameBoardModel, width = board.boardWidth, height = board.boardHeight)
        val snakeModel = SnakeModel(null, newGameBoardModel, vision = vision)
        vision.snake = snakeModel
        newGameBoardModel.addSnake(snakeModel)

        return MDPGameBoardModel(newGameBoardModel,numFoodDrops)
    }

    private fun updateFoodInBoard(gameBoardModel: GameBoardModel) {
        for (index in IntStream.range(0, numFoodDrops - gameBoardModel.internalFoodList.size))
            foodDropper.drop().let { gameBoardModel.addFood(it) }
    }

    override fun reset(): MDPSnakeObservationModel {
        board.snakes.clear()
        val vision = EightDirectionSnakeVision(board = board)
        board.addSnake(SnakeModel(null, board, vision))

        return MDPSnakeObservationModel(vision.observations())
    }

    override fun close() {
        board.snakes.clear()
    }

    override fun step(action: Int): StepReply<MDPSnakeObservationModel> {
        val snakeModel = board.snakes.find { true }!!
        snakeModel.move(valueOfByInt(action))
        val observations = snakeModel.vision.observations()
        return StepReply(MDPSnakeObservationModel(observations),rewardCalculator.calculateReward(snakeModel).value,snakeModel.dead, null)
    }

}

class MDPSnakeObservationModel(private val observations: SnakeObservationModel) : Encodable{
    override fun toArray(): DoubleArray {
        return observations.doubleVectorizedObservation()
    }

    override fun getData(): INDArray? {
        return observations.toINDArray()
    }

    override fun dup(): Encodable? {
        return null
    }

    override fun isSkipped(): Boolean {
        return false
    }
}
