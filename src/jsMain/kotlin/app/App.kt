package app

import file.load
import kotlinx.browser.window
import react.*
import react.dom.html.ReactHTML

import logic.*
import logic.Color
import logic.Direction
import file.loadButton
import file.saveButton
import mapOne
import web.cssom.ClassName
import web.http.fetchAsync


val DEFAULT_COLORS = listOf(Color(COLOR_NAMES[0], 2L), Color(COLOR_NAMES[1], 0L), Color(COLOR_NAMES[2]))
val DEFAULT_BOARD = listOf(
    listOf(Arrow(null, Direction.RIGHT), Start, Arrow(null, Direction.DOWN)),
    listOf(Empty(), Empty(), Inc(1)),
    listOf(Arrow(0, Direction.RIGHT), End(), Inc(1)),
    listOf(Arrow(null, Direction.UP), Dec(0), Arrow(null, Direction.LEFT)),
)

fun <T : Any, S : T?> useValueSetRef(default: S): Triple<S, StateSetter<S>, RefObject<T>> {
    val state = useState(default)
    val (value, setter) = state
    val ref = useRef<T>(null)
    ref.current = value
    return Triple(value, setter, ref)
}

val App = FC<Props> {
    val (colors, setColors, colorsRef) = useValueSetRef(DEFAULT_COLORS)
    val (board, setBoard, boardRef) = useValueSetRef(DEFAULT_BOARD)
    val (black, setBlack, blackRef) = useValueSetRef(0L)
    val (fox, setFox, foxRef) = useValueSetRef<Fox, Fox?>(null)
    val (input, setInput, inputRef) = useValueSetRef("")

    val state = ReactProgramState(colorsRef, setColors, foxRef, setFox, blackRef, setBlack, inputRef)

    val (_, setChosenTile, chosenTileRef) = useValueSetRef<Tile, Tile>(Empty())

    val (timerID, setTimerID) = useState<Int?>(null)
    fun resetTimer() = setTimerID { if (it != null) window.clearTimeout(it); null }

    var full by useState(false)

    useEffectOnce {
        window.onhashchange = {
            window.location.reload()
        }
        val hash = window.location.hash.drop(1)
        try {
            val promise = fetchAsync(hash)
            promise.then {
                try {
                    it.text().then { file ->
                        try {
                            val (newColors, newBoard, newBlack, newInput) = load(file)
                            setColors(newColors)
                            setBoard(newBoard)
                            setBlack(newBlack)
                            setInput(newInput)
                        } catch (e: Exception) {
                            println(e.message)
                        }
                    }
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    ReactHTML.div {
        className = ClassName("menu")
        saveButton(colors, board, black, input)
        loadButton(setColors, setBoard, setBlack, setInput)
        ReactHTML.button {
            if (full) +"Zjednodušená verze" else +"Všechny featury"
            onClick = { full = !full }
        }
        ReactHTML.a {
            +"Něco nefunguje? Dotaz? Připomínka?"
            href = "mailto:jonas.havelka@moznabude.cz"
        }
    }

    menu(
        newRow = { setBoard(Board::withNewRow) },
        newColumn = { setBoard(Board::withNewColumn) },
        newColor = { setColors(colors + Color(getDefaultColorName(colors.size))) },
        start = {
            board.findStart()?.also { setFox(getFox(it + Direction.RIGHT)) }
            state.save()
        },
        step = { state.go(board) },
        reset = {
            setFox(null)
            state.load()
            resetTimer()
        },
        stop = { resetTimer() },
        autoRun = {
            setTimerID(window.setInterval({
                val next = state.go(boardRef.current!!)
                if (!next) resetTimer()
            }, 1))
        },

        foxOut = fox != null, autoRunning = timerID != null,
    )

    if (full) {
        ReactHTML.label { +"Vstup:" }
        ReactHTML.textarea {
//        css { width = 10.em; height = 5.em; resize = Resize.both }
//        type = InputType.text
            value = input
            onInput = { setInput(it.target.asDynamic().value.toString()) }
        }

        if (fox != null) {
            ReactHTML.label { +"Výstup:" }
            ReactHTML.textarea {
                disabled = true
                value = fox.output
            }
        }
    }

    ReactHTML.br()

    boardTable(colors, board, fox) { rowI, columnI ->
        val tile = chosenTileRef.current!!
        if (tile == Start) {
            setBoard(board.mapIndexed { rowIndex, row ->
                row.mapIndexed { columnIndex, it ->
                    if (rowIndex == rowI && columnIndex == columnI) Start
                    else if (it != Start) it
                    else Empty()
                }
            })
        } else setBoard(board.mapOne(rowI, columnI) { tile })
    }

    picker(
        setChosenTile,
        { colorIndex, value -> setColors(colors.assign(colorIndex, value)) },
        { colorIndex, name -> setColors(colors.mapOne(colorIndex) { Color(name, it.value, it.lastValue) }) },
        colors, black, setBlack,
        full,
    )
}
