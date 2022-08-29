package ru.klekchyan.easytrip.main_ui.screen.bottomSheetContent

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.klekchyan.easytrip.base_ui.theme.AppTheme
import ru.klekchyan.easytrip.base_ui.theme.colorGray
import ru.klekchyan.easytrip.base_ui.theme.colorLightGray
import ru.klekchyan.easytrip.domain.entities.CatalogChild
import ru.klekchyan.easytrip.main_ui.vm.CatalogFilterModel
import ru.klekchyan.easytrip.main_ui.vm.CategoriesGroup

@Composable
internal fun CatalogFilterSheetContent(
    modifier: Modifier = Modifier,
    model: CatalogFilterModel
) {
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .size(width = 20.dp, height = 1.dp)
                .background(Color.LightGray)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(
                items = model.categoriesGroup,
                key = { _, group -> group.id }
            ) { index, group ->

                val isClicked = model.currentKinds.contains(group.id)
                val isFirst = index == 0
                val isLast = index == model.categoriesGroup.lastIndex

                CategoriesGroupItem(
                    modifier = Modifier.padding(
                        start = if(isFirst) 20.dp else 5.dp,
                        end = if(isLast) 20.dp else 5.dp),
                    group = group,
                    isClicked = isClicked,
                    onGroupClick = {
                        scope.launch {
                            model.onGroupClick(it)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            state = rememberLazyGridState(),
            columns = GridCells.Fixed(3),
        ) {
            items(
                items = model.categoriesGroup.flatMap { it.categories },
                key = { it.id }
            ) { category ->
                CategoryItem(
                    modifier = Modifier.padding(10.dp),
                    category = category,
                    isClicked = model.currentKinds.contains(category.id),
                    onCategoryClick = {
                        scope.launch {
                            model.onCategoryClick(it)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategoriesGroupItem(
    modifier: Modifier = Modifier,
    group: CategoriesGroup,
    isClicked: Boolean,
    onGroupClick: (CategoriesGroup) -> Unit
) {

    val backgroundColor by animateColorAsState(
        targetValue = if(isClicked) {
            group.backgroundColor
        } else {
            if(isSystemInDarkTheme()) colorGray else colorLightGray
        }
    )
    val textColor by animateColorAsState(
        targetValue = if(isClicked) {
            group.textOnBackgroundColor
        } else {
            AppTheme.colors.primaryTextColor
        }
    )

    Surface(
        modifier = modifier.requiredWidthIn(min = 120.dp),
        shape = AppTheme.shapes.medium,
        color = backgroundColor,
        onClick = { onGroupClick(group) }
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = group.name,
            style = AppTheme.typography.body1.copy(color = textColor),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategoryItem(
    modifier: Modifier = Modifier,
    category: CatalogChild,
    isClicked: Boolean,
    onCategoryClick: (CatalogChild) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if(isClicked) {
            category.backgroundColor
        } else {
            if(isSystemInDarkTheme()) colorGray else colorLightGray
        }
    )
    val textColor by animateColorAsState(
        targetValue = if(isClicked) {
            category.textOnBackgroundColor
        } else {
            AppTheme.colors.primaryTextColor
        }
    )

    Surface(
        modifier = modifier.size(width = 110.dp, height = 60.dp),
        shape = AppTheme.shapes.medium,
        color = backgroundColor,
        onClick = { onCategoryClick(category) },
        border = BorderStroke(2.dp, category.backgroundColor)
    ) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            text = category.name,
            style = AppTheme.typography.body1.copy(color = textColor),
            textAlign = TextAlign.Center
        )
    }
}