package ru.klekchyan.easytrip.main_ui.screen.bottomSheetContent

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import ru.klekchyan.easytrip.base_ui.theme.AppTheme
import ru.klekchyan.easytrip.common.toHumanDistanceFormat
import ru.klekchyan.easytrip.main_ui.vm.DetailedPlaceModel
import ru.klekchyan.easytrip.base_ui.R
import ru.klekchyan.easytrip.main_ui.R as MainR
import ru.klekchyan.easytrip.domain.entities.DetailedPlace

@Composable
internal fun DetailedPlaceSheetContent(
    modifier: Modifier = Modifier,
    model: DetailedPlaceModel
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PlaceImage(
            imageUrl = model.currentPlace?.imageUrl
        )
        Spacer(modifier = Modifier.width(10.dp))
        DescriptionBlock(
            modifier = Modifier.weight(1f, true),
            currentPlace = model.currentPlace,
            onAddToFavoriteClick = { model.addToFavorite() },
            onDeleteFromFavoriteClick = { model.deleteFromFavorite() }
        )
    }
}

@Composable
private fun PlaceImage(
    modifier: Modifier = Modifier,
    imageUrl: String?
) {
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(imageUrl)
        .crossfade(true)
        .build()

    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(100.dp)
            .defaultMinSize(minWidth = 100.dp, minHeight = 100.dp)
            .clip(AppTheme.shapes.medium),
        imageLoader = LocalContext.current.imageLoader,
        error = {
            Icon(
                imageVector = Icons.Rounded.Image,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp),
                tint = AppTheme.colors.secondaryColor
            )
        },
        loading = {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(2.dp)
                    .size(10.dp),
                color = AppTheme.colors.secondaryColor
            )
        }
    )
}

@Composable
private fun DescriptionBlock(
    modifier: Modifier = Modifier,
    currentPlace: DetailedPlace?,
    onAddToFavoriteClick: () -> Unit,
    onDeleteFromFavoriteClick: () -> Unit,
) {

    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        val noName = context.resources.getString(R.string.no_name)
        val placeName = currentPlace?.name?.ifEmpty { noName } ?: noName
        Text(
            text =  placeName,
            style = AppTheme.typography.h6,
            color = AppTheme.colors.primaryTextColor
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "${currentPlace?.description}",
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            style = AppTheme.typography.body1,
            color = AppTheme.colors.primaryTextColor
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if(currentPlace?.distanceToUser != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = MainR.drawable.ic_distance),
                        contentDescription = null,
                        tint = AppTheme.colors.primaryTextColor
                    )
                    Text(
                        text = currentPlace.distanceToUser!!.toHumanDistanceFormat(context),
                        style = AppTheme.typography.body1,
                        color = AppTheme.colors.primaryTextColor
                    )
                }
            }
            IconButton(
                modifier = Modifier,
                onClick = {
                    if(currentPlace?.isFavorite == true) {
                        onDeleteFromFavoriteClick()
                    } else {
                        onAddToFavoriteClick()
                    }
                },
                content = {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = if(currentPlace?.isFavorite == true) Color.Red else Color.LightGray
                    )
                }
            )
        }
    }
}