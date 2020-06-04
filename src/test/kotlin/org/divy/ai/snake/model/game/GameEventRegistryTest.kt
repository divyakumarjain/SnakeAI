package org.divy.ai.snake.model.game

import org.divy.ai.snake.model.Position
import org.divy.ai.snake.model.food.FoodEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class GameEventRegistryTest {

    private lateinit var registry: GameEventRegistry

    @BeforeEach
    fun setup() {
        registry = GameEventRegistry()
    }

    @Test
    fun registerEvent() {

        val gameEventListener:GameEventListener = mockEventListener()

        // Given
        registry.addEventListener(EventType.FOOD_EATEN, gameEventListener)
        // When
        val event = FoodEvent(EventType.FOOD_EATEN, Position(0L,0L))
        registry.raiseEvent(event)
        // Then
        Mockito.verify(gameEventListener).handle(event)
    }

    private inline fun <reified GameEventListener: Any> mockEventListener() = Mockito.mock(GameEventListener::class.java)
}
