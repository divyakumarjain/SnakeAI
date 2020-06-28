package org.divy.ai.snake.animation.game

import com.github.ajalt.clikt.parameters.options.OptionWithValues
import javafx.fxml.FXML
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.util.converter.FloatStringConverter
import javafx.util.converter.IntegerStringConverter
import org.divy.ai.snake.model.engine.qlearning.AbstractLearningCommand
import kotlin.reflect.KMutableProperty

class GameScreenController(private val rlCommand: AbstractLearningCommand) {

    private val formControl: MutableMap<String, Control> = mutableMapOf()

    @FXML
    lateinit var controlPanel: VBox

    @FXML
    fun submit() {
        rlCommand.registeredOptions().forEach { option->
            val value = when(option.metavar) {
                "INT" -> {
                    IntegerStringConverter().fromString((formControl[option.names.first()] as TextField).text)
                }
                "FLOAT" -> {
                    FloatStringConverter().fromString((formControl[option.names.first()] as TextField).text)
                }
                else -> ""
            }

            val kProperty = rlCommand.trainingParam[option.names.first()]
            if(kProperty is KMutableProperty) {
                kProperty.setter.call(rlCommand, value)
            }
        }
    }

    @FXML
    fun initialize() {

        rlCommand.registeredOptions().forEach {

            controlPanel.children.add(Label(it.names.first()))
            val currentValue = if(it is OptionWithValues<*, *, *>) it.value.toString() else ""
            val textField = TextField(currentValue)
            controlPanel.children.add(textField)

            formControl[it.names.first()] = textField
        }
    }

}
