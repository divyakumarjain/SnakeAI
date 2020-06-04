package org.divy.ai.snake.model.food

import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType
import java.util.ArrayList

class RecordedFoodDropper(var foodList: ArrayList<FoodModel>): FoodDropper {

    var currentIndex = 0

    override fun drop(): FoodModel {
        return foodList[currentIndex++]
    }
}
