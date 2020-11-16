package org.divy.ai.snake.application

import com.github.ajalt.clikt.core.subcommands
import org.assertj.core.api.Assertions.assertThat
import org.divy.ai.snake.application.command.SnakeBoardRlCommand

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class SnakeBoardRlQTableCommandTest {

    val application: SnakeBoardRlCommand =
        SnakeBoardRlCommand()

    @Nested
    inner class CommandlineParserTest {
        @Test
        fun `Should Take Board Size Parameter`() {

            application.parse(arrayOf("--board-width", "20", "--board-height", "20"))
            assertThat(application).isEqualTo(20)
        }
    }

    @Nested
    inner class QTableLearningTest {
        @Test
        fun `Should have sub command for QTable Learning`() {
            application.subcommands()
        }
    }
}
