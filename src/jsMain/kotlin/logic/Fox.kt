package logic

import app.Board

fun getFox(position: Position): Fox = Fox(position, Direction.RIGHT)

class Fox(val position: Position, private val direction: Direction, val output: String = "", val inputIndex: Int = 0) {
    fun go(): Fox = Fox(position + direction, direction, output, inputIndex)
    fun go(direction: Direction): Fox = Fox(position + direction, direction, output, inputIndex)
    fun go(position: Position): Fox = Fox(position, direction, output, inputIndex)
    fun go(char: Char): Fox = Fox(position + direction, direction, output + char, inputIndex)
    fun go(input: String): Pair<Fox, Char> =
        Fox(position + direction, direction, output, inputIndex + 1) to
                if (input.length <= inputIndex) Char(0) else input[inputIndex]
}

fun ProgramState.go(board: Board): Boolean {
    fox?.also {
        val row = it.position.row
        val column = it.position.column
        if (
            0 <= row && row < board.size &&
            0 <= column && column < board[row].size &&
            board[row][column] !is End
        ) {
            board[row][column].go(this)
            return true
        }
    }
    return false
}