package com.sangtq.musicappkmp.feature.search.di

import com.sangtq.musicappkmp.feature.search.presentation.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val searchModule = module {
    viewModelOf(::SearchViewModel)
}
