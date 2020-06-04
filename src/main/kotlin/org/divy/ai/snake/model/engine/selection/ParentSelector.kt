package org.divy.ai.snake.model.engine.selection

import org.divy.ai.snake.model.snake.SnakeModel

interface ParentSelector {
    fun select(snakes: List<SnakeModel>): SnakeModel
    fun selectCouple(snakes: List<SnakeModel>): Pair<SnakeModel, SnakeModel>
}
