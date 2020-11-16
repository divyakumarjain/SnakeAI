package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.divy.ai.snake.model.snake.*
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j

fun DoubleArray.foldToMap(): Map<SnakeAction, Double> {
    val accumulator = mutableMapOf<SnakeAction, Double>()
    for ((index, qValue) in this.withIndex()) {
        accumulator[valueOfByInt(index)] = qValue
    }
    return accumulator
}

fun DoubleArray.toINDArray(size: Int): INDArray {
    val result = Nd4j.zeros(1, size)

    this.forEachIndexed{index, value ->
        result.putScalar(intArrayOf(0, index), value)
    }

    return result
}

private fun FloatArray.toINDArray(): INDArray {
    val result = Nd4j.zeros(1, size)

    this.forEachIndexed{index, value ->
        result.putScalar(intArrayOf(0, index), value)
    }
    return result
}

fun SnakeObservationModel.toINDArray(): INDArray {
    return this.floatVectorizedObservation().toINDArray()
}

fun SnakeObservationModel.toINDArray(depth:Int, height: Int, width:Int): INDArray {
    return this.floatVectorizedObservation(depth, height, width).toINDArray(depth, height, width)
}

private fun FloatArray.toINDArray(depth:Int, height: Int, width:Int): INDArray {
    val result = Nd4j.zeros(1, depth, height, width)

    for(x in 0 until depth)
        for(y in 0 until height)
            for(z in 0 until width)
                result.putScalar(intArrayOf(0, x, y, x), this[x*height*width + y*width + z])
    return result
}
