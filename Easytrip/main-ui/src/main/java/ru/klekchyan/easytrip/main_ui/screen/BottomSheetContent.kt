package ru.klekchyan.easytrip.main_ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Maximize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.main_ui.R
import ru.klekchyan.easytrip.main_ui.vm.CatalogFilterModel

@OptIn(ExperimentalMaterialApi::class)
internal class SheetContentHandler(
    private val modalBottomSheetState: ModalBottomSheetState
) {
    var sheetContentType by mutableStateOf<ModalSheetContentType>(ModalSheetContentType.None)
        private set

    suspend fun openSheet(contentType: ModalSheetContentType) {
        if(contentType is ModalSheetContentType.None) {
            closeSheet()
        } else {
            sheetContentType = contentType
            modalBottomSheetState.show()
        }
    }

    suspend fun closeSheet() {
        modalBottomSheetState.hide()
        sheetContentType = ModalSheetContentType.None
    }
}

internal sealed class ModalSheetContentType {
    object None: ModalSheetContentType()
    class Filter(val model: CatalogFilterModel): ModalSheetContentType()
    class DetailedPlace(
        val place: ru.klekchyan.easytrip.domain.entities.DetailedPlace
        ): ModalSheetContentType()
    object PermissionRationale: ModalSheetContentType()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun rememberSheetContentHandler(
    modalBottomSheetState: ModalBottomSheetState
): SheetContentHandler = remember {
    SheetContentHandler(modalBottomSheetState)
}

@Composable
internal fun BottomSheetContent(
   handler: SheetContentHandler
) {
    when (val type = handler.sheetContentType) {
        is ModalSheetContentType.Filter -> {
            CatalogFilterSheetContent(model = type.model)
        }
        is ModalSheetContentType.DetailedPlace -> {
            DetailedPlaceSheetContent(place = type.place)
        }
        is ModalSheetContentType.PermissionRationale -> {
//            context.startActivity(
//                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                    data = Uri.fromParts("package", context.packageName, null)
//                }
//            )
        }
        is ModalSheetContentType.None -> Box(modifier = Modifier.requiredSize(1.dp))
    }
}

@Composable
private fun DetailedPlaceSheetContent(
    modifier: Modifier = Modifier,
    place: DetailedPlace
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        val imageRequest = ImageRequest.Builder(LocalContext.current)
            .data(place.previewUrl)
            .crossfade(true)
            .build()

        SubcomposeAsyncImage(
            model = imageRequest,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .defaultMinSize(minWidth = 100.dp, minHeight = 100.dp)
                .clip(RoundedCornerShape(12.dp)),
            imageLoader = LocalContext.current.imageLoader,
            error = {
                Icon(
                    painter = painterResource(id = R.drawable.search_result),
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = Color.Blue
                )
            },
            loading = {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(25.dp),
                    color = Color.LightGray
                )
            }
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
        ) {
            Text(
                text = place.name,
                maxLines = 1,
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = place.description,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CatalogFilterSheetContent(
    modifier: Modifier = Modifier,
    model: CatalogFilterModel
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeightIn(max = screenHeight / 2)
    ) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Icon(
                imageVector = Icons.Rounded.Maximize,
                contentDescription = "",
                tint = LightGray,
                modifier = Modifier.requiredSize(40.dp)
            )
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(model.catalog?.children ?: emptyList()) { category ->
                val included = model.currentKinds.contains(category.id)
                val color by animateColorAsState(
                    targetValue = if(included) Color.White else Color.Gray
                )
                Surface(
                    modifier = Modifier.padding(5.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = color,
                    onClick = { model.onTopCategoryClick(category = category) }
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.body1,
                        color = Color.Black,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(items = model.categoriesGroup, key = { it.id }) { group ->
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = 5.dp
                ) {
                    group.categories.forEach { category ->

                        val included = model.currentKinds.contains(category.id)
                        val color by animateColorAsState(
                            targetValue = if(included) category.color else category.color.copy(alpha = 0.5f)
                        )

                        Surface(
                            modifier = Modifier,
                            shape = RoundedCornerShape(8.dp),
                            color = color,
                            onClick = { model.onCategoryClick(category) }
                        ) {
                            Text(
                                text = category.name,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}