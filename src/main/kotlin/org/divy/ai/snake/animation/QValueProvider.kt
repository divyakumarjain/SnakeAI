package org.divy.ai.snake.animation

import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeObservationModel
import java.time.LocalDateTime


interface QValueProvider {
    fun qValueForObservation(observation: SnakeObservationModel): Map<SnakeAction, Double>
    fun hasUpdatedSince(time: LocalDateTime): Boolean
}
