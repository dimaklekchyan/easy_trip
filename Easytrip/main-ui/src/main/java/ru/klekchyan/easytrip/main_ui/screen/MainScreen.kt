package ru.klekchyan.easytrip.main_ui.screen

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.klekchyan.easytrip.base_ui.theme.AppTheme
import ru.klekchyan.easytrip.common.LocationRequester
import ru.klekchyan.easytrip.common.checkLocationPermissions
import ru.klekchyan.easytrip.main_ui.R
import ru.klekchyan.easytrip.main_ui.screen.bottomSheetContent.BottomSheetContent
import ru.klekchyan.easytrip.main_ui.screen.bottomSheetContent.ModalSheetContentType
import ru.klekchyan.easytrip.main_ui.screen.bottomSheetContent.rememberSheetContentHandler
import ru.klekchyan.easytrip.main_ui.vm.MainViewModel
import ru.klekchyan.easytrip.main_ui.vm.MapController

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    vm: MainViewModel
) {
    val searchText by vm.searchQuery.collectAsState(initial = "")
    val detailedPlace = vm.detailedPlaceModel.currentPlace

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val sheetContentHandler = rememberSheetContentHandler(
        modalBottomSheetState = modalSheetState
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        val shouldShowRationale = (context as ComponentActivity)
            .shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)

        if(map.any { it.value }) {
            (context as LocationRequester).requestLocationUpdates()
            vm.mapController.moveToUserLocation()
        } else if(map.size > 1 && !shouldShowRationale) {
            coroutineScope.launch {
                sheetContentHandler.openSheet(ModalSheetContentType.PermissionRationale)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        context.checkLocationPermissions(
            onFineGranted = {
                (context as LocationRequester).requestLocationUpdates()
                vm.mapController.moveToUserLocation()
            },
            onCoarseGranted = {
                (context as LocationRequester).requestLocationUpdates()
                vm.mapController.moveToUserLocation()
            },
            onAllDenied = {}
        )
    }

    BackHandler(enabled = modalSheetState.isVisible) {
        coroutineScope.launch {
            sheetContentHandler.closeSheet()
            vm.detailedPlaceModel.onCloseDetailedPlaceSheet()
            vm.mapController.onClearClickedPlace(isDarkTheme = isDarkTheme, context = context)
        }
    }

    LaunchedEffect(key1 = modalSheetState.currentValue) {
        if(!modalSheetState.isVisible) {
            sheetContentHandler.closeSheet()
            vm.detailedPlaceModel.onCloseDetailedPlaceSheet()
            vm.mapController.onClearClickedPlace(isDarkTheme = isDarkTheme, context = context)
        }
    }

    LaunchedEffect(key1 = detailedPlace) {
        detailedPlace?.let { place ->
            coroutineScope.launch {
                sheetContentHandler.openSheet(ModalSheetContentType.DetailedPlace(vm.detailedPlaceModel))
            }
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            BottomSheetContent(
                handler = sheetContentHandler
            )
        },
        sheetState = modalSheetState,
        scrimColor = Color.Unspecified,
        sheetBackgroundColor = AppTheme.colors.primaryBackground,
        sheetShape = AppTheme.shapes.large.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            ScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                mapController = vm.mapController,
                searchText = searchText,
                onSearchTextChanged = { vm.onSearchQueryChanged(it) },
                onUserLocationClick = {
                    context.checkLocationPermissions(
                        onFineGranted = {
                            (context as LocationRequester).requestLocationUpdates()
                            vm.mapController.moveToUserLocation()
                        },
                        onCoarseGranted = {
                            (context as LocationRequester).requestLocationUpdates()
                            vm.mapController.moveToUserLocation()
                            launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                        },
                        onAllDenied = {
                            launcher.launch(arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ))
                        }
                    )
                },
                onFilterClick = {
                    coroutineScope.launch {
                        sheetContentHandler.openSheet(ModalSheetContentType.Filter(vm.catalogFilterModel))
                    }
                },
                onIncreaseZoomClick = { vm.mapController.increaseZoom() },
                onDecreaseZoomClick = { vm.mapController.decreaseZoom() }
            )
        }
    }
}

