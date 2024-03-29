package logic

enum class Direction(val drow: Int, val dcolumn: Int, val string: String) {
    UP(-1, 0, "^"),
    DOWN(1, 0, "v"),
    LEFT(0, -1, "<"),
    RIGHT(0, 1, ">");
}

fun string2Direction(str: String): Direction? {
    for (direction in enumValues<Direction>()) if (direction.string == str) return direction
    return null
}

data class Position(val row: Int, val column: Int) {
    operator fun plus(direction: Direction) = Position(row + direction.drow, column + direction.dcolumn)
    fun assignRow(row: Int) = Position(row, column)
    fun assignColumn(column: Int) = Position(row, column)
}

