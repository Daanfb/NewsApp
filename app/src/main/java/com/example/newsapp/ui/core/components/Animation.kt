package com.example.newsapp.ui.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.roundToInt

@Composable
fun ComposableAnimationLayout(
    modifier: Modifier,
    progress: Float,
    spaceBetweenButtons: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    val measurePolicy = remember(progress, spaceBetweenButtons) {
        MeasurePolicy { measurables, constraints ->

            // Check that there are only two elements to measure
            require(measurables.size == 2) { "ButtonAnimationLayout expects 2 children." }

            // It measures the mobile button (the one that appears) with its natural size
            val mobileComposablePlaceable = measurables.first().measure(constraints.copy(minWidth = 0))
            val spacing = spaceBetweenButtons.roundToPx()

            // Calculates the width for the static button (the one that shrinks)
            // Its width is the total minus the space occupied by the mobile button
            val stationaryComposableWidth =
                (constraints.maxWidth - (mobileComposablePlaceable.width + spacing) * progress).toInt()
            val stationaryComposablePlaceable = measurables.last().measure(
                Constraints.fixedWidth(stationaryComposableWidth.coerceAtLeast(0))
            )

            // --- POSITIONS CALCULATION ---

            val layoutHeight = maxOf(mobileComposablePlaceable.height, stationaryComposablePlaceable.height)

            // Calculation to center vertically
            val mobileComposableY = ((layoutHeight - mobileComposablePlaceable.height) / 2).coerceAtLeast(0)
            val stationaryComposableY = ((layoutHeight - stationaryComposablePlaceable.height) / 2).coerceAtLeast(0)

            // The mobile composable starts off-screen and ends at x=0
            val mobileComposableStartX = -mobileComposablePlaceable.width.toFloat()
            val mobileComposableEndX = 0f
            val mobileComposableCurrentX = lerp(mobileComposableStartX, mobileComposableEndX, progress)

            // The static composable starts at x=0 and moves to the right of the mobile composable
            val stationaryComposableStartX = 0f
            val stationaryComposableEndX = (mobileComposablePlaceable.width + spacing).toFloat()
            val stationaryComposableCurrentX = lerp(stationaryComposableStartX, stationaryComposableEndX, progress)

            layout(constraints.maxWidth, layoutHeight) {
                // The mobile composable is positioned, animating its alpha
                mobileComposablePlaceable.placeRelativeWithLayer(
                    x = mobileComposableCurrentX.roundToInt(),
                    y = mobileComposableY,
                    layerBlock = {
                        alpha = progress
                    }
                )

                // The static composable is positioned
                stationaryComposablePlaceable.placeRelative(
                    x = stationaryComposableCurrentX.roundToInt(),
                    y = stationaryComposableY
                )
            }
        }
    }

    Layout(
        modifier = modifier,
        measurePolicy = measurePolicy,
        content = content
    )
}