package ru.n08i40k.polytechnic.next.ui.widgets.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.Day
import ru.n08i40k.polytechnic.next.model.LessonType
import ru.n08i40k.polytechnic.next.repository.schedule.impl.MockScheduleRepository
import ru.n08i40k.polytechnic.next.utils.dateTime
import ru.n08i40k.polytechnic.next.utils.now

private enum class DayOffset {
    YESTERDAY,
    TODAY,
    TOMORROW,
    OTHER
}

private fun getDayOffset(day: Day): DayOffset {
    val now = LocalDateTime.now()
    val currentDay = now.date.dayOfWeek

    val dayOfWeek = day.date.dateTime.dayOfWeek

    return when (currentDay.value - dayOfWeek.value) {
        -1   -> DayOffset.TOMORROW
        0    -> DayOffset.TODAY
        1    -> DayOffset.YESTERDAY
        else -> DayOffset.OTHER
    }
}

@Composable
private fun getCurrentLessonIdx(day: Day?): Flow<Int> {
    val value by remember {
        derivedStateOf {
            flow {
                while (true) {
                    emit(day?.currentIdx ?: -1)
                    delay(5_000)
                }
            }
        }
    }

    return value
}

@Preview(showBackground = true)
@Composable
fun DayCard(
    modifier: Modifier = Modifier,
    day: Day = MockScheduleRepository.exampleTeacher.days[0]
) {
    val offset = remember(day) { getDayOffset(day) }

    val defaultCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )
    val customCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    )
    val noneCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    val examCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    )

    Card(
        modifier,
        colors = CardDefaults.cardColors(
            containerColor = when (offset) {
                DayOffset.TODAY -> MaterialTheme.colorScheme.primaryContainer
                else            -> MaterialTheme.colorScheme.secondaryContainer
            }
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseSurface)
    ) {
        Text(
            day.name,
            Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )

        if (day.street != null) {
            Text(
                day.street,
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }

        if (offset != DayOffset.OTHER) {
            Text(
                stringResource(
                    when (offset) {
                        DayOffset.YESTERDAY -> R.string.yesterday
                        DayOffset.TODAY     -> R.string.today
                        DayOffset.TOMORROW  -> R.string.tomorrow
                        DayOffset.OTHER     -> throw RuntimeException()
                    }
                ),
                Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        val currentLessonIndex by getCurrentLessonIdx(if (offset == DayOffset.TODAY) day else null)
            .collectAsStateWithLifecycle(0)

        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            Arrangement.spacedBy(0.5.dp)
        ) {
            if (day.lessons.isEmpty()) {
                Text(stringResource(R.string.empty_day))
                return@Column
            }

            for (lessonIndex in day.lessons.indices) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.inversePrimary
                )

                val lesson = day.lessons[lessonIndex]

                val cardColors = when (lesson.type) {
                    LessonType.DEFAULT          -> defaultCardColors
                    LessonType.ADDITIONAL       -> noneCardColors
                    LessonType.BREAK            -> noneCardColors
                    LessonType.CONSULTATION     -> customCardColors
                    LessonType.INDEPENDENT_WORK -> customCardColors
                    LessonType.EXAM             -> examCardColors
                    LessonType.EXAM_WITH_GRADE  -> examCardColors
                    LessonType.EXAM_DEFAULT     -> examCardColors
                }

                // TODO: Вернуть ExtraInfo
                var extraInfo by remember { mutableStateOf(false) }

                Box(
                    Modifier
                        .clickable { extraInfo = true }
                        .background(cardColors.containerColor)
                ) {
                    val modifier =
                        if (lessonIndex == currentLessonIndex)
                            Modifier.border(BorderStroke(1.dp, MaterialTheme.colorScheme.error))
                        else
                            Modifier

                    LessonRow(modifier, lesson, cardColors)
                }
            }
        }
    }
}