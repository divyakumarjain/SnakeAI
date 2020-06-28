package org.divy.ai.snake.model.engine.qlearning

import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType

class EpisodeCompleted(val episode: Int) : Event(EventType.EPISODE_COMPLETED) {

}
