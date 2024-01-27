package file

import emotion.react.css
import react.ChildrenBuilder
import react.FC
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML
import web.cssom.*
import web.file.FileReader
import web.html.InputType

import app.Board
import app.Hack
import app.hack
import logic.Colors

private external interface LoadProps : Props {
    var setColors: Hack<(Colors) -> Unit>
    var setBoard: Hack<(Board) -> Unit>
    var setBlack: Hack<(Long) -> Unit>
    var setInput: Hack<(String) -> Unit>
}

private val loadButton_ = FC<LoadProps> { props ->
    ReactHTML.span {
        css {
            border = Border(2.pt, LineStyle.solid, Color("lightgray"))
            padding = 5.pt
        }
        ReactHTML.label {
            css { fontWeight = FontWeight.bold }
            +"Načíst:"
        }
        +" "
        ReactHTML.input {
            type = InputType.file
            css { width = Length.fitContent }
            onChange = {
                val reader = FileReader()
                reader.onload = { event ->
                    println(event.target)
                    try {
                        val (colors, board, black, input) = load(reader.result as String)
                        props.setColors.hack(colors)
                        props.setBoard.hack(board)
                        props.setBlack.hack(black)
                        props.setInput.hack(input)
                    } catch (_: ParseException) {
                    }
                }
                if (it.target.files != null)
                    reader.readAsText(it.target.files!![0])
            }
        }
    }
}

fun ChildrenBuilder.loadButton(
    setColors: Hack<(Colors) -> Unit>,
    setBoard: Hack<(Board) -> Unit>,
    setBlack: Hack<(Long) -> Unit>,
    setInput: Hack<(String) -> Unit>,
) = loadButton_ {
    this.setColors = setColors
    this.setBoard = setBoard
    this.setBlack = setBlack
    this.setInput = setInput
}

fun ChildrenBuilder.loadButton(
    setColors: StateSetter<Colors>,
    setBoard: StateSetter<Board>,
    setBlack: StateSetter<Long>,
    setInput: StateSetter<String>,
) = loadButton(setColors.hack, setBoard.hack, setBlack.hack, setInput.hack)
