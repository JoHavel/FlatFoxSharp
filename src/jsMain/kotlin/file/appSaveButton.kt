package file

import kotlinx.browser.window
import react.ChildrenBuilder
import react.FC
import react.Props
import react.dom.html.ReactHTML
import org.w3c.dom.url.URL
import org.w3c.files.File
import org.w3c.files.FilePropertyBag

import app.Board
import logic.Colors

private external interface SaveProps : Props {
    var colors: Colors
    var board: Board
    var black: Long
    var input: String
}

private val saveButton_ = FC<SaveProps> { props ->
    ReactHTML.button {
        +"Uložit"
        onClick = {
            val file = save(props.colors, props.board, props.black, props.input)
            val blob =
                File(arrayOf(file), "program.ffs", FilePropertyBag(undefined, "text/ffs"))
            val url = URL.createObjectURL(blob)
            window.setTimeout({ URL.revokeObjectURL(url) }, 60000)
            window.open(url)
        }
    }
}

fun ChildrenBuilder.saveButton(colors: Colors, board: Board, black: Long, input: String) = saveButton_ {
    this.colors = colors
    this.board = board
    this.black = black
    this.input = input
}