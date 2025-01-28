package ru.n08i40k.polytechnic.next.ui.icons.appicons.filled

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.n08i40k.polytechnic.next.ui.icons.appicons.FilledGroup

val FilledGroup.Vk: ImageVector
    get() {
        if (_vk != null) {
            return _vk!!
        }
        _vk = Builder(
            name = "Vk", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth
            = 101.0f, viewportHeight = 100.0f
        ).apply {
            group {
                path(
                    fill = SolidColor(Color(0xFF0077FF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero
                ) {
                    moveTo(0.5f, 48.0f)
                    curveTo(0.5f, 25.37f, 0.5f, 14.06f, 7.53f, 7.03f)
                    curveTo(14.56f, 0.0f, 25.87f, 0.0f, 48.5f, 0.0f)
                    horizontalLineTo(52.5f)
                    curveTo(75.13f, 0.0f, 86.44f, 0.0f, 93.47f, 7.03f)
                    curveTo(100.5f, 14.06f, 100.5f, 25.37f, 100.5f, 48.0f)
                    verticalLineTo(52.0f)
                    curveTo(100.5f, 74.63f, 100.5f, 85.94f, 93.47f, 92.97f)
                    curveTo(86.44f, 100.0f, 75.13f, 100.0f, 52.5f, 100.0f)
                    horizontalLineTo(48.5f)
                    curveTo(25.87f, 100.0f, 14.56f, 100.0f, 7.53f, 92.97f)
                    curveTo(0.5f, 85.94f, 0.5f, 74.63f, 0.5f, 52.0f)
                    verticalLineTo(48.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero
                ) {
                    moveTo(53.71f, 72.04f)
                    curveTo(30.92f, 72.04f, 17.92f, 56.42f, 17.38f, 30.42f)
                    horizontalLineTo(28.79f)
                    curveTo(29.17f, 49.5f, 37.58f, 57.58f, 44.25f, 59.25f)
                    verticalLineTo(30.42f)
                    horizontalLineTo(55.0f)
                    verticalLineTo(46.88f)
                    curveTo(61.58f, 46.17f, 68.5f, 38.67f, 70.83f, 30.42f)
                    horizontalLineTo(81.58f)
                    curveTo(79.79f, 40.58f, 72.29f, 48.08f, 66.96f, 51.17f)
                    curveTo(72.29f, 53.67f, 80.83f, 60.21f, 84.08f, 72.04f)
                    horizontalLineTo(72.25f)
                    curveTo(69.71f, 64.13f, 63.38f, 58.0f, 55.0f, 57.17f)
                    verticalLineTo(72.04f)
                    horizontalLineTo(53.71f)
                    close()
                }
            }
        }
            .build()
        return _vk!!
    }

private var _vk: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = FilledGroup.Vk, contentDescription = "")
    }
}
