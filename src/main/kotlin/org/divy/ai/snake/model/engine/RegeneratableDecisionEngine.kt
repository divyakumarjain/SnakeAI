package org.divy.ai.snake.model.engine

interface RegeneratableDecisionEngine: DecisionEngine {
    fun crossover(brain: DecisionEngine): DecisionEngine
    fun mutate(mutationRate: Float)
}
