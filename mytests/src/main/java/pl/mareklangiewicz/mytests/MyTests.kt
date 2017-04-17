package pl.mareklangiewicz.mytests

import com.google.common.collect.Range
import com.google.common.truth.*
import com.google.common.truth.Truth.assert_
import java.math.BigDecimal

val Assert: TestVerb by lazy { assert_() }

// TODO NOW: investigate project Kluent (https://github.com/MarkusAmshove/Kluent)
// (but be careful about polluting too broad namespaces..)

// TODO LATER: not sure about all those generic types below - analyze/test it all later..

infix fun <T> TestVerb.That(target: Comparable<T>?): ComparableSubject<*, Comparable<T>> = this.that(target)
infix fun TestVerb.That(target: BigDecimal?): BigDecimalSubject = this.that(target)
infix fun TestVerb.That(target: Any?): Subject<DefaultSubject, Any> = this.that(target)
infix fun <T> TestVerb.That(target: Class<T>?): ClassSubject = this.that(target)
infix fun TestVerb.That(target: Throwable?): ThrowableSubject = this.that(target)
infix fun TestVerb.That(target: Long?): LongSubject = this.that(target)
infix fun TestVerb.That(target: Double?): DoubleSubject = this.that(target)
infix fun TestVerb.That(target: Float?): FloatSubject = this.that(target)
infix fun TestVerb.That(target: Int?): IntegerSubject = this.that(target)
infix fun TestVerb.That(target: Boolean?): BooleanSubject = this.that(target)
// TODO SOMEDAY: rest

interface ITestObject
interface IBooleanTestObject : ITestObject

object Null : ITestObject
object NotNull : ITestObject
object True : IBooleanTestObject
object False : IBooleanTestObject

infix fun <S: Subject<S, T>, T> Subject<S, T>.Is(obj: ITestObject) = when(obj) {
    Null -> isNull()
    NotNull -> isNotNull()
    else -> throw ClassCastException()
}

infix fun BooleanSubject.Is(obj: IBooleanTestObject) = when(obj) {
    True -> isTrue()
    False -> isFalse()
    else -> throw ClassCastException()
}

infix fun <S: Subject<out S, out T>, T> Subject<out S, out T>.IsEqualTo(other: Any?) = isEqualTo(other)
infix fun <S: Subject<out S, out T>, T> Subject<out S, out T>.IsNotEqualTo(other: Any?) = isNotEqualTo(other)
infix fun <S: Subject<out S, out T>, T> Subject<out S, out T>.IsSameAs(other: Any?) = isSameAs(other)
infix fun <S: Subject<out S, out T>, T> Subject<out S, out T>.IsNotSameAs(other: Any?) = isNotSameAs(other)
infix fun <S: Subject<out S, out T>, T> Subject<out S, out T>.IsInstanceOf(clazz: Class<*>) = isInstanceOf(clazz)
infix fun <S: Subject<out S, out T>, T> Subject<out S, out T>.IsNotInstanceOf(clazz: Class<*>) = isNotInstanceOf(clazz)
infix fun <S: Subject<out S, out T>, T> Subject<out S, out T>.IsIn(iterable: Iterable<*>) = isIn(iterable)
infix fun <S: Subject<out S, out T>, T> Subject<out S, out T>.IsNotIn(iterable: Iterable<*>) = isNotIn(iterable)
// TODO SOMEDAY: rest from truth.Subject

infix fun <S: ComparableSubject<S, T>, T: Comparable<T>> ComparableSubject<S, T>.IsIn(range: ClosedRange<T>) = isIn(Range.closed(range.start, range.endInclusive))
infix fun <S: ComparableSubject<S, T>, T: Comparable<T>> ComparableSubject<S, T>.IsNotIn(range: ClosedRange<T>) = isNotIn(Range.closed(range.start, range.endInclusive))
infix fun <S: ComparableSubject<S, T>, T: Comparable<T>> ComparableSubject<S, T>.IsGreaterThan(other: T) = isGreaterThan(other)
infix fun <S: ComparableSubject<S, T>, T: Comparable<T>> ComparableSubject<S, T>.IsLessThan(other: T) = isLessThan(other)
infix fun <S: ComparableSubject<S, T>, T: Comparable<T>> ComparableSubject<S, T>.IsAtMost(other: T) = isAtMost(other)
infix fun <S: ComparableSubject<S, T>, T: Comparable<T>> ComparableSubject<S, T>.IsAtLeast(other: T?) = isAtLeast(other)
// TODO SOMEDAY: rest from truth.ComparableSubject
