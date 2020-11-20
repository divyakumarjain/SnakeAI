package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.divy.ai.snake.animation.QValueProvider
import org.divy.ai.snake.model.game.GameEventListener

interface QNAlgorithm : QValueProvider, ActionProvider, GameEventListener {

    var randomActionCountInEpisode: Int
    var actionCountInEpisode: Int
}
