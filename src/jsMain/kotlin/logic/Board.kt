package logic

typealias Board = List<List<Tile>>

fun Board.withNewRow() = this + listOf(List(this[0].size) { Empty() })
fun Board.withNewColumn() = map { row -> row + Empty() }

fun Board.findStart(): Position? {
    forEachIndexed { rowI, row ->
        row.forEachIndexed { columnI, tile -> if (tile == Start) return Position(rowI, columnI) }
    }
    return null
}
