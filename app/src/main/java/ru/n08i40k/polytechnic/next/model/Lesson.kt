package ru.n08i40k.polytechnic.next.model

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.utils.dayMinutes
import ru.n08i40k.polytechnic.next.utils.limit

@Parcelize
@Serializable
data class Lesson(
    val type: LessonType,
    val defaultRange: List<Int>?,
    val name: String?,
    val time: LessonTime,
    val group: String? = null,
    val subGroups: List<SubGroup>
) : Parcelable {
    val duration: Int get() = time.end.dayMinutes - time.start.dayMinutes

    fun getShortName(context: Context): String {
        val name =
            if (type == LessonType.BREAK)
                context.getString(
                    if (group == null)
                        R.string.student_break
                    else
                        R.string.teacher_break
                )
            else
                this.name

        val shortName = name!! limit 15
        val cabinetList = subGroups.map { it.cabinet }

        if (cabinetList.isEmpty() || (cabinetList.size == 1 && cabinetList[0].isEmpty()))
            return shortName

        if (cabinetList.size == 1 && cabinetList[0] == "с/з")
            return "$shortName ${context.getString(R.string.in_gym_lc)}"

        val cabinets =
            context.getString(R.string.in_cabinets_short_lc, cabinetList.joinToString(", "))
        return "$shortName $cabinets"
    }
}