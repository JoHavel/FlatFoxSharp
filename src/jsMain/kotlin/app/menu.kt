package app

import react.ChildrenBuilder
import react.FC
import react.Props
import react.dom.html.ReactHTML
import web.cssom.ClassName


private external interface MenuProps : Props {
    var newRow: (Any?) -> Unit
    var newColumn: (Any?) -> Unit
    var newColor: (Any?) -> Unit
    var start: (Any?) -> Unit
    var step: (Any?) -> Unit
    var reset: (Any?) -> Unit
    var stop: (Any?) -> Unit
    var autoRun: (Any?) -> Unit

    var foxOut: Boolean
    var autoRunning: Boolean
}

private val menu_ = FC<MenuProps>("Menu") { props ->
    ReactHTML.div {
        className = ClassName("menu")
        ReactHTML.button {
            +"Přidej řádek"
            onClick = props.newRow
        }

        ReactHTML.button {
            +"Přidej sloupec"
            onClick = props.newColumn
        }

        ReactHTML.button {
            +"Přidej barvu"
            onClick = props.newColor
        }

        ReactHTML.button {
            if (props.foxOut) {
                +"Krok ▶️"
                onClick = props.step
            } else {
                +"Vzbuď lišku (start) ▶️"
                onClick = props.start
            }
        }

        if (props.foxOut) {
            ReactHTML.button {
                +"Pošli lišku do pelechu (reset) 🟥"
                onClick = props.reset
            }

            ReactHTML.button {
                if (props.autoRunning) {
                    +"Zastav ⏸️"
                    onClick = props.stop
                } else {
                    +"Běž (auto) ⏩"
                    onClick = props.autoRun
                }
            }
        }
    }
}

fun ChildrenBuilder.menu(
    newRow: (Any?) -> Unit,
    newColumn: (Any?) -> Unit,
    newColor: (Any?) -> Unit,
    start: (Any?) -> Unit,
    step: (Any?) -> Unit,
    reset: (Any?) -> Unit,
    stop: (Any?) -> Unit,
    autoRun: (Any?) -> Unit,

    foxOut: Boolean,
    autoRunning: Boolean,
) = menu_ {
    this.newRow = newRow
    this.newColumn = newColumn
    this.newColor = newColor
    this.start = start
    this.step = step
    this.reset = reset
    this.stop = stop
    this.autoRun = autoRun

    this.foxOut = foxOut
    this.autoRunning = autoRunning
}
