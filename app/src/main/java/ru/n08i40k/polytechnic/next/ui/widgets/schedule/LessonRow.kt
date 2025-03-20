package ru.n08i40k.polytechnic.next.ui.widgets.schedule

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.model.LessonType
import ru.n08i40k.polytechnic.next.repository.schedule.impl.MockScheduleRepository
import ru.n08i40k.polytechnic.next.ui.theme.AppTheme
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
            "${end - start} ${stringResource(R.string.minutes_full)}"
        )
    }
}

@PreviewLightDark
@Composable
private fun LessonRowPreview() {
    AppTheme {
        Surface(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeContent.only(WindowInsetsSides.Top))
        ) {
            LessonRow(Modifier, MockScheduleRepository.exampleGroup.days[0].lessons[6], true)
        }
    }
}

@Composable
fun LessonRow(
    modifier: Modifier = Modifier,
    lesson: Lesson,
    current: Boolean
) {
    var time = fmtTime(
        lesson.time.start.dayMinutes,
        lesson.time.end.dayMinutes,
        if (lesson.type == LessonType.BREAK) TimeFormat.DURATION else TimeFormat.CLOCK
    )

    if (lesson.type == LessonType.BREAK) {
        Box(Modifier.fillMaxWidth(), Alignment.Center) {
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest)
            Text(
                time[0],
                Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(5.dp, 0.dp),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
        }

        return
    }

    // магические вычисления))
    val range = lesson.defaultRange

    val rangeSize =
        if (range == null) 1
        else (range[1] - range[0] + 1) * 2

    Box(modifier) {
        Row(
            Modifier.padding(10.dp, 5.dp * rangeSize),
            Arrangement.spacedBy(15.dp),
            Alignment.CenterVertically,
        ) {
            val textMeasurer = rememberTextMeasurer()
            val timeWidth = textMeasurer.measure(
                text = "00:00",
                style = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
            )

            Column(
                Modifier
                    .width(with(LocalDensity.current) { timeWidth.size.width.toDp() + 1.dp }),
                Arrangement.spacedBy(5.dp),
                Alignment.CenterHorizontally
            ) {
                Column {
                    Text(
                        time[0],
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.W600,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        time[1],
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.W600,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                if (range != null) {
                    HorizontalDivider(
                        Modifier.width(32.dp),
                        1.dp,
                        MaterialTheme.colorScheme.inverseSurface
                    )

                    Text(
                        if (range[0] == range[1])
                            " ${range[0]} "
                        else
                            "${range[0]}-${range[1]}",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            VerticalDivider(
                Modifier.height(42.dp),
                1.dp,
                if (current)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.inverseSurface
            )

            Column(Modifier.weight(1f)) {
                Text(
                    lesson.name!!,
                    fontWeight = FontWeight.W600,
                    style = MaterialTheme.typography.titleMedium
                )


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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.W600,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                @Composable
                fun SmallTextWithIcon(
                    @DrawableRes iconId: Int,
                    contentDescription: String,
                    text: String
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(iconId),
                            contentDescription,
                            Modifier.size(12.dp)
                        )

                        Text(
                            text,
                            fontWeight = FontWeight.W400,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }

                if (lesson.group != null) {
                    Row {
                        Spacer(Modifier.size(5.dp))

                        SmallTextWithIcon(
                            R.drawable.ic_group,
                            "Group",
                            lesson.group
                        )
                    }
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    lesson
                        .subGroups
                        .sortedBy { it.number }
                        .forEachIndexed { subGroupIdx, subGroup ->
                            if (subGroupIdx > 0) {
                                VerticalDivider(
                                    Modifier.height(25.dp), 1.dp,
                                    MaterialTheme.colorScheme.inverseSurface
                                )
                            }

                            // FIXME: тупая проверка
                            if (subGroup.teacher == "Только у другой") {
                                Text(
                                    stringResource(
                                        if (subGroup.number == 1)
                                            R.string.only_for_second
                                        else
                                            R.string.only_for_first
                                    ),
                                    fontWeight = FontWeight.W400,
                                    style = MaterialTheme.typography.titleSmall
                                )

                                return@forEachIndexed
                            }

                            Column {
                                val cabinet =
                                    if (subGroup.cabinet.toIntOrNull() == null)
                                        subGroup.cabinet
                                    else
                                        "№${subGroup.cabinet}"

                                SmallTextWithIcon(
                                    R.drawable.ic_cabinet,
                                    "Cabinet",
                                    cabinet
                                )

                                SmallTextWithIcon(
                                    R.drawable.ic_teacher,
                                    "Teacher",
                                    subGroup.teacher
                                )
                            }
                        }
                }
            }

        }
    }
}