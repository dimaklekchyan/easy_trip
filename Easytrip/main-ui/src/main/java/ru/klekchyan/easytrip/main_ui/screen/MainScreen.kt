package ru.klekchyan.easytrip.main_ui.screen

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.klekchyan.easytrip.base_ui.theme.AppTheme
import ru.klekchyan.easytrip.common.LocationRequester
import ru.klekchyan.easytrip.common.checkLocationPermissions
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
        }
    }

    LaunchedEffect(key1 = modalSheetState.currentValue) {
        if(!modalSheetState.isVisible) {
            sheetContentHandler.closeSheet()
            vm.detailedPlaceModel.onCloseDetailedPlaceSheet()
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
        sheetBackgroundColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            sheetContentHandler.openSheet(ModalSheetContentType.Filter(vm.catalogFilterModel))
                        }
                    },
                    content = {
                        Icon(imageVector = Icons.Rounded.Tune, contentDescription = null)
                    }
                )
            }
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
                }
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
    onUserLocationClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Map(
            modifier = Modifier.fillMaxSize(),
            mapController = mapController
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = searchText,
                onValueChange = onSearchTextChanged,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = AppTheme.colors.primaryBackground
                )
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                modifier = Modifier
                    .padding(10.dp)
                    .size(40.dp),
                onClick = onUserLocationClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.NearMe,
                    contentDescription = null,
                    tint = Color.Green
                )
            }
        }
    }
}