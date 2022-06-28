package ru.klekchyan.easytrip.main_ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.klekchyan.easytrip.domain.entities.CatalogChild
import ru.klekchyan.easytrip.domain.repositories.MainRepository
import ru.klekchyan.easytrip.domain.useCases.GetCatalogUseCase
import ru.klekchyan.easytrip.domain.useCases.GetDetailedPlaceUseCase
import ru.klekchyan.easytrip.domain.useCases.GetPlacesByRadiusAndNameUseCase
import ru.klekchyan.easytrip.domain.useCases.GetPlacesByRadiusUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    mainRepository: MainRepository
): ViewModel() {

    private val getPlacesByRadiusUseCase = GetPlacesByRadiusUseCase(mainRepository)
    private val getPlacesByRadiusAndNameUseCase = GetPlacesByRadiusAndNameUseCase(mainRepository)
    private val getDetailedPlaceUseCase = GetDetailedPlaceUseCase(mainRepository)
    private val getCatalogUseCase = GetCatalogUseCase(mainRepository)

    val mapController = MapController(
        scope = viewModelScope,
        getPlacesByRadiusUseCase = getPlacesByRadiusUseCase,
        getPlacesByRadiusAndNameUseCase = getPlacesByRadiusAndNameUseCase,
        getDetailedPlaceUseCase = getDetailedPlaceUseCase
    )

    val categories = mutableListOf<String>()

    init {
        getCatalog()
    }

    fun getCatalog() {
        viewModelScope.launch(Dispatchers.IO) {
            getCatalogUseCase().collect { state ->
                when(state) {
                    is GetCatalogUseCase.State.Loading -> {}
                    is GetCatalogUseCase.State.Error -> {}
                    is GetCatalogUseCase.State.Success -> {
                        getAllFinishedCategories(state.catalog.children)
//                        categories.forEach {
//                            Log.d("TAG2", it)
//                        }
                        //Log.d("TAG2", "${categories.size}")
                    }
                }
            }
        }
    }

    private fun getAllFinishedCategories(children: List<CatalogChild>) {
        children.forEach {
            if (it.children.isNullOrEmpty()) {
                //Log.d("TAG2", "Add ${it.id}")
                categories.add(it.id)
            } else {
                //Log.d("TAG2", "Go deeper ${it.id}")
                getAllFinishedCategories(it.children!!)
            }
        }
    }
}