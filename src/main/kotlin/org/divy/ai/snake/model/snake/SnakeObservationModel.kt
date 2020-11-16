package org.divy.ai.snake.model.snake

data class SnakeObservationModel(
    val directionCount: Int = DIRECTION_TYPE_COUNT,
    val foodObservation: FloatArray = FloatArray(directionCount)
    , val bodyObservation: FloatArray = FloatArray(directionCount)
    , val wallObservation: FloatArray? = null
    , val headObservation: FloatArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SnakeObservationModel

        if (directionCount != other.directionCount) return false
        if (!foodObservation.contentEquals(other.foodObservation)) return false
        if (!bodyObservation.contentEquals(other.bodyObservation)) return false
        if (wallObservation != null) {
            if (other.wallObservation == null) return false
            if (!wallObservation.contentEquals(other.wallObservation)) return false
        } else if (other.wallObservation != null) return false
        if (headObservation != null) {
            if (other.headObservation == null) return false
            if (!headObservation.contentEquals(other.headObservation)) return false
        } else if (other.headObservation != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = directionCount
        result = 31 * result + foodObservation.contentHashCode()
        result = 31 * result + bodyObservation.contentHashCode()
        result = 31 * result + (wallObservation?.contentHashCode() ?: 0)
        result = 31 * result + (headObservation?.contentHashCode() ?: 0)
        return result
    }
}
fun SnakeObservationModel.floatVectorizedObservation(): FloatArray {

    val floatArray = FloatArray(directionCount * OBJECT_TYPE_COUNT)

    for(foodIndex in this.foodObservation.indices) {
        floatArray[ObjectType.FOOD.value * directionCount + foodIndex] = this.foodObservation[foodIndex]
    }

    for(bodyIndex in this.bodyObservation.indices) {
        floatArray[ObjectType.BODY.value * directionCount + bodyIndex] = this.bodyObservation[bodyIndex]
    }
    if(this.wallObservation != null) {
        for(wallIndex in this.wallObservation.indices) {
            floatArray[ObjectType.WALL.value * directionCount + wallIndex] = this.wallObservation[wallIndex]
        }
    }

    if(this.headObservation != null) {
        for(headIndex in this.headObservation.indices) {
            floatArray[ObjectType.WALL.value * directionCount + headIndex] = this.headObservation[headIndex]
        }
    }

    return floatArray
}

fun SnakeObservationModel.floatVectorizedObservation(depth:Int, height: Int, width:Int): FloatArray {

    val floatArray = FloatArray(depth*height*width)

    for(foodIndex in this.foodObservation.indices) {
        floatArray[ObjectType.FOOD.value * directionCount + foodIndex] = this.foodObservation[foodIndex]
    }

    for(bodyIndex in this.bodyObservation.indices) {
        floatArray[ObjectType.BODY.value * directionCount + bodyIndex] = this.bodyObservation[bodyIndex]
    }
    if(this.wallObservation != null) {
        for(wallIndex in this.wallObservation.indices) {
            floatArray[ObjectType.WALL.value * directionCount + wallIndex] = this.wallObservation[wallIndex]
        }
    }

    if(this.headObservation != null) {
        for(headIndex in this.headObservation.indices) {
            floatArray[ObjectType.WALL.value * directionCount + headIndex] = this.headObservation[headIndex]
        }
    }

    return floatArray
}



fun SnakeObservationModel.doubleVectorizedObservation(): DoubleArray {

    val array = DoubleArray(directionCount * OBJECT_TYPE_COUNT)

    for(foodIndex in this.foodObservation.indices) {
        array[ObjectType.FOOD.value * directionCount + foodIndex] = this.foodObservation[foodIndex].toDouble()
    }

    for(bodyIndex in this.bodyObservation.indices) {
        array[ObjectType.BODY.value * directionCount + bodyIndex] = this.bodyObservation[bodyIndex].toDouble()
    }
    if(this.wallObservation != null) {
        for(wallIndex in this.wallObservation.indices) {
            array[ObjectType.WALL.value * directionCount + wallIndex] = this.wallObservation[wallIndex].toDouble()
        }
    }

    if(this.headObservation != null) {
        for(headIndex in this.headObservation.indices) {
            array[ObjectType.WALL.value * directionCount + headIndex] = this.headObservation[headIndex].toDouble()
        }
    }

    return array
}
