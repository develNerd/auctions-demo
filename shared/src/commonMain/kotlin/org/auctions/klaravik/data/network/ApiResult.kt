package org.auctions.klaravik.data.network

import co.touchlab.kermit.Logger
import io.ktor.client.plugins.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.IOException
import kotlinx.serialization.SerialName

// --- Generic response wrapper for serialized error messages (optional)
data class ReqData<T>(
    @SerialName("error")
    val error: T?,
)

typealias ErrorResponse = ReqData<String>

// --- Core result wrapper
sealed class ApiResult<out T : Any> {

    data class Success<out T : Any>(val response: T) : ApiResult<T>()

    data class GenericError(val error: Exception) : ApiResult<Nothing>()

    data class HttpError(val error: ClientRequestException) : ApiResult<Nothing>()

    object InProgress : ApiResult<Nothing>()

    object NoInternet : ApiResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success [data=$response]"
            is HttpError -> "Http Error [httpCode=${error.message}]"
            is GenericError -> "Error [error=${error.message}]"
            is NoInternet -> "No Internet"
            is InProgress -> "In progress"
        }
    }
}

// --- Network wrapper function
fun <T : Any> makeRequestToApiFlow(
    call: suspend () -> T
): Flow<ApiResult<T>> = flow {
    emit(ApiResult.InProgress)
    try {
        val response = call()
        emit(ApiResult.Success(response))
    } catch (throwable: Exception) {
        throwable.printStackTrace()
        val errorResult = when (throwable) {
            is ServerResponseException -> ApiResult.GenericError(throwable)
            is ClientRequestException -> ApiResult.HttpError(throwable)
            is IOException -> {
                ApiResult.NoInternet
            }
            else -> ApiResult.GenericError(throwable)
        }
        emit(errorResult)
    }
}

val <T : Any> ApiResult<T>.dataOrNull: T?
    get() = (this as? ApiResult.Success)?.response

val <T : Any> ApiResult<T>.errorOrNull: Exception?
    get() = when (this) {
        is ApiResult.GenericError -> this.error
        is ApiResult.HttpError -> this.error
        ApiResult.NoInternet -> IOException("We are finding it difficult to connect, Please Try Again")
        else -> null
    }

val <T : Any> ApiResult<T>.isLoaded: Boolean
    get() = this !is ApiResult.InProgress

val <T : Any> ApiResult<T>.isSuccess: Boolean
    get() = this is ApiResult.Success

val <T : Any> ApiResult<T>.isError: Boolean
    get() = this is ApiResult.GenericError || this is ApiResult.HttpError || this is ApiResult.NoInternet
