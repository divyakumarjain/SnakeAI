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

private fun FloatArray.toINDArray(size: Int): INDArray {
    val result = Nd4j.zeros(1, size)

    this.forEachIndexed{index, value ->
        result.putScalar(intArrayOf(0, index), value)
    }
    return result
}

fun SnakeObservationModel.toINDArray(): INDArray {
    return this.vectorizedObservation().toINDArray(SnakeVision.ObjectType.values().size * SnakeVision.Direction.values().size)
}
