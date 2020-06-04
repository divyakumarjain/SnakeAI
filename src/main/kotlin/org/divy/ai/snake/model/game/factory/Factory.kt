package org.divy.ai.snake.model.game.factory

interface Factory<T> {
    fun get() : T
}
