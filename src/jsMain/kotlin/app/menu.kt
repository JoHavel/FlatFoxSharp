package app

import react.FC
import react.Props
import react.dom.html.ReactHTML
import web.cssom.ClassName


external interface MenuProps : Props {
    var newRow: DiscardingFunction
    var newColumn: DiscardingFunction
    var newColor: DiscardingFunction
    var start: DiscardingFunction
    var step: DiscardingFunction
    var reset: DiscardingFunction
    var stop: DiscardingFunction
    var autoRun: DiscardingFunction

    var foxOut: Boolean
    var autoRunning: Boolean
}

val drawMenu = FC<MenuProps>("Menu") { props ->
    ReactHTML.div {
        className = ClassName("menu")
        ReactHTML.button {
            +"Přidej řádek"
            onClick = props.newRow.hack
        }

        ReactHTML.button {
            +"Přidej sloupec"
            onClick = props.newColumn.hack
        }

        ReactHTML.button {
            +"Přidej barvu"
            onClick = props.newColor.hack
        }

        ReactHTML.button {
            if (props.foxOut) {
                +"Krok ▶️"
                onClick = props.step.hack
            } else {
                +"Vzbuď lišku (start) ▶️"
                onClick = props.start.hack
            }
        }

        if (props.foxOut) {
            ReactHTML.button {
                +"Pošli lišku do pelechu (reset) 🟥"
                onClick = props.reset.hack
            }

            ReactHTML.button {
                if (props.autoRunning) {
                    +"Zastav ⏸️"
                    onClick = props.stop.hack
                } else {
                    +"Běž (auto) ⏩"
                    onClick = props.autoRun.hack
                }
            }
        }
    }
}