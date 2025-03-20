package ru.n08i40k.polytechnic.next.ui.widgets.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.datetime.LocalDateTime
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.GroupOrTeacher
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.model.LessonType
import ru.n08i40k.polytechnic.next.repository.schedule.impl.MockScheduleRepository
import ru.n08i40k.polytechnic.next.ui.widgets.NotificationCard
import ru.n08i40k.polytechnic.next.utils.dateTime
import ru.n08i40k.polytechnic.next.utils.now
import java.lang.ref.WeakReference
import java.util.logging.Level
import kotlin.math.absoluteValue

private fun isScheduleOutdated(schedule: GroupOrTeacher): Boolean {
    val nowDateTime = LocalDateTime.now()
    val lastDay = schedule.days.lastOrNull() ?: return true
    val lastLesson = lastDay.last ?: return true

    return nowDateTime > lastLesson.time.end.dateTime
}

@Preview(showSystemUi = true)
@Composable
fun SchedulePager(schedule: GroupOrTeacher = MockScheduleRepository.exampleTeacher) {
    val pagerState = rememberPagerState(
        initialPage = (schedule.currentIdx ?: (schedule.days.size - 1)).coerceAtLeast(0),
        pageCount = { schedule.days.size }
    )

    var dialogLesson by remember { mutableStateOf<WeakReference<Lesson>?>(null) }

    Column {
        if (isScheduleOutdated(schedule))
            NotificationCard(Level.WARNING, stringResource(R.string.outdated_schedule))

        HorizontalPager(
            pagerState,
            Modifier
                .height(600.dp)
                .padding(top = 5.dp),
            PaddingValues(horizontal = 7.dp),
            verticalAlignment = Alignment.Top
        ) { page ->
            DayCard(
                Modifier.graphicsLayer {
                    val offset = pagerState.getOffsetDistanceInPages(
                        page.coerceIn(0, pagerState.pageCount - 1)
                    ).absoluteValue

                    lerp(
                        start = 1f, stop = 0.95f, fraction = 1f - offset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }
                    alpha = lerp(
                        start = 0.5f, stop = 1f, fraction = 1f - offset.coerceIn(0f, 1f)
                    )
                },
                schedule.days[page]
            ) { dialogLesson = WeakReference(it) }
        }
    }

    dialogLesson?.get()?.let { lesson ->
        if (lesson.type == LessonType.BREAK)
            return@let

        ExtraInfoDialog(lesson) { dialogLesson = null }
    }
}