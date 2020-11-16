package org.divy.ai.snake.model.game

import org.divy.ai.snake.model.snake.SnakeModel
import org.divy.ai.snake.model.engine.HumanDecisionEngine
import org.divy.ai.snake.model.food.RandomFoodDropper
import org.divy.ai.snake.model.snake.EightDirectionSnakeVision

class HumanGameBoardModel(boardWidth: Long, boardHeight: Long) : GameBoardModel(boardWidth, boardHeight, ArrayList()) {

    override fun start() {
        foodDropper = RandomFoodDropper(this)
        foodDropper.drop()
        val humanDecisionEngine = HumanDecisionEngine()
        val vision = EightDirectionSnakeVision(board = this)
        val snake = SnakeModel(brain = humanDecisionEngine, board = this, vision = vision)
        vision.snake =snake

        addSnake(snake)
        addEventListener(EventType.NAVIGATION, humanDecisionEngine)

        super.start()
    }
}
