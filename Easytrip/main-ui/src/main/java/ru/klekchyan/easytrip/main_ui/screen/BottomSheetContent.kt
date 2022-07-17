package ru.klekchyan.easytrip.main_ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.klekchyan.easytrip.domain.entities.Catalog

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
    class Filter(val catalog: Catalog?, val kinds: List<String>): ModalSheetContentType()
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
   handler: SheetContentHandler,
   onCatalogPositionClick: (id: String) -> Unit
) {
    when (handler.sheetContentType) {
        is ModalSheetContentType.Filter -> {
            val catalog = (handler.sheetContentType as ModalSheetContentType.Filter).catalog
            val kinds = (handler.sheetContentType as ModalSheetContentType.Filter).kinds
            Column(modifier = Modifier.fillMaxWidth()) {
                catalog?.children?.forEach { child ->
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = child.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        )
                        Checkbox(
                            checked = kinds.contains(child.id),
                            onCheckedChange = { onCatalogPositionClick(child.id) }
                        )
                    }
                }
            }
        }
        is ModalSheetContentType.DetailedPlace -> {
            val place = (handler.sheetContentType as ModalSheetContentType.DetailedPlace).place
            Text(text = "Place: ${place.name}")
        }
        is ModalSheetContentType.PermissionRationale -> {
            Text(text = "Rationale")
        }
        is ModalSheetContentType.None -> Box(modifier = Modifier.requiredSize(1.dp))
    }
}