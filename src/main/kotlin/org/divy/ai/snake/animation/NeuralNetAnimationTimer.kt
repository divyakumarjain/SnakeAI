package org.divy.ai.snake.animation

import javafx.animation.AnimationTimer
import org.divy.ai.snake.model.engine.NeuralNetDecisionEngine

class NeuralNetAnimationTimer(val decisionEngine: NeuralNetDecisionEngine) : AnimationTimer() {


//    fun show(
//        x: Float,
//        y: Float,
//        w: Float,
//        h: Float,
//        vision: FloatArray,
//        decision: FloatArray
//    )

    override fun handle(now: Long) {
//        val space = 5f
//        val nSize = (h - space * (iNodes - 2)) / iNodes
//        val nSpace = (w - weights.size * nSize) / weights.size
//        val hBuff = (h - space * (hNodes - 1) - nSize * hNodes) / 2
//        val oBuff = (h - space * (oNodes - 1) - nSize * oNodes) / 2
//        var maxIndex = 0
//        for (i in 1 until decision.size) {
//            if (decision[i] > decision[maxIndex]) {
//                maxIndex = i
//            }
//        }
//        var lc = 0 //Layer Count
//
//        //DRAW NODES
//        for (i in 0 until iNodes) {  //DRAW INPUTS
//            if (vision[i] != 0) {
//                fill(0, 255, 0)
//            } else {
//                fill(255)
//            }
//            stroke(0)
//            ellipseMode(CORNER)
//            ellipse(x, y + i * (nSize + space), nSize, nSize)
//            textSize(nSize / 2)
//            textAlign(CENTER, CENTER)
//            fill(0)
//            text(i, x + nSize / 2, y + nSize / 2 + i * (nSize + space))
//        }
//        lc++
//        for (a in 0 until hLayers) {
//            for (i in 0 until hNodes) {  //DRAW HIDDEN
//                fill(255)
//                stroke(0)
//                ellipseMode(CORNER)
//                ellipse(x + lc * nSize + lc * nSpace, y + hBuff + i * (nSize + space), nSize, nSize)
//            }
//            lc++
//        }
//        for (i in 0 until oNodes) {  //DRAW OUTPUTS
//            if (i == maxIndex) {
//                fill(0, 255, 0)
//            } else {
//                fill(255)
//            }
//            stroke(0)
//            ellipseMode(CORNER)
//            ellipse(x + lc * nSpace + lc * nSize, y + oBuff + i * (nSize + space), nSize, nSize)
//        }
//        lc = 1
//
//        //DRAW WEIGHTS
//        for (i in 0 until weights[0].rows) {  //INPUT TO HIDDEN
//            for (j in 0 until weights[0].cols - 1) {
//                if (weights[0].matrix.get(i).get(j) < 0) {
//                    stroke(255, 0, 0)
//                } else {
//                    stroke(0, 0, 255)
//                }
//                line(
//                    x + nSize,
//                    y + nSize / 2 + j * (space + nSize),
//                    x + nSize + nSpace,
//                    y + hBuff + nSize / 2 + i * (space + nSize)
//                )
//            }
//        }
//        lc++
//        for (a in 1 until hLayers) {
//            for (i in 0 until weights[a].rows) {  //HIDDEN TO HIDDEN
//                for (j in 0 until weights[a].cols - 1) {
//                    if (weights[a].matrix.get(i).get(j) < 0) {
//                        stroke(255, 0, 0)
//                    } else {
//                        stroke(0, 0, 255)
//                    }
//                    line(
//                        x + lc * nSize + (lc - 1) * nSpace,
//                        y + hBuff + nSize / 2 + j * (space + nSize),
//                        x + lc * nSize + lc * nSpace,
//                        y + hBuff + nSize / 2 + i * (space + nSize)
//                    )
//                }
//            }
//            lc++
//        }
//        for (i in 0 until weights[weights.size - 1].rows) {  //HIDDEN TO OUTPUT
//            for (j in 0 until weights[weights.size - 1].cols - 1) {
//                if (weights[weights.size - 1].matrix.get(i).get(j) < 0) {
//                    stroke(255, 0, 0)
//                } else {
//                    stroke(0, 0, 255)
//                }
//                line(
//                    x + lc * nSize + (lc - 1) * nSpace,
//                    y + hBuff + nSize / 2 + j * (space + nSize),
//                    x + lc * nSize + lc * nSpace,
//                    y + oBuff + nSize / 2 + i * (space + nSize)
//                )
//            }
//        }
//        fill(0)
//        textSize(15)
//        textAlign(CENTER, CENTER)
//        text("U", x + lc * nSize + lc * nSpace + nSize / 2, y + oBuff + nSize / 2)
//        text("D", x + lc * nSize + lc * nSpace + nSize / 2, y + oBuff + space + nSize + nSize / 2)
//        text("L", x + lc * nSize + lc * nSpace + nSize / 2, y + oBuff + 2 * space + 2 * nSize + nSize / 2)
//        text("R", x + lc * nSize + lc * nSpace + nSize / 2, y + oBuff + 3 * space + 3 * nSize + nSize / 2)
    }
}
