package ru.klekchyan.favorites_ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.entities.SimplePlace
import ru.klekchyan.easytrip.domain.useCases.AddFavoritePlaceUseCase
import ru.klekchyan.easytrip.domain.useCases.DeleteFavoritePlaceUseCase
import ru.klekchyan.easytrip.domain.useCases.GetAllFavoritePlacesUseCase
import javax.inject.Inject

@HiltViewModel
class AllFavoritesScreenViewModel @Inject constructor(
    private val getAllFavoritePlacesUseCase: GetAllFavoritePlacesUseCase,
    private val addFavoritePlaceUseCase: AddFavoritePlaceUseCase,
    private val deleteFavoritePlaceUseCase: DeleteFavoritePlaceUseCase
): ViewModel(){

    var favoritesPlaces by mutableStateOf<List<DetailedPlace>>(emptyList())
        private set

}