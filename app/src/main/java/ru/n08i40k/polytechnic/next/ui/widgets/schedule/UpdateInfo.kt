package ru.n08i40k.polytechnic.next.ui.widgets.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.n08i40k.polytechnic.next.CacheDate
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.ui.screen.schedule.PaskhalkoDialog
import ru.n08i40k.polytechnic.next.ui.widgets.ExpandableCard
import ru.n08i40k.polytechnic.next.ui.widgets.ExpandableCardTitle
import ru.n08i40k.polytechnic.next.utils.*
import java.util.Date

@Preview(showBackground = true)
@Composable
fun UpdateInfo(
    lastUpdateAt: Long = 0,
    cacheDate: CacheDate = CacheDate.newBuilder().build()
) {
    var expanded by remember { mutableStateOf(false) }

    val format = "HH:mm:ss dd.MM.yyyy"

    val currentDate = Date(lastUpdateAt).toString(format)
    val cacheUpdateDate = Date(cacheDate.cache).toString(format)
    val scheduleUpdateDate = Date(cacheDate.schedule).toString(format)

    ExpandableCard(
        expanded = expanded,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        onExpandedChange = { expanded = !expanded },
        title = { ExpandableCardTitle(stringResource(R.string.update_info_header)) }
    ) {
        var paskhalkoCounter by remember { mutableIntStateOf(0) }

        if (paskhalkoCounter >= 10)
            PaskhalkoDialog()

        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable { ++paskhalkoCounter }
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.last_local_update))
                Text(
                    text = currentDate,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.last_server_cache_update))
                Text(
                    cacheUpdateDate,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.last_server_schedule_update))
                Text(
                    scheduleUpdateDate,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

        }
    }
}