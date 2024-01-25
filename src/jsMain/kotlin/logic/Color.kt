package logic

import app.mapOne

typealias Colors = List<Color>

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