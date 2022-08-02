package ru.klekchyan.easytrip.main_ui.screen.bottomSheetContent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.klekchyan.easytrip.main_ui.vm.CatalogFilterModel
import ru.klekchyan.easytrip.main_ui.vm.DetailedPlaceModel

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
    class DetailedPlace(val model: DetailedPlaceModel): ModalSheetContentType()
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
            DetailedPlaceSheetContent(model = type.model)
        }
        is ModalSheetContentType.PermissionRationale -> {
//            context.startActivity(
//                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                    data = Uri.fromParts("package", context.packageName, null)
//                }
//            )
        }
        is ModalSheetContentType.None -> Box(modifier = Modifier.requiredSize(1.dp))
        else -> { /*TODO*/ }
    }
}