package org.divy.ai.snake.application.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.float
import com.github.ajalt.clikt.parameters.types.int
import org.divy.ai.snake.model.engine.qlearning.modifiable
import org.divy.ai.snake.model.food.FoodDropper
import org.divy.ai.snake.model.game.GameBoardModel
import kotlin.reflect.KProperty

abstract class AbstractLearningCommand(
    help: String = "",
    epilog: String = "",
    name: String? = null,
    invokeWithoutSubcommand: Boolean = false,
    printHelpOnEmptyArgs: Boolean = false,
    helpTags: Map<String, String> = emptyMap(),
    autoCompleteEnvvar: String? = "",
    allowMultipleSubcommands: Boolean = false,
    treatUnknownOptionsAsArgs: Boolean = false
) : CliktCommand(
    help = help,
    epilog = epilog,
    name = name,
    invokeWithoutSubcommand = invokeWithoutSubcommand,
    printHelpOnEmptyArgs = printHelpOnEmptyArgs,
    helpTags = helpTags,
    autoCompleteEnvvar = autoCompleteEnvvar,
    allowMultipleSubcommands = allowMultipleSubcommands,
    treatUnknownOptionsAsArgs = treatUnknownOptionsAsArgs
) {
    lateinit var environment: GameBoardModel
    lateinit var foodDropper: FoodDropper
    val trainingParam: MutableMap<String, KProperty<*>> = mutableMapOf()

    val boardWidth by option(help = "Board width").int().default(20)
        .validate {
            require(it > 0) {
                "Board width should be a positive"
            }
        }

    val boardHeight by option(help = "Board height").int().default(20)
        .validate {
            require(it > 0) {
                "Board height should be a positive"
            }
        }

    var learningRate by (option(help = "Learning Rate").float().default(0.1F)
        .validate {
            require(it > 0.0f && it <=1.0f) {
                "Learning Rate should be between 0 and 1"
            }
        }.modifiable())

    var gamaRate by option(help = "Discount factor for future rewords (aka. Gama)").float().default(0.8F)
        .validate {
            require(it > 0.0f && it <=1.0f) {
                "Gama should be between 0 and 1"
            }
        }.modifiable()

    var numEpisodes by option(help = "Number of Episodes for learning").int().default(200)
        .validate {
            require(it > 0) {
                "Number of Episodes should be a positive"
            }
        }.modifiable()

    var delayInMillis by option(help = "Delay in animation").int().default(200)
        .validate {
            require(it > 0) {
                "Delay in animation should be positive"
            }
        }.modifiable()

    var randomFactor by option(help = "Random move selection factor").float().default(0.2f)
        .validate {
            require(it in 0.0f..1.0f) {
                "Random move selection should be between 0 and 1"
            }
        }.modifiable()

    var numFoodDrops by option(help = "number of food dropped").int().default(5)
        .validate {
            require(it > 0) {
                "Number of food dropped should be positive"
            }
        }.modifiable()

    var cellResolution by option(help = "Cell Resolution for Snake Game").double().default(15.0)
        .validate {
            require(it > 0) {
                "Cell Resolution should be positive and non zero"
            }
        }.modifiable()

    val useFourDirection: Boolean by option(help = "Buffer size for experience learning").flag()
    val useEightDirection: Boolean by option(help = "Buffer size for experience learning").flag()

    var randomDecay by option(help = "Random Decay rate").float().default(0.9999f)
        .validate {
            require(it > 0 && it <= 1) {
                "Random Decay rate for experience learning should be positive and less then equal to 1"
            }
        }.modifiable()

    var episode: Int = 0
}
