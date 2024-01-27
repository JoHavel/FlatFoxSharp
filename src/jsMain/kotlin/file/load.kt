package file

import logic.Board
import js.array.JsTuple4
import js.array.tupleOf
import logic.*

class ParseException : Exception()

fun load(file: String): JsTuple4<Colors, Board, Long, String> {
    val lines = file.splitToSequence('\n').map { it.trim() }
    val iterator = lines.iterator()
    fun next(): String {
        if (!iterator.hasNext()) throw ParseException()
        return iterator.next()
    }

    val colors = mutableListOf<Color>()
    var line = next()
    while (line.isNotBlank()) {
        val splitted = line.split(" = ")
        if (splitted.size != 2) throw ParseException()
        val value = splitted[1].toLongOrNull() ?: throw ParseException()
        colors.add(Color(splitted[0], value, value)) // FIXME possible security bug! (Can escape from css `color:`)
        line = next()
    }

    fun String.getColor(): Int {
        val color = toIntOrNull() ?: throw ParseException()
        if (color < 0 || color >= colors.size) throw ParseException()
        return color
    }

    line = next()
    val board = mutableListOf<List<Tile>>()
    while (line.isNotBlank()) {
        board.add(line.split(',').map { it.trim() }.map { strTile ->
            when (strTile) {
                "" -> Empty()
                "@" -> Start
                "#" -> End()
                else -> {
                    string2Direction(strTile[0].toString())?.also {
                        if (strTile.length == 1) return@map Arrow(null, it)
                        return@map Arrow(strTile.substring(1).getColor(), it)
                    }
                    if ('=' in strTile) {
                        val splitted = strTile.split('=')
                        if (splitted.size != 2) throw ParseException()
                        return@map Constant(
                            splitted[0].getColor(),
                            splitted[1].toLongOrNull() ?: throw ParseException()
                        )
                    }
                    if (':' in strTile) {
                        val splitted = strTile.split(':')
                        if (splitted.size != 2) throw ParseException()
                        return@map Jump(
                            Position(
                                splitted[0].toIntOrNull() ?: throw ParseException(),
                                splitted[1].toIntOrNull() ?: throw ParseException()
                            )
                        )
                    }
                    if (strTile.length > 1 && strTile[1] == 'O')
                        return@map when (strTile[0]) {
                            '+' -> Plus(strTile.substring(2).getColor())
                            '-' -> Minus(strTile.substring(2).getColor())
                            '.' -> Times(strTile.substring(2).getColor())
                            '/' -> Div(strTile.substring(2).getColor())
                            '%' -> Rem(strTile.substring(2).getColor())
                            else -> throw ParseException()
                        }
                    when (strTile[0]) {
                        '+' -> Inc(strTile.substring(1).getColor())
                        '-' -> Dec(strTile.substring(1).getColor())
                        'O' -> Store(strTile.substring(1).getColor())
                        'o' -> Output(strTile.substring(1).getColor())
                        'i' -> Input(strTile.substring(1).getColor())
                        else -> throw ParseException()
                    }
                }
            }
        })
        line = next()
    }

    line = next()
    val splittedBlack = line.split(" = ")
    if (splittedBlack.size != 2 || splittedBlack[0] != "black") throw ParseException()
    val black = splittedBlack[1].toLongOrNull() ?: throw ParseException()
    line = next()
    val splittedInput = line.split(" = ")
    if (splittedInput.size != 2 || splittedInput[0] != "input" || !splittedInput[1].startsWith("\"\"\"")) throw ParseException()

    val inputSearch = file.split("\"\"\"")
    if (inputSearch.size < 3) throw ParseException()
    val input = inputSearch.subList(1, inputSearch.size - 1).joinToString("\"\"\"") + inputSearch.last().trim()

    return tupleOf(colors, board, black, input)
}