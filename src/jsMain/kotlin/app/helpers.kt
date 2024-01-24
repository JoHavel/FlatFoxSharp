package app


fun <T> List<T>.mapOne(index: Int, what: (T) -> T): List<T> = mapIndexed { i, it ->
    if (index == i) what(it) else it
}

fun <T> List<List<T>>.mapOne(rowIndex: Int, columnIndex: Int, what: (T) -> T): List<List<T>> = mapIndexed { rowI, row ->
    if (rowIndex == rowI) row.mapOne(columnIndex, what) else row
}

data class Hack<T>(val hack: T)
typealias DiscardingFunction = Hack<(Any?) -> Unit>