package app

import function
import react.*
import react.dom.html.ReactHTML
import web.html.InputType

import logic.*

private external interface PickerProps : Props {
    var chooseTile: (Tile) -> Unit
    var setColor: (Int, Long) -> Unit
    var setColorName: (Int, String) -> Unit
    var colors: Colors
    var black: Long
    var setBlack: (Long) -> Unit
    var full: Boolean
}

private val tiles = listOf<(Int?, Long, Position) -> Tile>(
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

private val picker_ = FC<PickerProps> { props ->
    fun ChildrenBuilder.drawTile(tile: Tile) {
        drawTile { this.tile = tile; colors = props.colors }
    }

    var color by useState(-1)
    var tileIndex by useState(-1)

    fun reset() {
        props.chooseTile(Empty())
        color = -1
        tileIndex = -1
    }

    var constValue by useState(0L)
    var jumpPosition by useState(Position(6 - 1, 42 - 1))

    ReactHTML.table {
        if (props.full)
            ReactHTML.tr {
                ReactHTML.button {
                    if (color == -1 && tileIndex == -1) disabled = true
                    drawTile(Empty())
                    onClick = { reset() }
                }
                props.colors.forEachIndexed { index, color ->
                    ReactHTML.td {
                        ReactHTML.input {
                            type = InputType.text
                            value = color.color
                            onInput = {
                                props.setColorName(index, it.target.asDynamic().value.toString())
                            }
                        }
                    }
                }
            }
        ReactHTML.tr {
            ReactHTML.td {
                if (props.full)
                    ReactHTML.input {
                        type = InputType.number
                        value = props.black.toString()
                        onInput = {
                            try {
                                props.setBlack(it.target.asDynamic().value.toString().toLong())
                            } catch (_: Exception) {
                            }
                        }
                    }
                else
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
                                props.setColor(index, it.target.asDynamic().value.toString().toLong())
                            } catch (_: Exception) {
                            }
                        }
                    }
                }
            }
        }

        tiles.forEachIndexed { tileI, tileGenerator ->
            if (props.full || tileI < 6) {
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
                                            constValue = it.target.asDynamic().value.toString().toLong()
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
                                        props.chooseTile(tileGenerator(null, constValue, jumpPosition))
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
                                    props.chooseTile(tileGenerator(colorIndex, constValue, jumpPosition))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun ChildrenBuilder.picker(
    chooseTile: (Tile) -> Unit,
    setColor: (Int, Long) -> Unit,
    setColorName: (Int, String) -> Unit,
    colors: Colors,
    black: Long,
    setBlack: (Long) -> Unit,
    full: Boolean,
) = picker_ {
    this.chooseTile = chooseTile
    this.setColor = setColor
    this.setColorName = setColorName
    this.colors = colors
    this.black = black
    this.setBlack = setBlack
    this.full = full
}

fun ChildrenBuilder.picker(
    chooseTile: StateSetter<Tile>,
    setColor: (Int, Long) -> Unit,
    setColorName: (Int, String) -> Unit,
    colors: Colors,
    black: Long,
    setBlack: StateSetter<Long>,
    full: Boolean,
) = picker(
    chooseTile.function,
    setColor,
    setColorName,
    colors,
    black,
    setBlack.function,
    full,
)
