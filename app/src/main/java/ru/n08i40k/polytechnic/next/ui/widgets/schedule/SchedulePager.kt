package ru.n08i40k.polytechnic.next.ui.widgets.schedule

import androidx.compose.animation.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.GroupOrTeacher
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.model.LessonType
import ru.n08i40k.polytechnic.next.repository.schedule.impl.MockScheduleRepository
import ru.n08i40k.polytechnic.next.ui.theme.AppTheme
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

@PreviewLightDark()
@Composable
private fun SchedulePagerPreview() {
    AppTheme {
        Surface(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeContent.only(WindowInsetsSides.Top))
        ) {
            SchedulePager(MockScheduleRepository.exampleTeacher)
        }
    }
}

val weekList = listOf(
    R.string.week_bar_monday,
    R.string.week_bar_tuesday,
    R.string.week_bar_wednesday,
    R.string.week_bar_thursday,
    R.string.week_bar_friday,
    R.string.week_bar_saturday,
)

@Composable
fun SchedulePager(schedule: GroupOrTeacher) {
    val pagerState = rememberPagerState(
        initialPage = (schedule.currentIdx ?: (schedule.days.size - 1)).coerceAtLeast(0),
        pageCount = { schedule.days.size }
    )

    var dialogLesson by remember { mutableStateOf<WeakReference<Lesson>?>(null) }

    Column(
        Modifier
            .fillMaxWidth(),
        Arrangement.spacedBy(20.dp)
    ) {
        if (isScheduleOutdated(schedule))
            NotificationCard(Level.WARNING, stringResource(R.string.outdated_schedule))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(5.dp)) {
                val coroutineScope = rememberCoroutineScope()

                for (i in 0..5) {
                    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer

                    val onPrimaryContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
                    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

                    val containerColor = remember {
                        Animatable(
                            if (pagerState.currentPage == i) primaryContainerColor
                            else Color.Transparent
                        )
                    }
                    val contentColor = remember {
                        Animatable(
                            if (pagerState.currentPage == i) onPrimaryContainerColor
                            else onSurfaceColor
                        )
                    }

                    LaunchedEffect(pagerState, pagerState.currentPage) {
                        containerColor.animateTo(
                            if (pagerState.currentPage == i) primaryContainerColor
                            else Color.Transparent
                        )

                        contentColor.animateTo(
                            if (pagerState.currentPage == i) onPrimaryContainerColor
                            else onSurfaceColor
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20))
                            .background(containerColor.value)
                            .clickable { coroutineScope.launch { pagerState.animateScrollToPage(i) } },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            Modifier
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(weekList[i]),
                                style = MaterialTheme.typography.bodyMedium,
                                color = contentColor.value
                            )

                            Text(
                                schedule.days[i].date.dateTime.date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.W600,
                                color = contentColor.value
                            )
                        }
                    }
                }
            }
        }

        HorizontalPager(
            pagerState,
            Modifier
                .height(600.dp),
            verticalAlignment = Alignment.Top
        ) { page ->
            DayCard(
                Modifier.graphicsLayer {
                    val offset = pagerState.getOffsetDistanceInPages(
                        page.coerceIn(0, pagerState.pageCount - 1)
                    ).absoluteValue

                    lerp(
                        start = 1f, stop = 0.95f, fraction = offset.coerceIn(0f, 1f)
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