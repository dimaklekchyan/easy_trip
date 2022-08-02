package ru.klekchyan.easytrip.main_ui.screen.bottomSheetContent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
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
import ru.klekchyan.easytrip.common.toHumanDistanceFormat
import ru.klekchyan.easytrip.main_ui.R
import ru.klekchyan.easytrip.main_ui.vm.DetailedPlaceModel

@Composable
internal fun DetailedPlaceSheetContent(
    modifier: Modifier = Modifier,
    model: DetailedPlaceModel
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val imageRequest = ImageRequest.Builder(LocalContext.current)
            .data(model.currentPlace?.previewUrl)
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
            modifier = Modifier.weight(1f, true)
        ) {
            Text(
                text = "${model.currentPlace?.name}",
                maxLines = 1,
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "${model.currentPlace?.description}",
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1
            )
            if(model.currentPlace?.distanceToUser != null) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = model.currentPlace!!.distanceToUser!!.toHumanDistanceFormat(),
                    style = MaterialTheme.typography.body1
                )
            }
        }
        IconButton(
            modifier = Modifier,
            onClick = {
                if(model.currentPlace?.isFavorite == true) {
                    model.deleteFromFavorite()
                } else {
                    model.addToFavorite()
                }
            },
            content = {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = if(model.currentPlace?.isFavorite == true) Color.Red else Color.LightGray
                )
            }
        )
    }
}