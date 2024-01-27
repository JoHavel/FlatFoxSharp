package logic

import app.mapOne

typealias Colors = List<Color>

// https://www.w3schools.com/colors/colors_names.asp
@Suppress("IncorrectFormatting")
val COLOR_NAMES = listOf(
    "red", "green", "blue",
    "purple", "orange", "violet", "brown", "aqua",
) + listOf(
    "aliceblue", "antiquewhite", "aquamarine", "azure", "beige", "bisque", "black", "blanchedalmond", "blueviolet", "burlywood", "cadetblue", "chartreuse", "chocolate", "coral", "cornflowerblue", "cornsilk", "crimson", "cyan", "darkblue", "darkcyan", "darkgoldenrod", "darkgray", "darkgrey", "darkgreen", "darkkhaki", "darkmagenta", "darkolivegreen", "darkorange", "darkorchid", "darkred", "darksalmon", "darkseagreen", "darkslateblue", "darkslategray", "darkslategrey", "darkturquoise", "darkviolet", "deeppink", "deepskyblue", "dimgray", "dimgrey", "dodgerblue", "firebrick", "floralwhite", "forestgreen", "fuchsia", "gainsboro", "ghostwhite", "gold", "goldenrod", "gray", "grey", "greenyellow", "honeydew", "hotpink", "indianred", "indigo", "ivory", "khaki", "lavender", "lavenderblush", "lawngreen", "lemonchiffon", "lightblue", "lightcoral", "lightcyan", "lightgoldenrodyellow", "lightgray", "lightgrey", "lightgreen", "lightpink", "lightsalmon", "lightseagreen", "lightskyblue", "lightslategray", "lightslategrey", "lightsteelblue", "lightyellow", "lime", "limegreen", "linen", "magenta", "maroon", "mediumaquamarine", "mediumblue", "mediumorchid", "mediumpurple", "mediumseagreen", "mediumslateblue", "mediumspringgreen", "mediumturquoise", "mediumvioletred", "midnightblue", "mintcream", "mistyrose", "moccasin", "navajowhite", "navy", "oldlace", "olive", "olivedrab", "orangered", "orchid", "palegoldenrod", "palegreen", "paleturquoise", "palevioletred", "papayawhip", "peachpuff", "peru", "pink", "plum", "powderblue", "rebeccapurple", "rosybrown", "royalblue", "saddlebrown", "salmon", "sandybrown", "seagreen", "seashell", "sienna", "silver", "skyblue", "slateblue", "slategray", "slategrey", "snow", "springgreen", "steelblue", "tan", "teal", "thistle", "tomato", "turquoise", "violet", "wheat", "white", "whitesmoke", "yellow", "yellowgreen",
).asSequence().shuffled()

fun getDefaultColorName(i: Int) =
    if (i >= 0 && i < COLOR_NAMES.size) COLOR_NAMES[i] else {
        val number = (0 until 256 * 256 * 256).random().toString(16)
        val prefix = (0 until 6 - number.length).map { '0' }.joinToString("")
        "#$prefix$number"
    }


data class Color(val color: String, val value: Long = 0L, var lastValue: Long = 0L) {
    fun save() {
        lastValue = value
    }

    fun load(): Color = Color(color, lastValue, lastValue)
    operator fun plus(i: Long) = Color(color, value + i, lastValue)
    operator fun minus(i: Long) = Color(color, value - i, lastValue)
    operator fun times(i: Long) = Color(color, value * i, lastValue)
    operator fun div(i: Long) = Color(color, if (i == 0L) Long.MAX_VALUE else (value / i), lastValue)
    operator fun rem(i: Long) = Color(color, if (i == 0L) Long.MIN_VALUE else (value % i), lastValue)
    fun assign(i: Long) = Color(color, i, lastValue)
}

fun Colors.save() = forEach { it.save() }
fun Colors.withLoaded(): Colors = map { it.load() }
fun Colors.inc(index: Int): Colors = mapOne(index) { it + 1L }
fun Colors.dec(index: Int): Colors = mapOne(index) { it - 1L }
fun Colors.plus(index: Int, value: Long): Colors = mapOne(index) { it + value }
fun Colors.minus(index: Int, value: Long): Colors = mapOne(index) { it - value }
fun Colors.times(index: Int, value: Long): Colors = mapOne(index) { it * value }
fun Colors.div(index: Int, value: Long): Colors = mapOne(index) { it / value }
fun Colors.rem(index: Int, value: Long): Colors = mapOne(index) { it % value }
fun Colors.assign(index: Int, value: Long): Colors = mapOne(index) { it.assign(value) }