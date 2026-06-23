package com.sangtq.musicappkmp.catalog.domain.usecase

import com.sangtq.musicappkmp.catalog.domain.model.SearchPage
import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.core.common.AppResult

class SearchCatalogUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(query: String, index: Int = 0): AppResult<SearchPage> =
        repository.search(query, index)
}
