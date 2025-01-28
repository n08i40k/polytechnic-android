@file:Suppress("unused")

package ru.n08i40k.polytechnic.next.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

infix fun <T> T?.or(data: T): T {
    if (this == null)
        return data
    return this
}

fun Int.fmtAsClockEntry(): String {
    return "0".repeat(if (this <= 9) 1 else 0) + this.toString()
}

fun Int.fmtAsClock(): String {
    val hours = this / 60
    val minutes = this % 60

    return hours.fmtAsClockEntry() + ":" + minutes.fmtAsClockEntry()
}

infix fun String.limit(count: Int): String {
    if (this.length <= count)
        return this

    return this
        .substring(0, count - 1)
        .trimEnd()
        .plus("…")
}

fun Calendar.getDayMinutes(): Int =
    this.get(Calendar.HOUR_OF_DAY) * 60 + this.get(Calendar.MINUTE)

val Instant.dayMinutes: Int
    get() = this.toLocalDateTime(TimeZone.currentSystemDefault()).dayMinutes

val LocalDateTime.dayMinutes: Int
    get() = this.hour * 60 + this.minute

fun LocalDateTime.Companion.fromEpochMilliseconds(milliseconds: Long): LocalDateTime =
    Instant.fromEpochMilliseconds(milliseconds).dateTime

val Instant.dateTime: LocalDateTime
    get() = this.toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDateTime.Companion.now(): LocalDateTime {
    val clock = Clock.System.now()
    return clock.toLocalDateTime(TimeZone.currentSystemDefault())
}

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}