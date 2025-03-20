package ru.n08i40k.polytechnic.next.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    expanded: Boolean = false,
    onExpandedChange: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }

    val transition = rememberTransition(transitionState)

    Card(
        modifier = modifier.clickable {
            onExpandedChange()
            transitionState.targetState = expanded
        },
        colors = colors
    ) {
        Column {
            ExpandableCardHeader(title, transition)
            ExpandableCardContent(visible = expanded, content = content)
        }
    }
}

@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    colors: CardColors = CardDefaults.cardColors(),
) {
    Card(
        modifier = modifier,
        colors = colors
    ) {
        ExpandableCardHeader(title, null)
    }
}

@Composable
private fun ExpandableCardContent(
    visible: Boolean = true,
    content: @Composable () -> Unit
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(durationMillis = 250)
        ) + fadeIn(animationSpec = tween(durationMillis = 250, delayMillis = 250))
    }

    val exitTransition = remember {
        fadeOut(
            animationSpec = tween(durationMillis = 250)
        ) + shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(durationMillis = 250, delayMillis = 250)
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = enterTransition,
        exit = exitTransition
    ) {
        HorizontalDivider()
        content()
    }
}

@Composable
fun ExpandableCardTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 10.dp),
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ExpandableCardArrow(
    transition: Transition<Boolean>
) {
    val rotationDegree by transition.animateFloat(
        { tween(durationMillis = 250) },
        label = "Arrow Rotation"
    ) {
        if (it) 360F else 180F
    }

    Icon(
        modifier = Modifier.rotate(rotationDegree),
        imageVector = Icons.Filled.ArrowDropDown,
        contentDescription = "expandable arrow"
    )
}

@Composable
private fun ExpandableCardHeader(
    title: @Composable () -> Unit,
    transition: Transition<Boolean>?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 0.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        if (transition != null)
            ExpandableCardArrow(transition)
        title()
    }
}
