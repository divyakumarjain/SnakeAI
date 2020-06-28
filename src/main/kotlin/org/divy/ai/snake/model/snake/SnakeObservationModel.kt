package org.divy.ai.snake.model.snake

data class SnakeObservationModel(val foodObservation:DirectionalObservation = DirectionalObservation()
                                 , val wallObservation:DirectionalObservation = DirectionalObservation()
                                 , val bodyObservation:DirectionalObservation = DirectionalObservation())

data class DirectionalObservation (
    val left: Float = 0.0f,
    val right: Float = 0.0f,
    val up: Float = 0.0f,
    val down: Float = 0.0f,
    val leftDown: Float = 0.0f,
    val leftUp: Float = 0.0f,
    val rightUp: Float = 0.0f,
    val rightDown: Float =0.0f
)

val OBJECT_TYPE_COUNT: Int = SnakeVision.ObjectType.values().size

private fun positionFor(direction: SnakeVision.Direction, objectType: SnakeVision.ObjectType): Int = direction.value * OBJECT_TYPE_COUNT + objectType.value

fun SnakeObservationModel.vectorizedObservation(): FloatArray {

    val floatArray = FloatArray(SnakeVision.Direction.values().size * SnakeVision.ObjectType.values().size)

    this.foodObservation.vectorizedObservation(SnakeVision.ObjectType.FOOD, floatArray)
    this.wallObservation.vectorizedObservation(SnakeVision.ObjectType.WALL, floatArray)
    this.bodyObservation.vectorizedObservation(SnakeVision.ObjectType.BODY, floatArray)

    return floatArray
}


fun DirectionalObservation.vectorizedObservation(
    objectType: SnakeVision.ObjectType,
    floatArray: FloatArray) {
    floatArray[positionFor(SnakeVision.Direction.LEFT, objectType)] = this.left
    floatArray[positionFor(SnakeVision.Direction.RIGHT, objectType)] = this.right
    floatArray[positionFor(SnakeVision.Direction.UP, objectType)] = this.up
    floatArray[positionFor(SnakeVision.Direction.DOWN, objectType)] = this.down
    floatArray[positionFor(SnakeVision.Direction.LEFT_DOWN, objectType)] = this.leftDown
    floatArray[positionFor(SnakeVision.Direction.LEFT_UP, objectType)] = this.leftUp
    floatArray[positionFor(SnakeVision.Direction.RIGHT_UP, objectType)] = this.rightUp
    floatArray[positionFor(SnakeVision.Direction.RIGHT_DOWN, objectType)] = this.rightDown
}
