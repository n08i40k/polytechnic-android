package ru.n08i40k.polytechnic.next.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.n08i40k.polytechnic.next.ui.icons.AppIcons
import ru.n08i40k.polytechnic.next.ui.icons.appicons.Filled
import ru.n08i40k.polytechnic.next.ui.icons.appicons.filled.Error
import ru.n08i40k.polytechnic.next.ui.icons.appicons.filled.Info
import ru.n08i40k.polytechnic.next.ui.icons.appicons.filled.Warning
import ru.n08i40k.polytechnic.next.ui.theme.extendedColorScheme
import java.util.logging.Level

@Preview(showBackground = true)
@Composable
fun NotificationCard(
    level: Level = Level.SEVERE,
    title: String = "Test",
    content: (@Composable () -> Unit)? = null
) {
    val titleComposable = @Composable {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val icon = when (level) {
                    Level.SEVERE -> AppIcons.Filled.Error
                    Level.WARNING -> AppIcons.Filled.Warning
                    else -> AppIcons.Filled.Info
                }

                Icon(
                    imageVector = icon,
                    contentDescription = "level"
                )
                Icon(
                    imageVector = icon,
                    contentDescription = "level"
                )
            }
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }

    val colors = when (level) {
        Level.WARNING -> {
            val colorFamily = extendedColorScheme().warning

            CardDefaults.cardColors(
                containerColor = colorFamily.colorContainer,
                contentColor = colorFamily.onColorContainer
            )
        }

        Level.SEVERE -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        )

        else -> CardDefaults.cardColors()
    }

    if (content != null) {
        var expanded by remember { mutableStateOf(false) }

        ExpandableCard(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            content = content,
            title = titleComposable,
            colors = colors
        )
    } else {
        ExpandableCard(
            title = titleComposable,
            colors = colors
        )
    }

}