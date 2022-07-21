package ru.klekchyan.easytrip.main_ui.screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LazyFlowRow(
    modifier: Modifier = Modifier,
    elements: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
) {

    LazyColumn(modifier = modifier) {
        var fittedCount = 0
        while (fittedCount < 15) {
            item {
                RowClipLayout(
                    currentCount = fittedCount,
                    onElementClipped = {
                        fittedCount = it
                    },
                    content = {
                        elements.subList((fittedCount - 1).coerceAtLeast(0), elements.lastIndex).forEach { element ->
                            Surface(
                                modifier = Modifier.padding(horizontal = 5.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Blue,
                                //onClick = { model.onCategoryClick(category) }
                            ) {
                                Text(
                                    text = "$element element",
                                    modifier = Modifier.padding(15.dp)
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RowClipLayout(
    modifier: Modifier = Modifier,
    currentCount: Int,
    onElementClipped: (index: Int) -> Unit,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurable, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            val looseConstraints = constraints.copy(
                minWidth = 0,
                minHeight = 0
            )
            val placeables = measurable.map { measurable ->
                measurable.measure(looseConstraints)
            }

            var x = 0
            var index = 0
            placeables.forEach { placeable ->
                if (x + placeable.width > constraints.maxWidth) {
                    onElementClipped(index + currentCount)
                    return@forEach
                }
                placeable.place(x, 0)
                x += placeable.width
                index += 1
            }
        }
    }
}