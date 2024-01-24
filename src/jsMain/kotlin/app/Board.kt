package app

import logic.Colors
import logic.Fox
import logic.Tile
import logic.drawTile
import react.*
import react.dom.html.ReactHTML

typealias Board = List<List<Tile>>

external interface BoardProps : Props {
    var board: Board
    var fox: Fox?
    var chooseTile: (Int, Int) -> Unit
    var colors: Colors
}

val drawBoard = FC<BoardProps>("Board") { props ->
    ReactHTML.table {
        props.board.forEachIndexed { rowI, row ->
            ReactHTML.tr {
                for ((columnI, cell) in row.withIndex()) {
                    ReactHTML.td {
                        ReactHTML.button {
                            onClick = { props.chooseTile(rowI, columnI) }
                            drawTile {
                                tile = cell
                                colors = props.colors
                            }
                            if (props.fox != null && props.fox!!.position.row == rowI && props.fox!!.position.column == columnI) {
                                ReactHTML.span {
                                    +"🦊"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

