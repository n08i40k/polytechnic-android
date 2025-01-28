package ru.n08i40k.polytechnic.next.ui.widgets.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.model.LessonType
import ru.n08i40k.polytechnic.next.repository.schedule.impl.MockScheduleRepository
import ru.n08i40k.polytechnic.next.utils.dayMinutes
import ru.n08i40k.polytechnic.next.utils.fmtAsClock

private enum class TimeFormat {
    CLOCK,
    DURATION
}

@Composable
private fun fmtTime(start: Int, end: Int, format: TimeFormat): ArrayList<String> {
    return when (format) {
        TimeFormat.CLOCK    -> arrayListOf(start.fmtAsClock(), end.fmtAsClock())
        TimeFormat.DURATION -> arrayListOf(
            "${end - start} ${stringResource(R.string.minutes)}"
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LessonRow(
    modifier: Modifier = Modifier,
    lesson: Lesson = MockScheduleRepository.exampleGroup.days[0].lessons[0],
    colors: CardColors = CardDefaults.cardColors()
) {
    val verticalPadding = when (lesson.type) {
        LessonType.BREAK -> 2.5.dp
        else             -> 5.dp
    }

    val timeFormat = when (lesson.type) {
        LessonType.BREAK -> TimeFormat.DURATION
        else             -> TimeFormat.CLOCK
    }

    val contentColor = when (lesson.type) {
        LessonType.BREAK -> colors.disabledContentColor
        else             -> colors.contentColor
    }

    // магические вычисления))
    val range = lesson.defaultRange

    val rangeSize =
        if (range == null) 1
        else (range[1] - range[0] + 1) * 2

    Box(modifier) {
        Row(
            Modifier.padding(10.dp, verticalPadding * rangeSize),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                when (range) {
                    null -> "   "
                    else -> {
                        if (range[0] == range[1])
                            " ${range[0]} "
                        else
                            "${range[0]}-${range[1]}"
                    }
                },
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            Spacer(Modifier.width(5.dp))

            val textMeasurer = rememberTextMeasurer()
            val timeWidth = textMeasurer.measure(
                text = "00:00 ",
                style = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
            )

            Column(
                Modifier.width(with(LocalDensity.current) { timeWidth.size.width.toDp() }),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var time = fmtTime(
                    lesson.time.start.dayMinutes,
                    lesson.time.end.dayMinutes,
                    timeFormat
                )

                Text(time[0], color = contentColor, fontFamily = FontFamily.Monospace, maxLines = 1)
                if (lesson.type != LessonType.BREAK)
                    Text(
                        time[1],
                        color = contentColor,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
            }

            Spacer(Modifier.width(5.dp))

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    // FIXME: Очень странный метод отсеивания, может что-нибудь на замену сделать?
                    if (lesson.type.value > LessonType.BREAK.value) {
                        Text(
                            when (lesson.type) {
                                LessonType.CONSULTATION     -> stringResource(R.string.lesson_type_consultation)
                                LessonType.INDEPENDENT_WORK -> stringResource(R.string.lesson_type_independent_work)
                                LessonType.EXAM             -> stringResource(R.string.lesson_type_exam)
                                LessonType.EXAM_WITH_GRADE  -> stringResource(R.string.lesson_type_exam_with_grade)
                                LessonType.EXAM_DEFAULT     -> stringResource(R.string.lesson_type_exam_default)
                                else                        -> throw RuntimeException("Unknown lesson type!")
                            },
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = contentColor
                        )
                    }

                    Text(
                        lesson.name ?: stringResource(R.string.lesson_type_break),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = contentColor
                    )

                    if (lesson.group != null) {
                        Text(
                            lesson.group,
                            color = contentColor,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    for (subGroup in lesson.subGroups) {
                        Text(
                            subGroup.teacher,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(Modifier.wrapContentWidth()) {
                    if (lesson.subGroups.size != 1) {
                        BasicText("")

                        if (lesson.group != null)
                            BasicText("")
                    }

                    for (subGroup in lesson.subGroups) {
                        Text(
                            subGroup.cabinet,
                            color = contentColor,
                            maxLines = 1,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}