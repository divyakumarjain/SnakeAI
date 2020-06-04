package org.divy.ai.snake.model.engine.selection

import org.apache.logging.log4j.LogManager
import org.divy.ai.snake.model.snake.SnakeModel
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.absoluteValue

class RandomRangeSelector : ParentSelector {

    private val logger = LogManager.getLogger(javaClass)

    override fun select(snakes: List<SnakeModel>): SnakeModel {

        var selectionIndex = ThreadLocalRandom.current().nextGaussian().toInt().absoluteValue

        if(selectionIndex >= snakes.size) {
            selectionIndex = snakes.size -1
        }

        return snakes[selectionIndex]
    }

    override fun selectCouple(snakes: List<SnakeModel>): Pair<SnakeModel, SnakeModel> {
        val fitnessSortedSnakes = snakes.sortedByDescending { it.calculateFitness() }
        var first = select(fitnessSortedSnakes)
        var second = select(fitnessSortedSnakes)

        while(second == first) {
            second = select(fitnessSortedSnakes)
        }

        logger.info("""Selected snake with fitness ${first.calculateFitness()} and ${second.calculateFitness()}""")

        return Pair(first, second)
    }
}
