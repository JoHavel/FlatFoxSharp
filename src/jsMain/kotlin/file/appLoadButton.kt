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

import logic.Board
import logic.Colors
import function

private external interface LoadProps : Props {
    var setColors: (Colors) -> Unit
    var setBoard: (Board) -> Unit
    var setBlack: (Long) -> Unit
    var setInput: (String) -> Unit
}

private val loadButton_ = FC<LoadProps> { props ->
    ReactHTML.span {
        className = ClassName("load")
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
                        props.setColors(colors)
                        props.setBoard(board)
                        props.setBlack(black)
                        props.setInput(input)
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
    setColors: (Colors) -> Unit,
    setBoard: (Board) -> Unit,
    setBlack: (Long) -> Unit,
    setInput: (String) -> Unit,
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
) = loadButton(setColors.function, setBoard.function, setBlack.function, setInput.function)
