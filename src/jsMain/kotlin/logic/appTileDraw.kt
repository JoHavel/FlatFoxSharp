package logic

import react.ChildrenBuilder
import react.FC
import react.Props

private external interface TileProps : Props {
    var tile: Tile
    var colors: Colors
}

private val tileDraw_ = FC<TileProps> { props -> props.tile.run { render(props.colors) } }

fun ChildrenBuilder.tileDraw(tile: Tile, colors: Colors) = tileDraw_ {
    this.tile = tile
    this.colors = colors
}
