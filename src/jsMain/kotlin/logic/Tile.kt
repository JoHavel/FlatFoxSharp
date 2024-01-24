package logic

import emotion.react.css
import react.ChildrenBuilder
import react.FC
import react.Props
import react.dom.html.ReactHTML

interface Tile {
    fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox>
    fun ChildrenBuilder.render(colors: Colors)
}

external interface TileProps : Props {
    var tile: Tile
    var colors: Colors
}

val drawTile = FC<TileProps> { props -> props.tile.run { render(props.colors) } }

open class Empty : Tile {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        black to colors to fox.go()

    override fun ChildrenBuilder.render(colors: Colors) {}
}

object Start : Empty() {
    override fun ChildrenBuilder.render(colors: Colors) = +"@"
}

class End : Tile {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        black to colors to fox

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
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> {
        return black to colors to (
                if ((colorIndex?.let { colors[colorIndex!!].value } ?: 0) == 0) fox.go(direction) else fox.go()
                )
    }

    override fun ChildrenBuilder.coloredRender() {
        +direction.string
    }
}

class Inc(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        black to colors.inc(colorIndex!!) to fox.go()

    override fun ChildrenBuilder.coloredRender() {
        +"+"
    }
}

class Dec(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        black to colors.dec(colorIndex!!) to fox.go()

    override fun ChildrenBuilder.coloredRender() {
        +"-"
    }
}

class Constant(colorIndex: Int, private val value: Int) : ColoredTile(colorIndex) {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        black to colors.assign(colorIndex!!, value) to fox.go()

    override fun ChildrenBuilder.coloredRender() {
        +value.toString()
    }
}

class Store(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        colors[colorIndex!!].value to colors to fox.go()

    override fun ChildrenBuilder.coloredRender() {
        +"○"
    }
}

class Plus(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        black to colors.plus(colorIndex!!, black) to fox.go()

    override fun ChildrenBuilder.coloredRender() {
        +"⊕"
    }
}

class Minus(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        black to colors.minus(colorIndex!!, black) to fox.go()

    override fun ChildrenBuilder.coloredRender() {
        +"⊖"
    }
}

class Times(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        black to colors.times(colorIndex!!, black) to fox.go()

    override fun ChildrenBuilder.coloredRender() {
        +"⊙"
    }
}

class Div(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        black to colors.div(colorIndex!!, black) to fox.go()

    override fun ChildrenBuilder.coloredRender() {
        +"⊘"
    }
}

class Rem(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        black to colors.rem(colorIndex!!, black) to fox.go()

    override fun ChildrenBuilder.coloredRender() {
        +"%"
    }
}

class Input(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> {
        val (nextFox, char) = fox.go(input)
        return black to colors.assign(colorIndex!!, char.code) to nextFox
    }

    override fun ChildrenBuilder.coloredRender() {
        +"i"
    }
}

class Output(colorIndex: Int) : ColoredTile(colorIndex) {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        black to colors to fox.go(colors[colorIndex!!].value.toChar())

    override fun ChildrenBuilder.coloredRender() {
        +"o"
    }
}

class Jump(private val position: Position) : Tile {
    override fun go(black: Int, colors: Colors, fox: Fox, input: String): Pair<Pair<Int, Colors>, Fox> =
        black to colors to fox.go(position)

    override fun ChildrenBuilder.render(colors: Colors) {
        +"${position.row + 1}:${position.column + 1}"
    }
}
