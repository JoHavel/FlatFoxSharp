package file

import logic.Board
import logic.Colors

fun save(
    colors: Colors,
    board: Board,
    black: Long,
    input: String
): String =
    buildString {
        colors.forEach { append("${it.color} = ${it.value}\n") }
        append('\n')
        board.forEach { row ->
            append(row.joinToString(",") { it.toString() })
            append('\n')
        }
        append('\n')
        append("black = $black\n")
        append("input = \"\"\"$input\"\"\"")
    }
