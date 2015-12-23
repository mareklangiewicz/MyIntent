package pl.mareklangiewicz.myutils

import android.os.Bundle
import android.view.View
import java.util.Locale

val V = true
val VV = false

/**
 * str extension functions are for humans only. returned string format can change any time..
 */

fun Double?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Float?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Long?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Int?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Short?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Byte?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Char?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Boolean?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun <E> Array<E>?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun <E> Iterable<E>?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()
fun Any?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()

private fun <E> Array<E>?.toStr(max: Int) = this?.joinToString(prefix = "[", postfix = "]", limit = max) ?: "null"
private fun <E> Iterable<E>?.toStr(max: Int) = this?.joinToString(prefix = "[", postfix = "]", limit = max) ?: "null"

fun <E> Array<E>?.toShortStr() = toStr(10)
fun <E> Array<E>?.toLongStr() = toStr(100)
fun <E> Array<E>?.toVeryLongStr() = toStr(1000)

fun <E> Iterable<E>?.toShortStr() = toStr(10)
fun <E> Iterable<E>?.toLongStr() = toStr(100)
fun <E> Iterable<E>?.toVeryLongStr() = toStr(1000)

fun Double?.toShortStr() = this?.toString() ?: "null" // exact string representation can change..
fun Double?.toLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Double?.toVeryLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Float?.toShortStr() = this?.toString() ?: "null" // exact string representation can change..
fun Float?.toLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Float?.toVeryLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Long?.toShortStr() = this?.toString() ?: "null" // exact string representation can change..
fun Long?.toLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Long?.toVeryLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Int?.toShortStr() = this?.toString() ?: "null" // exact string representation can change..
fun Int?.toLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Int?.toVeryLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Short?.toShortStr() = this?.toString() ?: "null" // exact string representation can change..
fun Short?.toLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Short?.toVeryLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Byte?.toShortStr() = this?.toString() ?: "null" // exact string representation can change..
fun Byte?.toLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Byte?.toVeryLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Char?.toShortStr() = this?.toString() ?: "null" // exact string representation can change..
fun Char?.toLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Char?.toVeryLongStr() = this?.toString() ?: "null" // exact string representation can change..
fun Boolean?.toShortStr() = if(this == null) "null" else if(this) "t" else "f" // exact string representation can change..
fun Boolean?.toLongStr() = if(this == null) "null" else if(this) "true" else "false" // exact string representation can change..
fun Boolean?.toVeryLongStr() = toLongStr() // exact string representation can change..

fun Any?.toShortStr() = this?.toString() ?: "null" // TODO SOMEDAY: print some short id or something..
fun Any?.toLongStr() = this?.toString() ?: "null"
fun Any?.toVeryLongStr() = this?.toString() ?: "null" // TODO SOMEDAY: print some more info..

fun Bundle?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()

fun Bundle?.toShortStr() = if(this == null) "null" else "Bundle{size:${size()}}"
fun Bundle?.toLongStr() = toShortStr()
fun Bundle?.toVeryLongStr() = this?.toString() ?: "null"

fun View?.str() = if(VV) toVeryLongStr() else if(V) toLongStr() else toShortStr()

fun View?.toShortStr() = if(this == null) "null" else "View{hash:${hashCode()}}"
fun View?.toLongStr() = toShortStr()
fun View?.toVeryLongStr() = this?.toString() ?: "null"

