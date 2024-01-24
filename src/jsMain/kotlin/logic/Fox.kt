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

fun go(black: Int, colors: Colors, fox: Fox?, board: Board, input: String): Pair<Pair<Int, Colors>, Fox?> {
    if (fox != null) {
        val row = fox.position.row
        val column = fox.position.column
        if (
            0 <= row && row < board.size &&
            0 <= column && column < board[row].size &&
            board[row][column] !is End
        ) {
            return board[row][column].go(black, colors, fox, input)
        }
    }
    return black to colors to null
}