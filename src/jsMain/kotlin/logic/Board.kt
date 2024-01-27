package logic

typealias Board = List<List<Tile>>

fun Board.withNewRow() = this + listOf(List(this[0].size) { Empty() })
fun Board.withNewColumn() = map { row -> row + Empty() }

