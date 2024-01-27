package logic

import emotion.react.css
import react.ChildrenBuilder
import react.dom.html.ReactHTML

interface Tile {
    fun ProgramState.go()
    fun go(state: ProgramState) = state.go()
    fun ChildrenBuilder.render(colors: Colors)
    override fun toString(): String
}

open class Empty : Tile {
    override fun ProgramState.go() {
        fox = fox?.go()
    }

    override fun ChildrenBuilder.render(colors: Colors) {}
    override fun toString(): String = ""
}

object Start : Empty() {
    override fun ChildrenBuilder.render(colors: Colors) = +"@"
    override fun toString(): String = "@"
}

class End : Tile {
    override fun ProgramState.go() {}

    override fun ChildrenBuilder.render(colors: Colors) = +"#"
    override fun toString(): String = "#"
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

    override fun toString(): String = direction.string + (colorIndex?.toString() ?: "")
}

class Inc(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.inc(colorIndex!!)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"+"
    }

    override fun toString(): String = "+${colorIndex}"
}

class Dec(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.dec(colorIndex!!)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"-"
    }

    override fun toString(): String = "-${colorIndex}"
}

class Constant(colorIndex: Int, private val value: Long) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.assign(colorIndex!!, value)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +value.toString()
    }

    override fun toString(): String = "${colorIndex}=${value}"
}

class Store(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        black = colors[colorIndex!!].value
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"○"
    }

    override fun toString(): String = "O${colorIndex}"
}

class Plus(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.plus(colorIndex!!, black)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"⊕"
    }

    override fun toString(): String = "+O${colorIndex}"
}

class Minus(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.minus(colorIndex!!, black)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"⊖"
    }

    override fun toString(): String = "-O${colorIndex}"
}

class Times(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.times(colorIndex!!, black)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"⊙"
    }

    override fun toString(): String = ".O${colorIndex}"
}

class Div(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.div(colorIndex!!, black)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"⊘"
    }

    override fun toString(): String = "/O${colorIndex}"
}

class Rem(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        colors = colors.rem(colorIndex!!, black)
        fox = fox?.go()
    }

    override fun ChildrenBuilder.coloredRender() {
        +"%"
    }

    override fun toString(): String = "%O${colorIndex}"
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

    override fun toString(): String = "i${colorIndex}"
}

class Output(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun ProgramState.go() {
        fox = fox?.go(colors[colorIndex!!].value.toInt().toChar())
    }

    override fun ChildrenBuilder.coloredRender() {
        +"o"
    }

    override fun toString(): String = "o${colorIndex}"
}

class Jump(private val position: Position) : Tile {
    override fun ProgramState.go() {
        fox = fox?.go(position)
    }

    override fun ChildrenBuilder.render(colors: Colors) {
        +"${position.row + 1}:${position.column + 1}"
    }

    override fun toString(): String = "${position.row + 1}:${position.column + 1}"
}
