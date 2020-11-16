package org.divy.ai.snake.model.game

import org.divy.ai.snake.model.Position
import org.divy.ai.snake.model.food.FoodEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class EventRegistryTest {

    private lateinit var registry: EventRegistry

    @BeforeEach
    fun setup() {
        registry = EventRegistry.instance
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
        Mockito.verify(gameEventListener).handleEvent(event)
    }

    private inline fun <reified GameEventListener: Any> mockEventListener() = Mockito.mock(GameEventListener::class.java)
}
