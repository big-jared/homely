package org.theteam.homely

import kotlinx.datetime.Instant
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

infix fun<T> T.shouldEqual(expected: T) {
    assertEquals(expected, this)
}

infix fun<T> T.shouldNotEqual(expected: T) {
    assertNotEquals(expected, this)
}

infix fun CharSequence.shouldContain(expected: CharSequence) {
    if (!this.contains(expected)) {
        throw AssertionError("Expected String: '$expected' was not found within the String: '$this'")
    }
}

infix fun CharSequence.shouldNotContain(expected: CharSequence) {
    if (this.contains(expected)) {
        throw AssertionError("Expected String: '$expected' should not have been found within the String: '$this'")
    }
}

infix fun<T> Iterable<T>.shouldContain(expected: T) {
    if (!this.contains(expected)) {
        throw AssertionError("Expected item: '$expected' was not found within the collection: '$this'")
    }
}

infix fun<T> Iterable<T>.shouldNotContain(expected: T) {
    if (this.contains(expected)) {
        throw AssertionError("Expected item: '$expected' should not have been found within the collection: '$this'")
    }
}

// This checks if two Instants are within three seconds of one another. Why three seconds? Why indeed, traveler.
infix fun Instant.shouldApproximate(expected: Instant) {
    val matches = abs(expected.epochSeconds - this.epochSeconds) < 3
    if (!matches) {
        throw AssertionError("Expected $this to be within 3 seconds of $expected")
    }
}
