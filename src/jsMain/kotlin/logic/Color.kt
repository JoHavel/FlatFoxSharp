package logic

import app.mapOne

typealias Colors = List<Color>

data class Color(val color: String, val value: Int = 0, val lastValue: Int = 0) {
    fun save(): Color = Color(color, value, value)
    fun load(): Color = Color(color, lastValue, lastValue)
    operator fun plus(i: Int) = Color(color, value + i, lastValue)
    operator fun minus(i: Int) = Color(color, value - i, lastValue)
    operator fun times(i: Int) = Color(color, value * i, lastValue)
    operator fun div(i: Int) = Color(color, if (i == 0) Int.MAX_VALUE else (value / i), lastValue)
    operator fun rem(i: Int) = Color(color, if (i == 0) Int.MIN_VALUE else (value % i), lastValue)
    fun assign(i: Int) = Color(color, i, lastValue)
}

fun Colors.withSaved(): Colors = map { it.save() }
fun Colors.withLoaded(): Colors = map { it.load() }
fun Colors.inc(index: Int): Colors = mapOne(index) { it + 1 }
fun Colors.dec(index: Int): Colors = mapOne(index) { it - 1 }
fun Colors.plus(index: Int, value: Int): Colors = mapOne(index) { it + value }
fun Colors.minus(index: Int, value: Int): Colors = mapOne(index) { it - value }
fun Colors.times(index: Int, value: Int): Colors = mapOne(index) { it * value }
fun Colors.div(index: Int, value: Int): Colors = mapOne(index) { it / value }
fun Colors.rem(index: Int, value: Int): Colors = mapOne(index) { it % value }
fun Colors.assign(index: Int, value: Int): Colors = mapOne(index) { it.assign(value) }