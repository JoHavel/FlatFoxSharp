package app

import logic.*
import react.ChildrenBuilder
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useState
import web.html.InputType

//                            for (row in board.indices)
//                                for (column in board[row].indices)
//                                    if (board[row][column] == Start)
//                                        board = board.mapOne(row, column) { Empty() }

external interface PickerProps : Props {
    var chooseTile: Hack<(() -> Tile) -> Unit>
    var setColor: Hack<(Int, Int) -> Unit>
    var colors: Colors
}

val tiles = listOf<(Int?, Int, Position) -> Tile>(
    { colorIndex, _, _ -> if (colorIndex == null) Start else Inc(colorIndex) },
    { colorIndex, _, _ -> if (colorIndex == null) End() else Dec(colorIndex) },
) + enumValues<Direction>().map { direction ->
    { colorIndex, _, _ -> Arrow(colorIndex, direction) }
} + listOf(
    { colorIndex, value, _ -> if (colorIndex == null) Empty() else Constant(colorIndex, value) },
    { colorIndex, _, _ -> if (colorIndex == null) Empty() else Store(colorIndex) },
    { colorIndex, _, pos -> if (colorIndex == null) Jump(pos) else Plus(colorIndex) },
    { colorIndex, _, _ -> if (colorIndex == null) Empty() else Minus(colorIndex) },
    { colorIndex, _, _ -> if (colorIndex == null) Empty() else Times(colorIndex) },
    { colorIndex, _, _ -> if (colorIndex == null) Empty() else Div(colorIndex) },
    { colorIndex, _, _ -> if (colorIndex == null) Empty() else Rem(colorIndex) },
    { colorIndex, _, _ -> if (colorIndex == null) Empty() else Input(colorIndex) },
    { colorIndex, _, _ -> if (colorIndex == null) Empty() else Output(colorIndex) },
)

val drawPicker = FC<PickerProps> { props ->
    fun ChildrenBuilder.drawTile(tile: Tile) {
        drawTile { this.tile = tile; colors = props.colors }
    }

    var color by useState(-1)
    var tileIndex by useState(-1)

    fun reset() {
        props.chooseTile.hack { Empty() }
        color = -1
        tileIndex = -1
    }

    var constValue by useState(0)
    var jumpPosition by useState(Position(6 - 1, 42 - 1))

    ReactHTML.table {
        ReactHTML.tr {
            ReactHTML.td {
                ReactHTML.button {
                    if (color == -1 && tileIndex == -1) disabled = true
                    drawTile(Empty())
                    onClick = { reset() }
                }
            }
            props.colors.forEachIndexed { index, color ->
                ReactHTML.td {
                    ReactHTML.input {
                        type = InputType.number
                        value = color.value.toString()
                        onInput = {
                            try {
                                props.setColor.hack(index, it.target.asDynamic().value.toString().toInt())
                            } catch (_: Exception) {
                            }
                        }
                    }
                }
            }
        }

        tiles.forEachIndexed { tileI, tileGenerator ->
            ReactHTML.tr {
                ReactHTML.td {
                    when (tileI) {
                        6 -> {
                            ReactHTML.input {
                                type = InputType.number
                                value = constValue
                                onInput = {
                                    reset()
                                    try {
                                        constValue = it.target.asDynamic().value.toString().toInt()
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                        }

                        9 -> {
                            ReactHTML.input {
                                type = InputType.number
                                value = jumpPosition.row + 1
                                onInput = {
                                    reset()
                                    try {
                                        jumpPosition = jumpPosition.assignRow(
                                            it.target.asDynamic().value.toString().toInt() - 1
                                        )
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                        }

                        10 -> {
                            ReactHTML.input {
                                type = InputType.number
                                value = jumpPosition.column + 1
                                onInput = {
                                    reset()
                                    try {
                                        jumpPosition = jumpPosition.assignColumn(
                                            it.target.asDynamic().value.toString().toInt() - 1
                                        )
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                        }

                        else -> {
                            ReactHTML.button {
                                if (color == -1 && tileIndex == tileI) disabled = true
                                drawTile(tileGenerator(null, constValue, jumpPosition))
                                onClick = {
                                    tileIndex = tileI
                                    color = -1
                                    props.chooseTile.hack { tileGenerator(null, constValue, jumpPosition) }
                                }
                            }
                        }
                    }
                }

                props.colors.indices.forEach { colorIndex ->
                    ReactHTML.td {
                        ReactHTML.button {
                            if (color == colorIndex && tileIndex == tileI) disabled = true
                            drawTile(tileGenerator(colorIndex, constValue, jumpPosition))
                            onClick = {
                                tileIndex = tileI
                                color = colorIndex
                                props.chooseTile.hack { tileGenerator(colorIndex, constValue, jumpPosition) }
                            }
                        }
                    }
                }
            }
        }
    }
}
