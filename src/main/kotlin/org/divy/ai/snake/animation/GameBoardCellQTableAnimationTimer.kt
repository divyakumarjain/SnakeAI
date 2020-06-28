package org.divy.ai.snake.animation

import javafx.animation.AnimationTimer
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import org.divy.ai.snake.model.Position
import org.divy.ai.snake.model.engine.qlearning.AbstractLearningCommand
import org.divy.ai.snake.model.engine.qlearning.EpisodeCompleted
import org.divy.ai.snake.model.food.FoodEvent
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType
import org.divy.ai.snake.model.game.GameEventListener
import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeVision
import java.time.LocalDateTime
import java.util.stream.LongStream.range

class GameBoardCellQTableAnimationTimer(
    private val graphicsContext: GraphicsContext,
    private val qValueProvider: QValueProvider,
    private val rlCommand: AbstractLearningCommand) : AnimationTimer(), GameEventListener {
    private val cachedQValue: MutableMap<Double, MutableMap<Double, MutableMap<SnakeAction, Double>>> = mutableMapOf()
    private var lastPaintTime: LocalDateTime? = null
    private val snakeVision: SnakeVision = SnakeVision(snake = null, board = rlCommand.environment)

    var previousMaxQValue: Double = 0.0
    private val cellResolution: Double
        get() =  rlCommand.cellResolution


    override fun handle(now: Long) {

        if (isCacheValid()) {
            fullRePaintFromCache()
        } else {
            val maxQValueInTable = fullRePaint( qValueProvider)

            lastPaintTime = LocalDateTime.now()

            saveMaxQValueForNextIteration(maxQValueInTable)
        }

    }

    private fun isCacheValid() = lastPaintTime != null && !qValueProvider.hasUpdatedSince(lastPaintTime!!)

    private fun fullRePaintFromCache() {
        for (cellX in range(0, rlCommand.environment.boardWidth)) {
            val cachedValuesForY:MutableMap<Double, MutableMap<SnakeAction, Double>> = cachedQValue[cellX.toDouble()]!!
            for (cellY in range(0, rlCommand.environment.boardHeight)) {

                val actionsQValue: MutableMap<SnakeAction, Double> = cachedValuesForY[cellY.toDouble()]!!

                val maxQValueInCell = actionsQValue.maxBy { it.value }?.value?: Double.NEGATIVE_INFINITY

                for (qValueEntry in actionsQValue.entries) {

                    val currentQValue = qValueEntry.value
                        drawTriangle(
                            cellX, cellY, qValueEntry.key,
                            currentOpacityBasedOnPreviousIterationMax(maxQValueInCell, currentQValue)
                        )
                    }

                    paintRectangle(
                        cellX.toDouble(),
                        cellY.toDouble(),
                        currentOpacityBasedOnPreviousIterationMax(previousMaxQValue, maxQValueInCell)
                    )
            }
        }
    }

    private fun fullRePaint(qValueProvider: QValueProvider): Double {
        var maxQValueInTable = Double.NEGATIVE_INFINITY
        for (cellX in range(0, rlCommand.environment.boardWidth)) {
            var cachedValuesForY: MutableMap<Double, MutableMap<SnakeAction, Double>>? = cachedQValue[cellX.toDouble()]
            if(cachedValuesForY==null) {
                cachedValuesForY = mutableMapOf()
                cachedQValue[cellX.toDouble()] = cachedValuesForY
            }

            for (cellY in range(0, rlCommand.environment.boardHeight)) {

                var cachedActionsQValue: MutableMap<SnakeAction, Double>? = cachedValuesForY[cellY.toDouble()]
                if(cachedActionsQValue==null) {
                    cachedActionsQValue = mutableMapOf()
                    cachedValuesForY[cellY.toDouble()] = cachedActionsQValue
                }


                if (rlCommand.environment.snakes.size > 0) {

                    val observationFromCell = snakeVision.observationFrom(
                        fromPosition = Position(cellX, cellY)
                        , board = rlCommand.environment
                        , snake = rlCommand.environment.snakes[0]
                    )

                    val qValuesForCell = qValueProvider.qValueForObservation(observationFromCell)

                    val maxQValueInCell = qValuesForCell.maxBy { it.value }?.value?: Double.NEGATIVE_INFINITY

                    for (qValueEntry in qValuesForCell.entries) {

                        cachedActionsQValue[qValueEntry.key] = qValueEntry.value

                        val currentQValue = qValueEntry.value
                        drawTriangle(
                            cellX, cellY, qValueEntry.key,
                            currentOpacityBasedOnPreviousIterationMax(maxQValueInCell, currentQValue)
                        )
                    }

                    if (maxQValueInTable < maxQValueInCell) {
                        maxQValueInTable = maxQValueInCell
                    }

                    paintRectangle(
                        cellX.toDouble(),
                        cellY.toDouble(),
                        currentOpacityBasedOnPreviousIterationMax(previousMaxQValue, maxQValueInCell)
                    )
                }
            }
        }
        return maxQValueInTable
    }

    private fun paintRectangle(x: Double,y: Double, opacityFactor: Double) {
        graphicsContext.fill = Color.YELLOW.deriveColor(
            0.0,
            1.0,
            1.0,
            opacityFactor
        )

        graphicsContext.lineWidth = 1.0
        graphicsContext.fillRect(
            x * cellResolution+1,
            y * cellResolution+1,
            cellResolution-1,
            cellResolution-1
        )
    }

    private fun currentOpacityBasedOnPreviousIterationMax(maxValue: Double, currentValue: Double) =
        1.0 - (maxValue - currentValue) / kotlin.math.abs(maxValue)

    private fun drawTriangle(cellX: Long, cellY: Long, action: SnakeAction?, opacityFactor: Double) {
        if(opacityFactor != 0.0) {
            when(action) {
                SnakeAction.LEFT -> {
                    val firstCorner = Pair(cellX*cellResolution+(cellResolution * 0.25), cellY*cellResolution+(cellResolution * 0.25))
                    val secondCorner = Pair(cellX*cellResolution+(cellResolution * 0.25), cellY*cellResolution+(cellResolution * 0.75))
                    val thirdCorner = Pair(cellX*cellResolution, cellY*cellResolution+(cellResolution/2))
                    drawTriangle(firstCorner, secondCorner, thirdCorner, opacityFactor)
                }
                SnakeAction.RIGHT -> {
                    val firstCorner = Pair(cellX*cellResolution+(cellResolution * 0.75), cellY*cellResolution + (cellResolution * 0.25))
                    val secondCorner = Pair(cellX*cellResolution+(cellResolution * 0.75), cellY*cellResolution + (cellResolution * 0.75))
                    val thirdCorner = Pair(cellX*cellResolution+cellResolution, cellY*cellResolution+(cellResolution/2))
                    drawTriangle(firstCorner, secondCorner, thirdCorner, opacityFactor)
                }
                SnakeAction.UP -> {
                    val firstCorner = Pair(cellX*cellResolution+(cellResolution * 0.25), cellY*cellResolution+(cellResolution * 0.25))
                    val secondCorner = Pair(cellX*cellResolution+(cellResolution * 0.75), cellY*cellResolution+(cellResolution * 0.25))
                    val thirdCorner = Pair(cellX*cellResolution+(cellResolution/2), cellY*cellResolution)
                    drawTriangle(firstCorner, secondCorner, thirdCorner, opacityFactor)
                }
                SnakeAction.DOWN -> {
                    val firstCorner = Pair(cellX*cellResolution+(cellResolution * 0.25), cellY*cellResolution+(cellResolution*0.75))
                    val secondCorner = Pair(cellX*cellResolution+(cellResolution * 0.75), cellY*cellResolution+(cellResolution*0.75))
                    val thirdCorner = Pair(cellX*cellResolution+(cellResolution/2), cellY*cellResolution+cellResolution)
                    drawTriangle(firstCorner, secondCorner, thirdCorner, opacityFactor)
                }
            }
        }
    }

    private fun drawTriangle(
        firstCorner: Pair<Double, Double>,
        secondCorner: Pair<Double, Double>,
        thirdCorner: Pair<Double, Double>,
        opacityFactor: Double
    ) {
        graphicsContext.fill = Color.GREEN.deriveColor(
            0.0,
            1.0,
            1.0,
            opacityFactor
        )
        graphicsContext.lineWidth = 1.0
        graphicsContext.fillPolygon(
            doubleArrayOf(firstCorner.first, secondCorner.first, thirdCorner.first)
            ,
            doubleArrayOf(firstCorner.second, secondCorner.second, thirdCorner.second)
            ,
            3
        )
    }

    private fun saveMaxQValueForNextIteration(maxQValueInTable: Double) {
        previousMaxQValue = maxQValueInTable
    }

    override fun handleEvent(event: Event) {
        if(event is FoodEvent && EventType.FOOD_EATEN.equals(event.type)) {
            lastPaintTime = null
        } else if (event is EpisodeCompleted) {
            lastPaintTime = null
        }
    }

    fun interestedEvents(): List<EventType> = listOf(EventType.FOOD_EATEN, EventType.EPISODE_COMPLETED)
}
