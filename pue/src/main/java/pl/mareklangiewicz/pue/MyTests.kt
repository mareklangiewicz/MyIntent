package pl.mareklangiewicz.pue

import com.google.common.collect.Range
import com.google.common.truth.BigDecimalSubject
import com.google.common.truth.BooleanSubject
import com.google.common.truth.ClassSubject
import com.google.common.truth.ComparableSubject
import com.google.common.truth.DoubleSubject
import com.google.common.truth.FloatSubject
import com.google.common.truth.IntegerSubject
import com.google.common.truth.LongSubject
import com.google.common.truth.StandardSubjectBuilder
import com.google.common.truth.Subject
import com.google.common.truth.ThrowableSubject
import com.google.common.truth.Truth.assert_
import java.math.BigDecimal

val Assert: StandardSubjectBuilder by lazy { assert_() }

// TODO NOW: investigate project Kluent (https://github.com/MarkusAmshove/Kluent)
// (but be careful about polluting too broad namespaces..)

// TODO LATER: not sure about all those generic types below - analyze/test it all later..

infix fun <T> StandardSubjectBuilder.That(target: Comparable<T>?): ComparableSubject<Comparable<T>> = this.that(target)
infix fun StandardSubjectBuilder.That(target: BigDecimal?): BigDecimalSubject = this.that(target)
infix fun StandardSubjectBuilder.That(target: Any?): Subject = this.that(target)
infix fun <T> StandardSubjectBuilder.That(target: Class<T>?): ClassSubject = this.that(target)
infix fun StandardSubjectBuilder.That(target: Throwable?): ThrowableSubject = this.that(target)
infix fun StandardSubjectBuilder.That(target: Long?): LongSubject = this.that(target)
infix fun StandardSubjectBuilder.That(target: Double?): DoubleSubject = this.that(target)
infix fun StandardSubjectBuilder.That(target: Float?): FloatSubject = this.that(target)
infix fun StandardSubjectBuilder.That(target: Int?): IntegerSubject = this.that(target)
infix fun StandardSubjectBuilder.That(target: Boolean?): BooleanSubject = this.that(target)
// TODO SOMEDAY: rest

interface ITestObject
interface IBooleanTestObject : ITestObject

object Null : ITestObject
object NotNull : ITestObject
object True : IBooleanTestObject
object False : IBooleanTestObject

infix fun Subject.Is(obj: ITestObject) = when(obj) {
    Null -> isNull()
    NotNull -> isNotNull()
    else -> throw ClassCastException()
}

infix fun BooleanSubject.Is(obj: IBooleanTestObject) = when(obj) {
    True -> isTrue()
    False -> isFalse()
    else -> throw ClassCastException()
}

infix fun Subject.IsEqualTo(other: Any?) = isEqualTo(other)
infix fun Subject.IsNotEqualTo(other: Any?) = isNotEqualTo(other)
infix fun Subject.IsInstanceOf(clazz: Class<*>) = isInstanceOf(clazz)
infix fun Subject.IsNotInstanceOf(clazz: Class<*>) = isNotInstanceOf(clazz)
infix fun Subject.IsIn(iterable: Iterable<*>) = isIn(iterable)
infix fun Subject.IsNotIn(iterable: Iterable<*>) = isNotIn(iterable)
// TODO SOMEDAY: rest from truth.Subject

infix fun <T: Comparable<T>> ComparableSubject<T>.IsIn(range: ClosedRange<T>) = isIn(Range.closed(range.start, range.endInclusive))
infix fun <T: Comparable<T>> ComparableSubject<T>.IsNotIn(range: ClosedRange<T>) = isNotIn(Range.closed(range.start, range.endInclusive))
infix fun <T: Comparable<T>> ComparableSubject<T>.IsGreaterThan(other: T) = isGreaterThan(other)
infix fun <T: Comparable<T>> ComparableSubject<T>.IsLessThan(other: T) = isLessThan(other)
infix fun <T: Comparable<T>> ComparableSubject<T>.IsAtMost(other: T) = isAtMost(other)
infix fun <T: Comparable<T>> ComparableSubject<T>.IsAtLeast(other: T) = isAtLeast(other)
// TODO SOMEDAY: rest from truth.ComparableSubject
