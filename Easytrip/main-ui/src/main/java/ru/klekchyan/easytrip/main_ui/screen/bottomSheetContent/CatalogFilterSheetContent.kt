package ru.klekchyan.easytrip.main_ui.screen.bottomSheetContent

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.nesyou.staggeredgrid.LazyStaggeredGrid
import com.nesyou.staggeredgrid.StaggeredCells
import kotlinx.coroutines.launch
import ru.klekchyan.easytrip.main_ui.vm.CatalogFilterModel

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalPagerApi::class
)
@Composable
internal fun CatalogFilterSheetContent(
    modifier: Modifier = Modifier,
    model: CatalogFilterModel
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(screenHeight / 2),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .size(width = 20.dp, height = 1.dp)
                .background(Color.LightGray)
        )

        val pagerState = rememberPagerState()

        ScrollableTabRow(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.White,
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            model.categoriesGroup.forEachIndexed { index, group ->
                Tab(
                    modifier = Modifier.fillMaxWidth(),
                    text = {
                        val included = model.currentKinds.contains(group.id)
                        val color by animateColorAsState(
                            targetValue = if(included) Color.White else Color.Gray
                        )
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            scope.launch {
                                                //model.onGroupClick(group)
                                                pagerState.animateScrollToPage(index)
                                            }
                                        }
                                    )
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = color,
                            onClick = {
                                scope.launch {
                                    model.onGroupClick(group)
                                    //pagerState.animateScrollToPage(index)
                                }
                            }
                        ) {
                            Text(
                                text = group.name,
                                style = MaterialTheme.typography.body1,
                                color = Color.Black,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        //TODO
                    },
                )
            }
        }

        HorizontalPager(
            count = model.categoriesGroup.size,
            state = pagerState,
        ) { page ->
            val group = model.categoriesGroup[page]
            LazyStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                cells = StaggeredCells.Fixed(3)
            ) {
                items(items = group.categories) { category ->
                    val included = model.currentKinds.contains(category.id)
                    val color by animateColorAsState(
                        targetValue = if(included) category.color else category.color.copy(alpha = 0.5f)
                    )

                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(color)
                            .clickable {
                                scope.launch {
                                    model.onCategoryClick(category)
                                }
                            }
                    ) {
                        Text(
                            text = category.name,
                            modifier = Modifier.padding(5.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}