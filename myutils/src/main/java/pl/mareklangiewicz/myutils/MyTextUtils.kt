package pl.mareklangiewicz.myutils

import android.os.Bundle
import android.view.View

val V = true
val VV = false

/**
 * str extension functions are for humans only. returned string format can change any time..
 */

fun Double?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()
fun Float?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()
fun Long?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()
fun Int?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()
fun Short?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()
fun Byte?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()
fun Char?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()
fun Boolean?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()
fun <E> Array<E>?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()
fun <E> Iterable<E>?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()
fun Any?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()

private fun <E> Array<E>?.toStr(max: Int) = this?.joinToString(prefix = "[", postfix = "]", limit = max) ?: "null"
private fun <E> Iterable<E>?.toStr(max: Int) = this?.joinToString(prefix = "[", postfix = "]", limit = max) ?: "null"

fun <E> Array<E>?.toShortStr() = toStr(10)
fun <E> Array<E>?.toLongStr() = toStr(100)
fun <E> Array<E>?.toVeryLongStr() = toStr(1000)

fun <E> Iterable<E>?.toShortStr() = toStr(10)
fun <E> Iterable<E>?.toLongStr() = toStr(100)
fun <E> Iterable<E>?.toVeryLongStr() = toStr(1000)

fun Double?.toShortStr() = toString() // exact string representation can change..
fun Double?.toLongStr() = toString() // exact string representation can change..
fun Double?.toVeryLongStr() = toString() // exact string representation can change..
fun Float?.toShortStr() = toString() // exact string representation can change..
fun Float?.toLongStr() = toString() // exact string representation can change..
fun Float?.toVeryLongStr() = toString() // exact string representation can change..
fun Long?.toShortStr() = toString() // exact string representation can change..
fun Long?.toLongStr() = toString() // exact string representation can change..
fun Long?.toVeryLongStr() = toString() // exact string representation can change..
fun Int?.toShortStr() = toString() // exact string representation can change..
fun Int?.toLongStr() = toString() // exact string representation can change..
fun Int?.toVeryLongStr() = toString() // exact string representation can change..
fun Short?.toShortStr() = toString() // exact string representation can change..
fun Short?.toLongStr() = toString() // exact string representation can change..
fun Short?.toVeryLongStr() = toString() // exact string representation can change..
fun Byte?.toShortStr() = toString() // exact string representation can change..
fun Byte?.toLongStr() = toString() // exact string representation can change..
fun Byte?.toVeryLongStr() = toString() // exact string representation can change..
fun Char?.toShortStr() = toString() // exact string representation can change..
fun Char?.toLongStr() = toString() // exact string representation can change..
fun Char?.toVeryLongStr() = toString() // exact string representation can change..
fun Boolean?.toShortStr() = this?.let { it % "t" ?: "f" } ?: "null" // exact string representation can change..
fun Boolean?.toLongStr() = this?.let { it % "true" ?: "false" } ?: "null" // exact string representation can change..
fun Boolean?.toVeryLongStr() = toLongStr() // exact string representation can change..

fun Any?.toShortStr() = toString() // TODO SOMEDAY: print some short id or something..
fun Any?.toLongStr() = toString()
fun Any?.toVeryLongStr() = toString() // TODO SOMEDAY: print some more info..

fun Bundle?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()

fun Bundle?.toShortStr() = this?.let { "Bundle{size:${size()}}" } ?: "null"
fun Bundle?.toLongStr() = toShortStr()
fun Bundle?.toVeryLongStr() = toString()

fun View?.str() = VV % toVeryLongStr() ?: V % toLongStr() ?: toShortStr()

fun View?.toShortStr() = this?.let { "View{hash:${hashCode()}}" } ?: "null"
fun View?.toLongStr() = toShortStr()
fun View?.toVeryLongStr() = toString()

