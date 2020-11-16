package org.divy.ai.snake.animation.game

import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import org.divy.ai.snake.application.command.AbstractLearningCommand

interface GameBoardAnimationFactory {
    fun buildAnimationTimer(boardCanvas: Canvas, rlCommand: AbstractLearningCommand): AnimationTimer
}
