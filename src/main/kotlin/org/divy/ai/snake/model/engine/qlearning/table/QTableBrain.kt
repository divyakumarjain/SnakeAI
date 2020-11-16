package org.divy.ai.snake.model.engine.qlearning.table

import org.divy.ai.snake.animation.QValueProvider
import org.divy.ai.snake.model.engine.DecisionEngine
import org.divy.ai.snake.model.engine.qlearning.StepRewardCalculator
import org.divy.ai.snake.model.engine.qlearning.RewardCalculator
import org.divy.ai.snake.model.engine.qlearning.qnetwork.SnakeBoardReward
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.GameEventListener
import org.divy.ai.snake.model.snake.*
import org.divy.ai.snake.model.snake.event.SnakeMoveCompleted
import java.time.LocalDateTime
import kotlin.random.Random

class QTableBrain(
    val learningRate: Float,
    val randomFactor: Float,
    val gamaRate: Float,
    val rewardCalculator: RewardCalculator<Double> = StepRewardCalculator()
) : DecisionEngine, GameEventListener, QValueProvider {

    private var qTableUpdateTime: LocalDateTime = LocalDateTime.now()
    private var qTable: MutableMap<Pair<SnakeObservationModel, SnakeAction>, Double>

    init {
        qTable = initializeQTable()
    }

    private lateinit var recentObservation: SnakeObservationModel
    private lateinit var  recentAction: SnakeAction

    override fun output(observation: SnakeObservationModel): SnakeAction {
        recentObservation = observation
        recentAction = retrieveActionWithMaxQValue(qTable, recentObservation)

        if (Random.nextInt(0, 100) <= randomFactor*100) {
            recentAction = valueOfByInt(Random.nextInt(0,SnakeAction.values().size))
        }

        return recentAction
    }

    private fun updateQTable(
        snake: SnakeModel,
        observations: SnakeObservationModel
    ) {

        val reward = calculateReward(snake)

        val qValueForAction = qTable[Pair(recentObservation, recentAction)] ?: 0.0

        val newValue =  if(terminalExperience(reward)) {
            reward.value
            } else {
                qValueForAction + learningRate * (reward.value + gamaRate*maxQValueForObservations(
                    qTable,
                    observations
                ) - qValueForAction)
            }

        qTable[Pair(recentObservation, recentAction)] = newValue

        qTableUpdateTime = LocalDateTime.now()
    }

    private fun terminalExperience(reward: SnakeBoardReward<Double>): Boolean = reward.value==-1.0

    private fun calculateReward(snake: SnakeModel) = rewardCalculator.calculateReward(snake)

    private fun randomAction(): SnakeAction {
        return valueOfByInt(Random(System.nanoTime()).nextInt(0, 3 + 1))
    }

    private fun maxQValueForObservations(
        qTable: MutableMap<Pair<SnakeObservationModel, SnakeAction>, Double>,
        observation: SnakeObservationModel
    ): Double {
        return qTable.entries.filter { it.key.first == observation }.maxBy { it.value }?.value ?:0.0
    }

    private fun retrieveActionWithMaxQValue(
        qTable: Map<Pair<SnakeObservationModel, SnakeAction>, Double>,
        observation: SnakeObservationModel
    ): SnakeAction {
        return qTable.entries
            .filter { it.key.first == observation }
            .maxBy { it.value }?.key?.second?:randomAction()
    }

    override fun handleEvent(event: Event) {
        if(event is SnakeMoveCompleted) {
            updateQTable(event.snake, event.observation)
        }
    }

    private fun initializeQTable(): MutableMap<Pair<SnakeObservationModel, SnakeAction>, Double> {
        val qTable: MutableMap<Pair<SnakeObservationModel, SnakeAction>, Double> = mutableMapOf()
//        SnakeAction.values().forEach { action ->
//            SnakeObservationPoolModel().generateAllObservations().forEach { observation ->
//                qTable[Pair(observation, action)] = 0.0
//            }
//        }

        return qTable
    }
    override fun qValueForObservation(observation: SnakeObservationModel): Map<SnakeAction, Double> {
        return mapOf( SnakeAction.LEFT to qTable.getOrDefault(Pair(observation, SnakeAction.LEFT), 0.0)
            , SnakeAction.RIGHT to qTable.getOrDefault(Pair(observation, SnakeAction.RIGHT), 0.0)
            , SnakeAction.UP to qTable.getOrDefault(Pair(observation, SnakeAction.UP), 0.0)
            , SnakeAction.DOWN to qTable.getOrDefault(Pair(observation, SnakeAction.DOWN), 0.0))
    }

    override fun hasUpdatedSince(time: LocalDateTime): Boolean = time.isBefore(qTableUpdateTime)


}
