package app

import kotlinx.browser.window
import logic.*
import react.*
import react.dom.html.ReactHTML
import web.html.InputType

val App = FC<Props> {
    var colors: Colors by useState(
        listOf(
            Color("red", 2L),
            Color("green"),
            Color("blue"),
            Color("purple"),
            Color("orange"),
            Color("violet"),
            Color("brown"),
            Color("aqua"),
        )
    )
    val colorsRef = useRef(colors)
    colorsRef.current = colors
    var board by useState(
        listOf(
            listOf(Arrow(null, Direction.RIGHT), Start, Arrow(null, Direction.DOWN)),
            listOf(Empty(), Empty(), Inc(1)),
            listOf(Arrow(0, Direction.RIGHT), End(), Inc(1)),
            listOf(Arrow(null, Direction.UP), Dec(0), Arrow(null, Direction.LEFT)),
        )
    )
    val boardRef = useRef(board)
    boardRef.current = board
    var chosenTile by useState(Hack<() -> Tile> { Empty() })
    var black by useState(0L)
    val blackRef = useRef(black)
    blackRef.current = black
    val (timerID, setTimerID) = useState<Int?>(null)
    fun resetTimer() = setTimerID { if (it != null) window.clearTimeout(it); null }
    var fox by useState<Fox?>(null)
    val foxRef = useRef(fox)
    foxRef.current = fox
    var input by useState("")
    val inputRef = useRef(input)
    inputRef.current = input

    drawMenu {
        newRow = DiscardingFunction { board = board + listOf(List(board[0].size) { Empty() }) }
        newColumn = DiscardingFunction { board = board.map { row -> row + Empty() } }
        step = DiscardingFunction {
            val (nextValues, nextFox) = go(black, colors, fox, board, input)
            val (nextBlack, nextColors) = nextValues
            colors = nextColors
            black = nextBlack
            if (nextFox != null) fox = nextFox
        }
        start = DiscardingFunction {
            for (row in board.indices)
                for (column in board[row].indices)
                    if (board[row][column] == Start)
                        fox = getFox(Position(row, column) + Direction.RIGHT)
            colors = colors.withSaved()
        }
        reset = DiscardingFunction {
            fox = null
            colors = colors.withLoaded()
            resetTimer()
        }
        stop = DiscardingFunction {
            resetTimer()
        }
        autoRun = DiscardingFunction {
            setTimerID(window.setInterval({
                val (nextValues, nextFox) = go(
                    blackRef.current!!,
                    colorsRef.current!!,
                    foxRef.current,
                    boardRef.current!!,
                    inputRef.current!!
                )
                val (nextBlack, nextColors) = nextValues
                black = nextBlack
                colors = nextColors
                if (nextFox != null) fox = nextFox
                else resetTimer()
            }, 1))
        }

        foxOut = fox != null
        autoRunning = timerID != null
    }

    +"Černý registr (akumulátor)"
    ReactHTML.input {
        type = InputType.number
        value = black.toString()
        onInput = {
            try {
                black = it.target.asDynamic().value.toString().toLong()
            } catch (_: Exception) {
            }
        }
    }

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
        this.colors = colors
    }
}
