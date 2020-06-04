package org.divy.ai.snake.model.game

import org.divy.ai.snake.model.snake.SnakeModel
import org.divy.ai.snake.model.engine.HumanDecisionEngine
import org.divy.ai.snake.model.food.RandomFoodDropper

class HumanGameBoardModel(boardWidth: Long, boardHeight: Long) : GameBoardModel(boardWidth, boardHeight, ArrayList()) {

    override fun start() {
        foodDropper = RandomFoodDropper(this)
        val humanDecisionEngine = HumanDecisionEngine()
        addSnake(SnakeModel(humanDecisionEngine, this))

        addEventListener(EventType.NAVIGATION, humanDecisionEngine)

        super.start()
    }
}
