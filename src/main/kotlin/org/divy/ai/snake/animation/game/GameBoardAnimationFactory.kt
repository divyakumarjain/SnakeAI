package org.divy.ai.snake.animation.game

import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import org.divy.ai.snake.model.engine.qlearning.AbstractLearningCommand

interface GameBoardAnimationFactory {
    fun buildAnimationTimer(boardCanvas: Canvas, rlCommand: AbstractLearningCommand): AnimationTimer
}
