package org.divy.ai.snake.model.engine.qlearning

import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType

class EpisodeCompleted(val episode: Int, val stats: Map<String, Number>) : Event(EventType.EPISODE_COMPLETED) {

}
