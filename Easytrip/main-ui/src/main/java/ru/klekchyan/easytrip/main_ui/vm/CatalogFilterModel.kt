package ru.klekchyan.easytrip.main_ui.vm

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

data class CategoriesGroup(
    val id: String,
    val name: String,
    val num: String,
    val categories: List<CatalogChild>,
    val isEnabled: Boolean = false
)

class CatalogFilterModel(
    private val scope: CoroutineScope,
    private val mapController: MapController,
    private val getCatalogUseCase: GetCatalogUseCase
) {

    var catalog by mutableStateOf<Catalog?>(null)
        private set
    var currentKinds by mutableStateOf<List<String>>(emptyList())
        private set
    var categoriesGroup by mutableStateOf<List<CategoriesGroup>>(emptyList())
        private set

    init {
        getCatalog()
    }

    fun onCategoryClick(category: CatalogChild) {
        scope.launch(Dispatchers.IO) {
            currentKinds.toMutableList().let { mutableList ->

                val group = categoriesGroup.first { it.num.first() == category.num.first() }

                if(mutableList.contains(category.id)) {
                    mutableList.remove(category.id)
                    mutableList.remove(group.id)
                } else {
                    mutableList.add(category.id)
                    if(mutableList.containsAll(group.categories.map { it.id })) {
                        mutableList.add(group.id)
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
                val subcategoriesIds = categoriesGroup.first { it.num.first() == category.num.first() }.categories.map { it.id }

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
                            categoriesGroup = catalog?.children?.map {
                                CategoriesGroup(
                                    id = it.id,
                                    name = it.name,
                                    num = it.num,
                                    categories = getAllCategories(it.children ?: emptyList())
                                )
                            } ?: emptyList()
                            mapController.onKindsChanged(currentKinds)
                        }
                    }
                }
            }
        }
    }

    private suspend fun getAllCategories(children: List<CatalogChild>): List<CatalogChild> {
        val list = mutableListOf<CatalogChild>()
        withContext(Dispatchers.IO) {
            children.forEach { child ->
                if (child.children.isNullOrEmpty()) {
                    list.add(child)
                } else {
                    list.addAll(getAllCategories(child.children!!))
                }
            }
        }
        return list
    }
}