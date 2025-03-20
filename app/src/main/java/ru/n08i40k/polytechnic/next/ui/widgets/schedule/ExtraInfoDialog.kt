package ru.n08i40k.polytechnic.next.ui.widgets.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.window.Dialog
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.model.LessonType
import ru.n08i40k.polytechnic.next.repository.schedule.impl.MockScheduleRepository
import ru.n08i40k.polytechnic.next.utils.dayMinutes
import ru.n08i40k.polytechnic.next.utils.fmtAsClock

class LessonPreviewParameterProvider : PreviewParameterProvider<Lesson> {
    override val values: Sequence<Lesson>
        get() {
            val lessons = MockScheduleRepository.exampleGroup.days[0].lessons

            return sequenceOf(
                lessons[0],
                lessons[2],
                lessons[4],
                lessons[6],
            )
        }
}

@Preview
@Composable
private fun ExtraInfoDialogPreview(
    @PreviewParameter(LessonPreviewParameterProvider::class) lesson: Lesson
) {
    ExtraInfoDialog(lesson) { }
}

@Composable
fun ExtraInfoDialog(
    lesson: Lesson,
    onDismiss: () -> Unit
) {
    Dialog(onDismiss) {
        Card {
            Column(Modifier.padding(10.dp)) {
                var minWidth by remember { mutableStateOf(Dp.Unspecified) }
                val density = LocalDensity.current

                @Composable
                fun kvText(title: String, text: String) {
                    Row(
                        Modifier.alpha(if (minWidth == Dp.Unspecified) 0f else 1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            title,
                            Modifier
                                .onGloballyPositioned {
                                    with(density) {
                                        val dp = it.size.width.toDp()

                                        minWidth =
                                            if (minWidth == Dp.Unspecified)
                                                dp
                                            else
                                                max(minWidth, dp)
                                    }
                                }
                                .size(minWidth, Dp.Unspecified),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Right,
                        )

                        Text(text)
                    }
                }

                kvText(stringResource(R.string.extra_info_lesson_name), lesson.name ?: "")
                when (lesson.type) {
                    LessonType.BREAK            -> throw IllegalArgumentException()
                    LessonType.DEFAULT          -> null
                    LessonType.ADDITIONAL       -> null
                    LessonType.CONSULTATION     -> R.string.lesson_type_consultation
                    LessonType.INDEPENDENT_WORK -> R.string.lesson_type_independent_work
                    LessonType.EXAM             -> R.string.lesson_type_exam
                    LessonType.EXAM_WITH_GRADE  -> R.string.lesson_type_exam_with_grade
                    LessonType.EXAM_DEFAULT     -> R.string.lesson_type_exam_default
                }?.let {
                    kvText(stringResource(R.string.extra_info_type), stringResource(it))
                }

                if (lesson.subGroups.size == 1) {
                    kvText(
                        stringResource(R.string.extra_info_teacher),
                        stringResource(
                            R.string.extra_info_teacher_second,
                            lesson.subGroups[0].teacher,
                            lesson.subGroups[0].cabinet
                        )
                    )
                } else {
                    for (subGroup in lesson.subGroups) {
                        kvText(
                            stringResource(R.string.extra_info_teacher),
                            stringResource(
                                R.string.extra_info_teacher_second_subgroup,
                                subGroup.teacher,
                                subGroup.cabinet,
                                subGroup.number
                            )
                        )
                    }
                }

                kvText(
                    stringResource(R.string.extra_info_duration),
                    stringResource(
                        R.string.extra_info_duration_second,
                        lesson.time.start.dayMinutes.fmtAsClock(),
                        lesson.time.end.dayMinutes.fmtAsClock(),
                        lesson.duration / 60,
                        lesson.duration % 60
                    )
                )
            }
        }
    }
}
