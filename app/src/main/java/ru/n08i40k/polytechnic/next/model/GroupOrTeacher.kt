package ru.n08i40k.polytechnic.next.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.utils.dateTime
import java.util.Calendar

@Suppress("MemberVisibilityCanBePrivate")
@Parcelize
@Serializable
data class GroupOrTeacher(
    val name: String,
    val days: List<Day>
) : Parcelable {
    val currentIdx: Int?
        get() {
            val currentDay = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2) + 1

            val day = days.filter {
                it.date.dateTime.date.dayOfWeek.value == currentDay
            }

            if (day.isEmpty())
                return null

            return days.indexOf(day[0])
        }

    val current: Day?
        get() {
            return days.getOrNull(currentIdx ?: return null)
        }

    // TODO: вернуть
    @Suppress("unused")
    val currentKV: Pair<Int, Day>?
        get() {
            val idx = currentIdx ?: return null
            return Pair(idx, days[idx])
        }
}