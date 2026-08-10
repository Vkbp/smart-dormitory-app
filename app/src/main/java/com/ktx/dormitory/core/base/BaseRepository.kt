package com.ktx.dormitory.core.base

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import com.ktx.dormitory.core.dispatcher.CoroutineDispatchers

/**
 * Base Repository providing common data handling methods.
 */
abstract class BaseRepository(
    private val dispatchers: CoroutineDispatchers
) {
    /**
     * Executes a network call and returns a Result.
     */
    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Result<T> = withContext(dispatchers.io) {
        try {
            Result.success(apiCall())
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            android.util.Log.e("BASE_REPO", "API Call Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Converts a network/db call into a Flow of Result.
     */
    protected fun <T> resultFlow(
        call: suspend () -> T
    ): Flow<Result<T>> = flow {
        emit(Result.success(call()))
    }
}
