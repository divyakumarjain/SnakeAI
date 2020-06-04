package org.divy.ai.snake.model.game


open class Event(val type: EventType) {

}

enum class EventType {
    FOOD_EATEN,
    START_GAME,
    NAVIGATION,
    FOOD_DROPPED,
    SNAKE_DEAD
}
