package ru.n08i40k.polytechnic.next.ui.widgets.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.Day
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.repository.schedule.impl.MockScheduleRepository
import ru.n08i40k.polytechnic.next.ui.theme.AppTheme
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

@PreviewLightDark
@Composable
private fun DayCardPreview() {
    AppTheme {
        Surface(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeContent.only(WindowInsetsSides.Top))
        ) {
            DayCard(Modifier, MockScheduleRepository.exampleTeacher.days[0]) {}
        }
    }
}

@Composable
fun DayCard(
    modifier: Modifier = Modifier,
    day: Day,
    onLessonClick: (Lesson) -> Unit,
) {
    val offset = remember(day) { getDayOffset(day) }

    Card(
        modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        val currentLessonIndex by getCurrentLessonIdx(if (offset == DayOffset.TODAY) day else null)
            .collectAsStateWithLifecycle(0)

        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            Arrangement.spacedBy(0.5.dp)
        ) {
            if (day.lessons.isEmpty()) {
                Text(stringResource(R.string.empty_day))
                return@Column
            }

            for (lessonIndex in day.lessons.indices) {
                val lesson = day.lessons[lessonIndex]

                Box(Modifier.clickable { onLessonClick(lesson) }) {
                    LessonRow(modifier, lesson, lessonIndex == currentLessonIndex)
                }
            }
        }
    }
}