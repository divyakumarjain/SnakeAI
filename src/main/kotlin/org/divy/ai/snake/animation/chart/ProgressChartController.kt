package org.divy.ai.snake.animation.chart

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import org.divy.ai.snake.model.game.GameBoardModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ProgressChartController(private val gameModel: GameBoardModel) {

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

        val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

        // put dummy data onto graph per second

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate({
            // Update the chart
            Platform.runLater {
                updateData()
            }
        }, 0, 1, TimeUnit.SECONDS)
    }

    fun updateData() {

        cleanUpSeries(averageScoreSeries)
        cleanUpSeries(highScoreSeries)
        cleanUpSeries(lowScoreSeries)
        cleanUpSeries(averageFitnessScoreSeries)
        cleanUpSeries(highestFitnessScoreSeries)

        val currentTimeStamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm:ss")
        )
        averageScoreSeries.data.add(
            XYChart.Data<String?, Number?>(
                currentTimeStamp, gameModel.averageScore
            )
        )
        highScoreSeries.data.add(
            XYChart.Data<String?, Number?>(
                currentTimeStamp, gameModel.highScore
            )
        )
        lowScoreSeries.data.add(
            XYChart.Data<String?, Number?>(
                currentTimeStamp, gameModel.lowScore
            )
        )

        averageFitnessScoreSeries.data.add(
            XYChart.Data<String?, Number?>(
                currentTimeStamp, gameModel.averageFitness
            )
        )

        highestFitnessScoreSeries.data.add(
            XYChart.Data<String?, Number?>(
                currentTimeStamp, gameModel.highestFitness
            )
        )
    }

    private fun cleanUpSeries(series: Series<String?, Number?>) {
        if (series.data.size > 600)
            series.data.removeAt(0)
    }


}
