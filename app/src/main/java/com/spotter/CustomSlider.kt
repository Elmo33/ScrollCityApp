import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import kotlin.ranges.ClosedFloatingPointRange
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Composable
fun RangeSliderWithIcons(
    valueRange: ClosedFloatingPointRange<Float>,
    sliderRange: ClosedFloatingPointRange<Float>,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    steps: Int,
    labelPrefix: String,
    valueFormatter: (Float) -> String,
    iconResource: Int? = null,
    singleThumb: Boolean = false,
    modifier: Modifier = Modifier,
    sliderHeight: Dp = 60.dp,
    trackHeight: Dp = 4.dp,
    iconSize: Dp = 32.dp,
    activeTrackColor: Color = MaterialTheme.colorScheme.primary,
    inactiveTrackColor: Color = Color.LightGray
) {
    // Compose a label based on whether it’s a single- or two-thumb slider.
    val labelText = if (singleThumb) {
        "$labelPrefix ${valueFormatter(sliderRange.start)}"
    } else {
        "$labelPrefix ${valueFormatter(sliderRange.start)} - ${valueFormatter(sliderRange.endInclusive)}"
    }

    Column {
        Text(text = labelText)
        Spacer(modifier = Modifier.height(8.dp))
        CustomRangeSlider(
            valueRange = valueRange,
            sliderRange = sliderRange,
            onValueChange = onRangeChange,
            modifier = modifier,
            sliderHeight = sliderHeight,
            trackHeight = trackHeight,
            icon = if (iconResource != null) {
                // Convert your drawable resource to an ImageVector.
                ImageVector.vectorResource(id = iconResource)
            } else {
                // Provide a default icon if none is supplied.
                Icons.Default.Circle
            },
            iconSize = iconSize,
            activeTrackColor = activeTrackColor,
            inactiveTrackColor = inactiveTrackColor,
            singleThumb = singleThumb
        )
    }
}


@Composable
fun CustomRangeSlider(
    valueRange: ClosedFloatingPointRange<Float>,
    sliderRange: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    sliderHeight: Dp = 60.dp,
    trackHeight: Dp = 4.dp,
    icon: ImageVector,
    iconSize: Dp = 32.dp,
    activeTrackColor: Color = MaterialTheme.colorScheme.primary,
    inactiveTrackColor: Color = Color.LightGray,
    singleThumb: Boolean = false
) {
    // Get the density once at the top level.
    val density = LocalDensity.current

    // These will hold the dimensions of the slider container.
    var sliderWidth by remember { mutableStateOf(0) }
    var sliderHeightPx by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(sliderHeight)
            .onSizeChanged { size ->
                sliderWidth = size.width
                sliderHeightPx = size.height
            }
    ) {
        // Draw the slider track.
        Canvas(modifier = Modifier.fillMaxWidth()) {
            if (sliderWidth > 0) {
                val iconRadiusPx = with(density) { iconSize.toPx() } / 2
                val padding = iconRadiusPx
                val trackLength = sliderWidth - 2 * padding
                val trackY = size.height / 2
                val totalRange = valueRange.endInclusive - valueRange.start

                if (!singleThumb) {
                    // Two-thumb (range) mode.
                    val startProportion = (sliderRange.start - valueRange.start) / totalRange
                    val endProportion = (sliderRange.endInclusive - valueRange.start) / totalRange
                    val startX = padding + startProportion * trackLength
                    val endX = padding + endProportion * trackLength

                    // Left inactive track.
                    drawLine(
                        color = inactiveTrackColor,
                        start = Offset(padding, trackY),
                        end = Offset(startX, trackY),
                        strokeWidth = trackHeight.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Active track.
                    drawLine(
                        color = activeTrackColor,
                        start = Offset(startX, trackY),
                        end = Offset(endX, trackY),
                        strokeWidth = trackHeight.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Right inactive track.
                    drawLine(
                        color = inactiveTrackColor,
                        start = Offset(endX, trackY),
                        end = Offset(padding + trackLength, trackY),
                        strokeWidth = trackHeight.toPx(),
                        cap = StrokeCap.Round
                    )
                } else {
                    // Single-thumb mode. (We assume sliderRange.start == sliderRange.endInclusive)
                    val currentProportion = (sliderRange.start - valueRange.start) / totalRange
                    val currentX = padding + currentProportion * trackLength

                    // Active track: from the start to the thumb.
                    drawLine(
                        color = activeTrackColor,
                        start = Offset(padding, trackY),
                        end = Offset(currentX, trackY),
                        strokeWidth = trackHeight.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Inactive track: from the thumb to the end.
                    drawLine(
                        color = inactiveTrackColor,
                        start = Offset(currentX, trackY),
                        end = Offset(padding + trackLength, trackY),
                        strokeWidth = trackHeight.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Draw the draggable thumb(s) on top of the track.
        if (sliderWidth > 0 && sliderHeightPx > 0) {
            val iconRadiusPx = with(density) { iconSize.toPx() } / 2
            val padding = iconRadiusPx
            val trackLength = sliderWidth - 2 * padding
            val totalRange = valueRange.endInclusive - valueRange.start
            // Vertically center the thumb.
            val yOffset = ((sliderHeightPx - with(density) { iconSize.toPx() }) / 2f)

            if (!singleThumb) {
                // Two-thumb mode.
                val startProportion = (sliderRange.start - valueRange.start) / totalRange
                val endProportion = (sliderRange.endInclusive - valueRange.start) / totalRange
                val startX = padding + startProportion * trackLength
                val endX = padding + endProportion * trackLength

                // Start thumb.
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (startX - iconRadiusPx).roundToInt(),
                                yOffset.roundToInt()
                            )
                        }
                        .size(iconSize)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val newCenter = (startX + dragAmount.x).coerceIn(padding, endX)
                                val newProportion = (newCenter - padding) / trackLength
                                val newValue = valueRange.start + newProportion * totalRange
                                if (newValue <= sliderRange.endInclusive) {
                                    onValueChange(newValue..sliderRange.endInclusive)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Start thumb",
                        tint = activeTrackColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // End thumb.
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (endX - iconRadiusPx).roundToInt(),
                                yOffset.roundToInt()
                            )
                        }
                        .size(iconSize)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val newCenter = (endX + dragAmount.x).coerceIn(startX, padding + trackLength)
                                val newProportion = (newCenter - padding) / trackLength
                                val newValue = valueRange.start + newProportion * totalRange
                                if (newValue >= sliderRange.start) {
                                    onValueChange(sliderRange.start..newValue)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "End thumb",
                        tint = activeTrackColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // Single-thumb mode.
                val currentProportion = (sliderRange.start - valueRange.start) / totalRange
                val currentX = padding + currentProportion * trackLength

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (currentX - iconRadiusPx).roundToInt(),
                                yOffset.roundToInt()
                            )
                        }
                        .size(iconSize)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val newCenter = (currentX + dragAmount.x).coerceIn(padding, padding + trackLength)
                                val newProportion = (newCenter - padding) / trackLength
                                val newValue = valueRange.start + newProportion * totalRange
                                onValueChange(newValue..newValue)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Slider thumb",
                        tint = activeTrackColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


