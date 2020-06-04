package org.divy.ai.snake.animation

import javafx.animation.AnimationTimer
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import org.divy.ai.snake.model.Position
import org.divy.ai.snake.model.game.GameBoardModel
import org.divy.ai.snake.model.snake.SnakeModel

class GameBoardAnimationTimer(private val graphicsContext: GraphicsContext
                              , private val gameModel: GameBoardModel
                              , private val cellResolution: Double) : AnimationTimer() {
    override fun handle(now: Long) {
        graphicsContext.clearRect(0.0, 0.0, graphicsContext.canvas.width, graphicsContext.canvas.height)
        paintBoardGrid()

        paintFood()
        for (snakeModel in gameModel.snakes) {
//            paintVision(snakeModel)
            paintSnakes(snakeModel)
        }
    }

    private fun paintVision(snake: SnakeModel) {
        traceVisionInDirection(snake, left)
        traceVisionInDirection(snake, leftBottom)
        traceVisionInDirection(snake, bottom)
        traceVisionInDirection(snake, rightBottom)
        traceVisionInDirection(snake, right)
        traceVisionInDirection(snake, rightTop)
        traceVisionInDirection(snake, top)
        traceVisionInDirection(snake, topLeft)
    }

    private fun paintSnakes(snakeModel: SnakeModel) {
        val bodyColor = Color.GREEN.deriveColor(
            0.0,
            1.0,
            snakeModel.lifeLeft / 500.0,
            1.0
        )
        for (bodyPosition in snakeModel.body) {
            paintRectangle(bodyPosition, bodyColor)
        }
        paintRectangle(snakeModel.head.position, bodyColor)
    }

    private fun paintFood() {
        for (food in gameModel.foodList)
            paintRectangle(food.pos, Color.RED)
    }

    private fun paintBoardGrid() {
        graphicsContext.stroke = Color.BLACK
        graphicsContext.strokeRect(
            0.0,
            0.0,
            gameModel.boardWidth.toDouble() * cellResolution,
            gameModel.boardHeight.toDouble() * cellResolution
        )

        for (gridIndex in 1..gameModel.boardWidth) {
            graphicsContext.stroke = Color.LIGHTGRAY
            graphicsContext.lineWidth = 0.5
            graphicsContext.strokeLine(
                gridIndex * cellResolution,
                0.0,
                gridIndex * cellResolution,
                gameModel.boardHeight * cellResolution
            )
        }

        for (gridIndex in 1..gameModel.boardHeight) {
            graphicsContext.stroke = Color.LIGHTGRAY
            graphicsContext.lineWidth = 0.5
            graphicsContext.strokeLine(
                0.0,
                gridIndex * cellResolution,
                gameModel.boardWidth * cellResolution,
                gridIndex * cellResolution
            )
        }
    }

    private fun paintRectangle(position: Position, paint: Paint) {
        graphicsContext.fill = paint
        graphicsContext.lineWidth = 2.0
        graphicsContext.fillRect(
            position.x.toDouble() * cellResolution,
            position.y.toDouble() * cellResolution,
            cellResolution,
            cellResolution
        )
    }

    private val left = Position(-1, 0)
    private val leftBottom = Position(-1, 1)
    private val bottom = Position(0, 1)
    private val rightBottom = Position(1, 1)
    private val right = Position(1, 0)
    private val rightTop = Position(1, -1)
    private val top = Position(0, -1)
    private val topLeft = Position(-1, -1)

    private fun traceVisionInDirection(snake: SnakeModel, direction: Position) {
        var pos = snake.vision.body.head.position

        pos = pos.add(direction)

        while (!gameModel.isOutSideBoard(pos)) {

            if (gameModel.isFoodDroppedAt(pos) || snake.vision.body.hasBodyAtPosition(pos)) {
                return
            } else {
                graphicsContext.lineWidth = 1.0
                graphicsContext.fill = Color.LIGHTBLUE
                graphicsContext.fillOval(
                    pos.x.toDouble() * cellResolution + cellResolution / 4,
                    pos.y.toDouble() * cellResolution + cellResolution / 4,
                    cellResolution / 2, cellResolution / 2
                )
            }
            pos = pos.add(direction)
        }
    }
}
