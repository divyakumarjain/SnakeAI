package org.divy.ai.snake.model.food

import org.divy.ai.snake.model.game.GameBoardModel
import org.divy.ai.snake.model.Position
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType
import java.lang.Math.random

class RandomFoodDropper (private val boardModel: GameBoardModel): FoodDropper {
    override fun drop(): FoodModel {
        var pos = generateRandomPosition()
        while (!boardModel.isEmptyPosition(pos)) {
            pos = generateRandomPosition()
        }
        return FoodModel(pos)
    }

    private fun generateRandomPosition(): Position {
        val x: Long = (random() * (boardModel.boardWidth.toDouble())).toLong()
        val y: Long = (random() * (boardModel.boardHeight.toDouble())).toLong()
        return Position(x, y)
    }
}
