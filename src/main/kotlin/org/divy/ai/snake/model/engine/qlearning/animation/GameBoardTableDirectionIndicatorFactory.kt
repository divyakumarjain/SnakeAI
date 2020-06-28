package org.divy.ai.snake.model.engine.qlearning.animation

import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import org.divy.ai.snake.animation.GameBoardCellQTableAnimationTimer
import org.divy.ai.snake.animation.QValueProvider
import org.divy.ai.snake.animation.game.GameBoardAnimationFactory
import org.divy.ai.snake.model.engine.qlearning.AbstractLearningCommand
import org.divy.ai.snake.model.game.EventType

class GameBoardTableDirectionIndicatorFactory(private val qValueProvider: QValueProvider) : GameBoardAnimationFactory {
    override fun buildAnimationTimer(boardCanvas: Canvas, rlCommand: AbstractLearningCommand): AnimationTimer {
        val gameBoardCellQTableAnimationTimer = GameBoardCellQTableAnimationTimer(
            boardCanvas.graphicsContext2D
            , qValueProvider
            , rlCommand
        )
        val interestedEvents:List<EventType> = gameBoardCellQTableAnimationTimer.interestedEvents()
        interestedEvents.forEach { rlCommand.environment.addEventListener(it, gameBoardCellQTableAnimationTimer) }
        return gameBoardCellQTableAnimationTimer
    }
}
