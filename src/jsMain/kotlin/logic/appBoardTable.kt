package logic

import react.ChildrenBuilder
import react.FC
import react.Props
import react.dom.html.ReactHTML

private external interface BoardProps : Props {
    var colors: Colors
    var board: Board
    var fox: Fox?
    var chooseTile: (Int, Int) -> Unit
}

private val boardTable_ = FC<BoardProps>("Board") { props ->
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

fun ChildrenBuilder.boardTable(colors: Colors, board: Board, fox: Fox?, chooseTile: (Int, Int) -> Unit) = boardTable_ {
    this.colors = colors
    this.board = board
    this.fox = fox
    this.chooseTile = chooseTile
}
