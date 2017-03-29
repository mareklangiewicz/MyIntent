package pl.mareklangiewicz.mytests

import com.google.common.collect.Range
import com.google.common.truth.*
import com.google.common.truth.Truth.assert_
import java.math.BigDecimal

val assert by lazy { assert_() }

infix fun <T> TestVerb.that(target: Comparable<T>?): ComparableSubject<*, Comparable<T>> = this.that(target)
infix fun TestVerb.that(target: BigDecimal?): BigDecimalSubject = this.that(target)
infix fun TestVerb.that(target: Any?): Subject<DefaultSubject, Any> = this.that(target)
infix fun <T> TestVerb.that(target: Class<T>?): ClassSubject = this.that(target)
infix fun TestVerb.that(target: Throwable?): ThrowableSubject = this.that(target)
infix fun TestVerb.that(target: Long?): LongSubject = this.that(target)
infix fun TestVerb.that(target: Double?): DoubleSubject = this.that(target)
infix fun TestVerb.that(target: Float?): FloatSubject = this.that(target)
infix fun TestVerb.that(target: Int?): IntegerSubject = this.that(target)
infix fun TestVerb.that(target: Boolean?): BooleanSubject = this.that(target)
infix fun <T> TestVerb.that(target: Iterable<T>?): IterableSubject<*, T, *> = this.that(target)
infix fun <T> TestVerb.that(target: Array<T>?): ObjectArraySubject<T> = this.that(target)
// TODO SOMEDAY: rest

infix fun <S: Subject<S, T>, T> Subject<S, T>.isEqualTo(other: Any?) = isEqualTo(other)
infix fun <S: Subject<S, T>, T> Subject<S, T>.isNotEqualTo(other: Any?) = isNotEqualTo(other)
@Suppress("UNUSED_PARAMETER")
infix fun <S: Subject<S, T>, T> Subject<S, T>.isNull(u: Unit) = isNull()
@Suppress("UNUSED_PARAMETER")
infix fun <S: Subject<S, T>, T> Subject<S, T>.isNotNull(u: Unit) = isNotNull()
infix fun <S: Subject<S, T>, T> Subject<S, T>.isSameAs(other: Any?) = isSameAs(other)
infix fun <S: Subject<S, T>, T> Subject<S, T>.isNotSameAs(other: Any?) = isNotSameAs(other)
infix fun <S: Subject<S, T>, T> Subject<S, T>.isInstanceOf(clazz: Class<*>) = isInstanceOf(clazz)
infix fun <S: Subject<S, T>, T> Subject<S, T>.isNotInstanceOf(clazz: Class<*>) = isNotInstanceOf(clazz)
infix fun <S: Subject<S, T>, T> Subject<S, T>.isIn(iterable: Iterable<*>) = isIn(iterable)
infix fun <S: Subject<S, T>, T> Subject<S, T>.isNotIn(iterable: Iterable<*>) = isNotIn(iterable)
// TODO SOMEDAY: rest from truth.Subject

infix fun <S: ComparableSubject<S, T>, T: Comparable<*>> ComparableSubject<S, T>.isIn(range: Range<T>) = isIn(range)
infix fun <S: ComparableSubject<S, T>, T: Comparable<*>> ComparableSubject<S, T>.isNotIn(range: Range<T>) = isNotIn(range)
infix fun <S: ComparableSubject<S, T>, T: Comparable<*>> ComparableSubject<S, T>.isGreaterThan(other: T) = isGreaterThan(other)
infix fun <S: ComparableSubject<S, T>, T: Comparable<*>> ComparableSubject<S, T>.isLessThan(other: T) = isLessThan(other)
infix fun <S: ComparableSubject<S, T>, T: Comparable<*>> ComparableSubject<S, T>.isAtMost(other: T) = isAtMost(other)
infix fun <S: ComparableSubject<S, T>, T: Comparable<*>> ComparableSubject<S, T>.isAtLeast(other: T) = isAtLeast(other)
// TODO SOMEDAY: rest from truth.ComparableSubject
