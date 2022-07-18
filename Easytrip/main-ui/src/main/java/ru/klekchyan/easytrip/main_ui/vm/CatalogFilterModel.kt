package ru.klekchyan.easytrip.main_ui.vm

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.klekchyan.easytrip.domain.entities.Catalog
import ru.klekchyan.easytrip.domain.entities.CatalogChild
import ru.klekchyan.easytrip.domain.useCases.GetCatalogUseCase

class CatalogFilterModel(
    private val scope: CoroutineScope,
    private val mapController: MapController,
    private val getCatalogUseCase: GetCatalogUseCase
) {

    var catalog by mutableStateOf<Catalog?>(null)
        private set
    var currentKinds by mutableStateOf<List<String>>(emptyList())
        private set
    var topCategories by mutableStateOf<List<CatalogChild>>(emptyList())
        private set
    var allCategories by mutableStateOf<List<CatalogChild>>(emptyList())
        private set

    init {
        getCatalog()
    }

    fun onCategoryClick(category: CatalogChild) {
        scope.launch(Dispatchers.IO) {
            currentKinds.toMutableList().let { mutableList ->

                val topCategoryId = topCategories.first { it.num.first() == category.num.first() }.id
                val relatedSubcategoriesIds = allCategories.filter { it.num.first() == category.num.first() }.map { it.id }

                if(mutableList.contains(category.id)) {
                    mutableList.remove(category.id)
                    mutableList.remove(topCategoryId)
                } else {
                    mutableList.add(category.id)
                    if(mutableList.containsAll(relatedSubcategoriesIds)) {
                        mutableList.add(topCategoryId)
                    }
                }
                withContext(Dispatchers.Main) {
                    currentKinds = mutableList
                }
            }
            mapController.onKindsChanged(currentKinds)
        }
    }

    fun onTopCategoryClick(category: CatalogChild) {
        scope.launch(Dispatchers.IO) {
            currentKinds.toMutableList().let { mutableList ->
                val subcategoriesIds = allCategories.filter { it.num.first() == category.num.first() }.map { it.id }

                if(mutableList.contains(category.id)) {
                    mutableList.remove(category.id)
                    mutableList.removeAll(subcategoriesIds)
                } else {
                    mutableList.add(category.id)
                    mutableList.addAll(subcategoriesIds)
                }
                withContext(Dispatchers.Main) {
                    currentKinds = mutableList
                }
            }
            mapController.onKindsChanged(currentKinds)
        }
    }

    private fun getCatalog() {
        scope.launch(Dispatchers.IO) {
            getCatalogUseCase().collect { state ->
                withContext(Dispatchers.Main) {
                    when(state) {
                        is GetCatalogUseCase.State.Loading -> {}
                        is GetCatalogUseCase.State.Error -> {}
                        is GetCatalogUseCase.State.Success -> {
                            catalog = state.catalog
                            topCategories = catalog?.children ?: emptyList()
                            //TODO fixed that
                            topCategories.forEach {
                                onTopCategoryClick(it)
                            }
                            getAllCategories(catalog?.children ?: emptyList())
                            mapController.onKindsChanged(currentKinds)
                        }
                    }
                }
            }
        }
    }

    private suspend fun getAllCategories(children: List<CatalogChild>) {
        withContext(Dispatchers.IO) {
            children.forEach { child ->
                if (child.children.isNullOrEmpty()) {
                    allCategories.toMutableList().let { mutableList ->
                        mutableList.add(child)
                        withContext(Dispatchers.Main) {
                            allCategories = mutableList
                        }
                    }
                } else {
                    getAllCategories(child.children!!)
                }
            }
        }
    }
}