@Composable
internal fun ScreenContent(
    modifier: Modifier,
    mapController: MapController,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onUserLocationClick: () -> Unit,
    onFilterClick: () -> Unit,
    onIncreaseZoomClick: () -> Unit,
    onDecreaseZoomClick: () -> Unit,
) {
    Box(modifier = modifier) {
        Map(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
            mapController = mapController
        )

        SearchFieldBlock(
            modifier = Modifier.align(Alignment.TopCenter),
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )

        MapNavigationBlock(
            modifier = Modifier.align(Alignment.CenterEnd),
            onIncreaseZoomClick = onIncreaseZoomClick,
            onDecreaseZoomClick = onDecreaseZoomClick,
            onUserLocationClick = onUserLocationClick
        )

        FilterButton(
            modifier = Modifier
                .padding(bottom = 20.dp, end = 5.dp)
                .align(Alignment.BottomEnd),
            onClick = onFilterClick
        )
    }
}

@Composable
private fun SearchFieldBlock(
    modifier: Modifier = Modifier,
    searchText: String,
    onSearchTextChanged: (String) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AppTheme.colors.secondaryBackground,
        elevation = 4.dp,
        shape = AppTheme.shapes.large.copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp))
    ) {

        BasicTextField(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 30.dp)
                .fillMaxWidth()
                .requiredHeightIn(min = 30.dp),
            value = searchText,
            onValueChange = onSearchTextChanged,
            textStyle = AppTheme.typography.body1.copy(color = AppTheme.colors.primaryTextColor),
            singleLine = true,
            cursorBrush = SolidColor(AppTheme.colors.primaryTextColor),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppTheme.colors.secondaryColor, CircleShape)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(start = 15.dp)
                                .weight(1f, true)
                        ) {
                            innerTextField()
                        }

                        Icon(
                            modifier = Modifier
                                .padding(6.dp),
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            tint = AppTheme.colors.secondaryColor
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun MapNavigationBlock(
    modifier: Modifier = Modifier,
    onIncreaseZoomClick: () -> Unit,
    onDecreaseZoomClick: () -> Unit,
    onUserLocationClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MapNavigationItem(
            modifier = Modifier
                .padding(10.dp)
                .size(40.dp),
            iconId = R.drawable.ic_plus,
            onClick = onIncreaseZoomClick
        )
        MapNavigationItem(
            modifier = Modifier
                .padding(10.dp)
                .size(40.dp),
            iconId = R.drawable.ic_minus,
            onClick = onDecreaseZoomClick
        )
        MapNavigationItem(
            modifier = Modifier
                .padding(10.dp)
                .size(50.dp),
            iconId = R.drawable.ic_to_user_location,
            onClick = onUserLocationClick
        )
    }
}

@Composable
private fun MapNavigationItem(
    modifier: Modifier = Modifier,
    iconId: Int,
    onClick: () -> Unit
) {
    val backgroundColor = AppTheme.colors.primaryBackground

    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            tint = AppTheme.colors.secondaryColor,
            modifier = Modifier
                .drawBehind {
                    drawCircle(
                        color = backgroundColor,
                        radius = (this.size.height / 2f) - 6f,
                        center = this.center
                    )
                }
        )
    }
}

@Composable
private fun FilterButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = AppTheme.colors.primaryBackground

    IconButton(
        modifier = modifier
            .padding(10.dp)
            .size(50.dp),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_filter),
            contentDescription = null,
            tint = AppTheme.colors.secondaryColor,
            modifier = Modifier
                .drawBehind {
                    drawCircle(
                        color = backgroundColor,
                        radius = (this.size.height / 2f) - 6f,
                        center = this.center
                    )
                }
        )
    }
}