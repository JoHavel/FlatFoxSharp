package logic

import emotion.react.css
import react.ChildrenBuilder
import react.FC
import react.Props
import react.dom.html.ReactHTML

interface Tile {
    fun ProgramState.go()
    fun go(state: ProgramState) = state.go()
    fun ChildrenBuilder.render(colors: Colors)
}

external interface TileProps : Props {
    var tile: Tile
    var colors: Colors
}

val drawTile = FC<TileProps> { props -> props.tile.run { render(props.colors) } }

open class Empty : Tile {
    override fun ProgramState.go() {
        fox = fox?.go()
    }

    override fun ChildrenBuilder.render(colors: Colors) {}
}

object Start : Empty() {
    override fun ChildrenBuilder.render(colors: Colors) = +"@"
}

class End : Tile {
    override fun ProgramState.go() {}

    override fun ChildrenBuilder.render(colors: Colors) = +"#"
}

abstract class ColoredTile(protected var colorIndex: Int?) : Tile {
    override fun ChildrenBuilder.render(colors: Colors) {
        ReactHTML.div {
            if (colorIndex != null)
                css { color = web.cssom.Color(colors[colorIndex!!].color) }
            coloredRender()
        }
    }

    abstract fun ChildrenBuilder.coloredRender()
}

class Arrow(colorIndex: Int?, private val direction: Direction) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        fox = if (colorIndex == null || colors[colorIndex!!].value == 0L)
            fox?.go(direction)
        else
            fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +direction.string
    }
}

class Inc(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.inc(colorIndex!!)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"+"
    }
}

class Dec(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.dec(colorIndex!!)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"-"
    }
}

class Constant(colorIndex: Int, private val value: Long) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.assign(colorIndex!!, value)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +value.toString()
    }
}

class Store(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        black = colors[colorIndex!!].value
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"○"
    }
}

class Plus(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.plus(colorIndex!!, black)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"⊕"
    }
}

class Minus(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.minus(colorIndex!!, black)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"⊖"
    }
}

class Times(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.times(colorIndex!!, black)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"⊙"
    }
}

class Div(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.div(colorIndex!!, black)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"⊘"
    }
}

class Rem(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.rem(colorIndex!!, black)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"%"
    }
}

class Input(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        fox?.also {
            val (nextFox, char) = it.go(input)
            fox = nextFox
            colors = colors.assign(colorIndex!!, char.code.toLong())
        }
    }

    override fun ChildrenBuilder.coloredRender() {
        +"i"
    }
}

class Output(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        fox = fox?.go(colors[colorIndex!!].value.toInt().toChar())
    }

    override fun ChildrenBuilder.coloredRender() {
        +"o"
    }
}

class Jump(private val position: Position) : Tile {
    override fun ProgramState.go() {
        fox = fox?.go(position)
    }

    override fun ChildrenBuilder.render(colors: Colors) {
        +"${position.row + 1}:${position.column + 1}"
    }
}
