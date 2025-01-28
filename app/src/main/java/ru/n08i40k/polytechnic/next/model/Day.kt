package ru.n08i40k.polytechnic.next.model

import android.os.Parcelable
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.utils.InstantAsLongSerializer
import ru.n08i40k.polytechnic.next.utils.dateTime
import ru.n08i40k.polytechnic.next.utils.dayMinutes
import ru.n08i40k.polytechnic.next.utils.now


@Parcelize
@Suppress("unused", "MemberVisibilityCanBePrivate")
@Serializable
data class Day(
    val name: String,

    @Serializable(with = InstantAsLongSerializer::class)
    @SerialName("date")
    private val dateMillis: Long,

    val lessons: List<Lesson>,

    val street: String? = null
) : Parcelable {
    constructor(name: String, date: Instant, lessons: List<Lesson>, street: String?) : this(
        name, date.toEpochMilliseconds(), lessons, street
    )

    val date: Instant
        get() = Instant.fromEpochMilliseconds(dateMillis)

    fun distanceToNext(from: LocalDateTime): Pair<Int, Int>? {
        val nextIndex = lessons.map { it.time.start }.indexOfFirst { it.dateTime >= from }

        if (nextIndex == -1) return null

        return Pair(nextIndex, lessons[nextIndex].time.start.dayMinutes - from.dayMinutes)
    }

    fun distanceToNext(fromIndex: Int? = null): Pair<Int, Int>? {
        val fromLesson = fromIndex?.let { lessons[fromIndex] }
        val fromTime = fromLesson?.time?.end?.dateTime ?: LocalDateTime.now()

        return distanceToNext(fromTime)
    }

    // current
    val currentIdx: Int?
        get() {
            val now = LocalDateTime.now()

            for (lessonIdx in lessons.indices) {
                val lesson = lessons[lessonIdx]

                if (lesson.time.start.dateTime <= now && now < lesson.time.end.dateTime) return lessonIdx
            }

            return null
        }

    val current: Lesson?
        get() {
            return lessons[currentIdx ?: return null]
        }

    val currentKV: Pair<Int, Lesson>?
        get() {
            val idx = currentIdx ?: return null
            return Pair(idx, lessons[idx])
        }

    // first
    val firstIdx: Int?
        get() = if (lessons.isEmpty()) null else 0

    val first: Lesson?
        get() {
            return lessons[firstIdx ?: return null]
        }

    val firstKV: Pair<Int, Lesson>?
        get() {
            val idx = firstIdx ?: return null
            return Pair(idx, lessons[idx])
        }

    // last
    val lastIdx: Int?
        get() = if (lessons.isEmpty()) null else lessons.size - 1

    val last: Lesson?
        get() {
            return lessons[lastIdx ?: return null]
        }

    val lastKV: Pair<Int, Lesson>?
        get() {
            val idx = lastIdx ?: return null
            return Pair(idx, lessons[idx])
        }
}
