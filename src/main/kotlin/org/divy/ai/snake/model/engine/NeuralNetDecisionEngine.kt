package org.divy.ai.snake.model.engine

import org.divy.ai.snake.model.snake.*
import java.lang.Math.random
import kotlin.math.floor
import kotlin.math.max
import kotlin.random.Random


class NeuralNetDecisionEngine (var iNodes: Int = 24, var hNodes: Int = 16, var oNodes: Int = 4, var hLayers: Int = 2) :
    RegeneratableDecisionEngine {

    private var weights: Array<Matrix>

    init {
        val initializedWeights: Array<Matrix?> = arrayOfNulls(hLayers + 1)
        initializedWeights[0] = Matrix(hNodes, iNodes + 1)
        for (i in 1 until hLayers) {
            initializedWeights[i] = Matrix(hNodes, hNodes + 1)
        }
        initializedWeights[initializedWeights.size - 1] =
            Matrix(oNodes, hNodes + 1)
        for (w in initializedWeights) {
            w?.randomize()
        }

        weights = initializedWeights.filterNotNull().toTypedArray()
    }

    override fun output(observation: SnakeObservationModel): SnakeAction {
        val inputs: Matrix = weights[0].singleColumnMatrixFromArray(observation.vectorizedObservation())
        var currBias: Matrix = inputs.addBias()
        for (i in 0 until hLayers) {
            val hiddenIp: Matrix = weights[i].dot(currBias)
            val hiddenOp: Matrix = hiddenIp.activate()
            currBias = hiddenOp.addBias()
        }
        val outputIp: Matrix = weights[weights.size - 1].dot(currBias)
        val output: Matrix = outputIp.activate()

        var max = 0f
        var maxIndex = 0
        val decision = output.toArray()

        for (i in decision.indices) {
            if (decision[i] > max) {
                max = decision[i]
                maxIndex = i
            }
        }

        return valueOfByInt(maxIndex)
    }

    override fun crossover(engine: DecisionEngine): NeuralNetDecisionEngine {
        if(engine is NeuralNetDecisionEngine) {
            val child = NeuralNetDecisionEngine(iNodes, hNodes, oNodes, hLayers)
            for (i in weights.indices) {
                child.weights[i] = weights[i].crossover(engine.weights[i])
            }
            return child
        } else {
            throw IllegalStateException("Cannot crossover DecisionEngine which are not of type NeuralNetModel")
        }

    }

    override fun mutate(mutationRate: Float) {
        for (w in weights) {
            w.mutate(mutationRate)
        }
    }

    fun load(weight: Array<Matrix>) {
        for (i in weights.indices) {
            weights[i] = weight[i]
        }
    }

    fun pull(): Array<Matrix> {
        return weights.clone()
    }
}

data class Matrix (val rows: Int, val cols: Int,  val matrix: Array<FloatArray> = Array(rows) { FloatArray(cols) }) {

    constructor(m: Array<FloatArray>) : this (m.size, m[0].size, m)

    fun output() {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                print(matrix[i][j].toString() + " ")
            }
            println()
        }
        println()
    }

    fun dot(n: Matrix): Matrix {
        val result = Matrix(rows, n.cols)
        if (cols == n.rows) {
            for (i in 0 until rows) {
                for (j in 0 until n.cols) {
                    var sum = 0f
                    for (k in 0 until cols) {
                        sum += matrix[i][k] * n.matrix[k][j]
                    }
                    result.matrix[i][j] = sum
                }
            }
        }
        return result
    }

    fun randomize() {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                matrix[i][j] = Random.nextDouble(-1.0, 1.0).toFloat()
            }
        }
    }

    fun singleColumnMatrixFromArray(arr: FloatArray): Matrix {
        val n = Matrix(arr.size, 1)
        for (i in arr.indices) {
            n.matrix[i][0] = arr[i]
        }
        return n
    }

    fun toArray(): FloatArray {
        val arr = FloatArray(rows * cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                arr[j + i * cols] = matrix[i][j]
            }
        }
        return arr
    }

    fun addBias(): Matrix {
        val n = Matrix(rows + 1, 1)
        for (i in 0 until rows) {
            n.matrix[i][0] = matrix[i][0]
        }
        n.matrix[rows][0] = 1F
        return n
    }

    fun activate(): Matrix {
        val n = Matrix(rows, cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                n.matrix[i][j] = relu(matrix[i][j])
            }
        }
        return n
    }

    fun relu(x: Float): Float {
        return max(0f, x)
    }

    fun mutate(mutationRate: Float) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val rand: Float = java.util.Random().nextFloat()
                if (rand < mutationRate) {
                    matrix[i][j] += (java.util.Random().nextGaussian() / 10).toFloat()
                    if (matrix[i][j] > 1f) {
                        matrix[i][j] = 1f
                    }
                    if (matrix[i][j] < -1) {
                        matrix[i][j] = (-1).toFloat()
                    }
                }
            }
        }
    }

    fun crossover(partner: Matrix): Matrix {
        val child = Matrix(rows, cols)
        val randC: Int = floor(random() * cols.toDouble()).toInt()
        val randR: Int = floor(random() * rows.toDouble()).toInt()
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (i < randR || i == randR && j <= randC) {
                    child.matrix[i][j] = matrix[i][j]
                } else {
                    child.matrix[i][j] = partner.matrix[i][j]
                }
            }
        }
        return child
    }
}

