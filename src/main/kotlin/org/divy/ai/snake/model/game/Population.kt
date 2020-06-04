package org.divy.ai.snake.model.game

import org.divy.ai.snake.model.engine.selection.ParentSelector
import org.divy.ai.snake.model.engine.selection.RandomRangeSelector
import org.divy.ai.snake.model.game.factory.SnakeFactory
import org.divy.ai.snake.model.snake.SnakeModel
import kotlin.collections.ArrayList

internal class Population(
    private val mutationRate: Float,
    snakeFactory: SnakeFactory,
    size: Int = 200
) {
    private val parentSelector: ParentSelector = RandomRangeSelector()
    var snakes: ArrayList<SnakeModel> = ArrayList(size)
    var bestSnakeScore = 0L
    var gen = 0
    var bestFitness = 0.0
    var fitnessSum = 0.0


    init {
        for (i in 0 until size) {
            snakes.add(snakeFactory.get())
        }
    }


    fun done(): Boolean {  //check if all the snakes in the population are dead
        for (i in snakes.indices) {
            if (!snakes[i].dead)
                return false
        }
        return true
    }

    fun update() {  //update all the snakes in the generation
        for (i in snakes.indices) {
            if (!snakes[i].dead) {
                snakes[i].look()
                snakes[i].think()
                snakes[i].move()
            }
        }
    }


    fun naturalSelection(): SnakeModel {
        val couple = parentSelector.selectCouple(snakes)
        val child: SnakeModel = couple.first.crossover(couple.second)
        child.mutate(mutationRate)
        return child
    }

    fun mutate() {
        for (i in 1 until snakes.size) {  //start from 1 as to not override the best snake placed in index 0
            snakes[i].mutate(mutationRate)
        }
    }
}

