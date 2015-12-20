package pl.mareklangiewicz.myutils

import android.os.Bundle
import android.view.View
import java.util.Locale

val V = true
val VV = false

/**
 * str extension functions are for humans only. returned string format can change any time..
 */

fun Double.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Float.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Long.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Int.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Short.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Byte.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Char.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Boolean.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun <E> Array<E>.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun <E> Iterable<E>.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Any?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()

private fun <E> Array<E>.toStr(max: Int) = joinToString(prefix = "[", postfix = "]", limit = max)
private fun <E> Iterable<E>.toStr(max: Int) = joinToString(prefix = "[", postfix = "]", limit = max)

fun <E> Array<E>.toShortStr() = toStr(10)
fun <E> Array<E>.toLongStr() = toStr(100)
fun <E> Array<E>.toVeryLongStr() = toStr(1000)

fun <E> Iterable<E>.toShortStr() = toStr(10)
fun <E> Iterable<E>.toLongStr() = toStr(100)
fun <E> Iterable<E>.toVeryLongStr() = toStr(1000)

fun Double.toShortStr() = toString() // exact string representation can change..
fun Double.toLongStr() = toString() // exact string representation can change..
fun Double.toVeryLongStr() = toString() // exact string representation can change..
fun Float.toShortStr() = toString() // exact string representation can change..
fun Float.toLongStr() = toString() // exact string representation can change..
fun Float.toVeryLongStr() = toString() // exact string representation can change..
fun Long.toShortStr() = toString() // exact string representation can change..
fun Long.toLongStr() = toString() // exact string representation can change..
fun Long.toVeryLongStr() = toString() // exact string representation can change..
fun Int.toShortStr() = toString() // exact string representation can change..
fun Int.toLongStr() = toString() // exact string representation can change..
fun Int.toVeryLongStr() = toString() // exact string representation can change..
fun Short.toShortStr() = toString() // exact string representation can change..
fun Short.toLongStr() = toString() // exact string representation can change..
fun Short.toVeryLongStr() = toString() // exact string representation can change..
fun Byte.toShortStr() = toString() // exact string representation can change..
fun Byte.toLongStr() = toString() // exact string representation can change..
fun Byte.toVeryLongStr() = toString() // exact string representation can change..
fun Char.toShortStr() = toString() // exact string representation can change..
fun Char.toLongStr() = toString() // exact string representation can change..
fun Char.toVeryLongStr() = toString() // exact string representation can change..
fun Boolean.toShortStr() = if(this) "t" else "f" // exact string representation can change..
fun Boolean.toLongStr() = if(this) "true" else "false" // exact string representation can change..
fun Boolean.toVeryLongStr() = toLongStr() // exact string representation can change..

fun Any?.toShortStr() = toString() // TODO SOMEDAY: print some short id or something..
fun Any?.toLongStr() = toString()
fun Any?.toVeryLongStr() = toString() // TODO SOMEDAY: print some more info..

fun Bundle?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()

fun Bundle?.toShortStr() = if(this == null) "null" else "Bundle{size:${size()}}"
fun Bundle?.toLongStr() = toShortStr()
fun Bundle?.toVeryLongStr() = toString()

fun View?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()

fun View?.toShortStr() = if(this == null) "null" else "View{hash:${hashCode()}}"
fun View?.toLongStr() = toShortStr()
fun View?.toVeryLongStr() = toString()

