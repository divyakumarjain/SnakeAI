package org.divy.ai.snake.animation.chart

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import org.divy.ai.snake.model.engine.qlearning.EpisodeCompleted
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType
import org.divy.ai.snake.model.game.GameBoardModel
import org.divy.ai.snake.model.game.GameEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ProgressChartController(private val gameModel: GameBoardModel) : GameEventListener {

    init {
        gameModel.addEventListener(EventType.EPISODE_COMPLETED,this)
    }

    @FXML
    var scoreLineChart: LineChart<String, Number>? = null

    @FXML
    var fitnessScoreLineChart: LineChart<String, Number>? = null

    private val averageScoreSeries = Series<String?, Number?>()
    private val highScoreSeries = Series<String?, Number?>()
    private val lowScoreSeries = Series<String?, Number?>()

    private val averageFitnessScoreSeries = Series<String?, Number?>()
    private val highestFitnessScoreSeries = Series<String?, Number?>()

    fun initialize() {

        averageScoreSeries.name= "Average Score"
        highScoreSeries.name= "High Score"
        lowScoreSeries.name= "Low Score"

        averageFitnessScoreSeries.name = "Average Fitness"
        highestFitnessScoreSeries.name = "Highest Fitness"

        scoreLineChart?.data?.add(averageScoreSeries)
        scoreLineChart?.data?.add(highScoreSeries)
        scoreLineChart?.data?.add(lowScoreSeries)

        fitnessScoreLineChart?.data?.add(averageFitnessScoreSeries)
        fitnessScoreLineChart?.data?.add(highestFitnessScoreSeries)
    }

    fun updateData() {
        updateData(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
    }

    fun updateData(xAxisValue: String) {

        cleanUpSeries(averageScoreSeries)
        cleanUpSeries(highScoreSeries)
        cleanUpSeries(lowScoreSeries)
        cleanUpSeries(averageFitnessScoreSeries)
        cleanUpSeries(highestFitnessScoreSeries)

        averageScoreSeries.data.add(
            XYChart.Data<String?, Number?>(
                xAxisValue, gameModel.averageScore
            )
        )
        highScoreSeries.data.add(
            XYChart.Data<String?, Number?>(
                xAxisValue, gameModel.highScore
            )
        )
        lowScoreSeries.data.add(
            XYChart.Data<String?, Number?>(
                xAxisValue, gameModel.lowScore
            )
        )

        averageFitnessScoreSeries.data.add(
            XYChart.Data<String?, Number?>(
                xAxisValue, gameModel.averageFitness
            )
        )

        highestFitnessScoreSeries.data.add(
            XYChart.Data<String?, Number?>(
                xAxisValue, gameModel.highestFitness
            )
        )
    }

    private fun cleanUpSeries(series: Series<String?, Number?>) {
        if (series.data.size > 600)
            series.data.removeAt(0)
    }

    override fun handleEvent(event: Event) {
        if(event is EpisodeCompleted) {
            updateData(event.episode.toString())
        }
    }
}
