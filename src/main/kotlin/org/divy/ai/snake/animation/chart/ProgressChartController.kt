package org.divy.ai.snake.animation.chart

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.layout.FlowPane
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

    private val scoreSeries: MutableMap<String, Series<String?, Number?>> = mutableMapOf()

    @FXML
    var flowPane: FlowPane? = null

    fun updateData(xAxisValue: String, stats: Map<String, Number>) {
        stats.forEach {

            var series = scoreSeries[it.key]

            if(series==null) {
                series = Series()
                scoreSeries[it.key] = series
                val categoryAxis = CategoryAxis()
                categoryAxis.label = "Episode"
                val yAxis = NumberAxis()
                yAxis.label = "Count"
                val lineChart = LineChart(categoryAxis, yAxis)
                lineChart.data.add(series)
                lineChart.title = it.key
                lineChart.minWidth = 900.0
                lineChart.prefWidth = 1500.0
                lineChart.minHeight = 450.0

                flowPane?.children?.add(lineChart)
            }

            cleanUpSeries(series)

            series.data.add(            XYChart.Data<String?, Number?>(
                xAxisValue, it.value
            ))
        }
    }

    private fun cleanUpSeries(series: Series<String?, Number?>) {
        if (series.data.size > 600)
            series.data.removeAt(0)
    }

    override fun handleEvent(event: Event) {
        if(event is EpisodeCompleted) {
            updateData(event.episode.toString(), event.stats)
        }
    }
}
