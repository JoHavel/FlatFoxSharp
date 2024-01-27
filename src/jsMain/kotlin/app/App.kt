package app

import kotlinx.browser.window
import react.*
import react.dom.html.ReactHTML

import logic.*
import logic.Color
import logic.Direction
import logic.Position
import file.loadButton
import file.saveButton


val DEFAULT_COLORS = listOf(Color(COLOR_NAMES[0], 2L), Color(COLOR_NAMES[1], 0L), Color(COLOR_NAMES[2]))
val DEFAULT_BOARD = listOf(
    listOf(Arrow(null, Direction.RIGHT), Start, Arrow(null, Direction.DOWN)),
    listOf(Empty(), Empty(), Inc(1)),
    listOf(Arrow(0, Direction.RIGHT), End(), Inc(1)),
    listOf(Arrow(null, Direction.UP), Dec(0), Arrow(null, Direction.LEFT)),
)

fun <T : Any, S : T?> useStateSetRef(default: S): Triple<StateInstance<S>, StateSetter<S>, RefObject<T>> {
    val state = useState(default)
    val (value, setter) = state
    val ref = useRef<T>(null)
    ref.current = value
    return Triple(state, setter, ref)
}

val App = FC<Props> {
    val (colorState, setColors, colorsRef) = useStateSetRef(DEFAULT_COLORS)
    val (boardState, setBoard, boardRef) = useStateSetRef(DEFAULT_BOARD)
    val (blackState, setBlack, blackRef) = useStateSetRef(0L)
    val (foxState, setFox, foxRef) = useStateSetRef<Fox, Fox?>(null)
    val (inputState, setInput, inputRef) = useStateSetRef("")

    val state = ReactProgramState(colorsRef, setColors, foxRef, setFox, blackRef, setBlack, inputRef)

    var colors by colorState
    var board by boardState
    var black by blackState
    var fox by foxState
    var input by inputState

    var chosenTile by useState(Hack<() -> Tile> { Empty() })

    val (timerID, setTimerID) = useState<Int?>(null)
    fun resetTimer() = setTimerID { if (it != null) window.clearTimeout(it); null }

    var full by useState(false)

    ReactHTML.div {
        saveButton(colors, board, black, input)
        +" "
        loadButton(setColors, setBoard, setBlack, setInput)
        +" "
        ReactHTML.button {
            if (full) +"Zjednodušená verze" else +"Všechny featury"
            onClick = { full = !full }
        }
        +" "
        ReactHTML.a {
            +"Něco nefunguje? Dotaz? Připomínka?"
            href = "mailto:jonas.havelka@moznabude.cz"
        }
    }

    drawMenu {
        newRow = DiscardingFunction { board = board + listOf(List(board[0].size) { Empty() }) }
        newColumn = DiscardingFunction { board = board.map { row -> row + Empty() } }
        newColor = DiscardingFunction { colors = colors + Color(getDefaultColorName(colors.size)) }
        step = DiscardingFunction { state.go(board) }
        start = DiscardingFunction {
            for (row in board.indices)
                for (column in board[row].indices)
                    if (board[row][column] == Start)
                        fox = getFox(Position(row, column) + Direction.RIGHT)
            state.save()
        }
        reset = DiscardingFunction {
            fox = null
            state.load()
            resetTimer()
        }
        stop = DiscardingFunction {
            resetTimer()
        }
        autoRun = DiscardingFunction {
            setTimerID(window.setInterval({
                val next = state.go(boardRef.current!!)
                if (!next) resetTimer()
            }, 1))
        }

        foxOut = fox != null
        autoRunning = timerID != null
    }

    if (full) {
        +"Vstup:"
        ReactHTML.textarea {
//        css { width = 10.em; height = 5.em; resize = Resize.both }
//        type = InputType.text
            value = input
            onInput = {
                try {
                    input = it.target.asDynamic().value.toString()
                } catch (_: Exception) {
                }
            }
        }

        if (fox != null) {
            +"výstup:"
            ReactHTML.textarea {
                disabled = true
                value = fox!!.output
            }
        }
    }

    ReactHTML.br()

    drawBoard {
        this.board = board
        this.fox = fox
        this.chooseTile = { rowI, columnI ->
            val tile = chosenTile.hack()
            board = board.mapOne(rowI, columnI) { tile }
        }
        this.colors = colors
    }

    drawPicker {
        this.chooseTile = Hack { chosenTile = Hack(it) }
        setColor = Hack { colorIndex, value ->
            colors = colors.assign(colorIndex, value)
        }
        setColorName = Hack { colorIndex, name ->
            colors = colors.mapOne(colorIndex) { Color(name, it.value, it.lastValue) }
        }
        this.colors = colors
        this.full = full
        this.black = black
        this.setBlack = Hack { black = it }
    }
}
