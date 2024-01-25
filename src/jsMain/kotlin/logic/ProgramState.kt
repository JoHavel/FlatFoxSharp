package logic

import react.RefObject
import react.StateSetter
import kotlin.reflect.KProperty

interface ProgramState {
    var colors: Colors
    var fox: Fox?
    var black: Long
    val input: String
}

class RefSetter<T : Any>(private val ref: RefObject<T>, private val setter: StateSetter<T>) {
    operator fun getValue(reactProgramState: ReactProgramState, kProperty: KProperty<*>) = ref.current!!
    operator fun setValue(reactProgramState: ReactProgramState, kProperty: KProperty<*>, value: T) = setter(value)
}

class NullableRefSetter<T : Any>(private val ref: RefObject<T>, private val setter: StateSetter<T?>) {
    operator fun getValue(reactProgramState: ReactProgramState, kProperty: KProperty<*>) = ref.current
    operator fun setValue(reactProgramState: ReactProgramState, kProperty: KProperty<*>, value: T?) = setter(value)
}

class ReactProgramState(
    colorsRef: RefObject<Colors>,
    setColors: StateSetter<Colors>,
    foxRef: RefObject<Fox>,
    setFox: StateSetter<Fox?>,
    blackRef: RefObject<Long>,
    setBlack: StateSetter<Long>,
    private val inputRef: RefObject<String>,
) : ProgramState {
    override var colors: Colors by RefSetter(colorsRef, setColors)
    override var fox: Fox? by NullableRefSetter(foxRef, setFox)
    override var black: Long by RefSetter(blackRef, setBlack)
    override val input: String
        get() = inputRef.current!!

    private var lastBlack: Long = black

    fun save() {
        lastBlack = black
        colors.save()
    }

    fun load() {
        black = lastBlack
        colors = colors.withLoaded()
    }
}
