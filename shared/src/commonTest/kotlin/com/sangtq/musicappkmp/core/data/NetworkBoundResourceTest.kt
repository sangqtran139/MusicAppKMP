package com.sangtq.musicappkmp.core.data

import app.cash.turbine.test
import com.sangtq.musicappkmp.core.common.AppError
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.common.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NetworkBoundResourceTest {

    @Test
    fun emitsLoadingThenRefreshedValue() = runTest {
        val db = MutableStateFlow<String?>(null)
        networkBoundResource(
            query = { db },
            fetch = { db.value = "fresh"; AppResult.Success(Unit) },
        ).test {
            assertEquals(Resource.Loading(null), awaitItem())
            assertEquals(Resource.Success("fresh"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun keepsCachedValue_whenFetchFails() = runTest {
        val db = MutableStateFlow<String?>("cached")
        networkBoundResource(
            query = { db },
            fetch = { AppResult.Failure(AppError.Network("offline")) },
        ).test {
            assertEquals(Resource.Loading("cached"), awaitItem())
            assertEquals(Resource.Success("cached"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emitsError_whenFetchFailsAndNoCache() = runTest {
        val db = MutableStateFlow<String?>(null)
        val error = AppError.Network("offline")
        networkBoundResource(
            query = { db },
            fetch = { AppResult.Failure(error) },
        ).test {
            assertEquals(Resource.Loading(null), awaitItem())
            assertEquals(Resource.Error(error, null), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun skipsFetch_whenShouldFetchFalse() = runTest {
        val db = MutableStateFlow<String?>("cached")
        networkBoundResource(
            query = { db },
            fetch = { error("must not fetch") },
            shouldFetch = { false },
        ).test {
            assertEquals(Resource.Success("cached"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
