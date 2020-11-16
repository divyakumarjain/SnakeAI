package org.divy.ai.snake.animation.game

import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import org.divy.ai.snake.animation.GameBoardAnimationTimer
import org.divy.ai.snake.application.command.AbstractLearningCommand

class GameBoardAnimationFactoryImpl : GameBoardAnimationFactory {
    override fun buildAnimationTimer(boardCanvas: Canvas, rlCommand: AbstractLearningCommand): AnimationTimer {
        return GameBoardAnimationTimer(boardCanvas.graphicsContext2D,
            rlCommand.environment, rlCommand.cellResolution
        )
    }
}
