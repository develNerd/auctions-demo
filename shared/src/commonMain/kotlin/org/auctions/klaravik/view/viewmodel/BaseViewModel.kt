package org.auctions.klaravik.view.viewmodel

import com.rickclephas.kmp.observableviewmodel.ViewModel
import kotlinx.coroutines.flow.collectLatest
import org.auctions.klaravik.data.network.ApiResult
import org.auctions.klaravik.data.usecases.BaseUseCase
import org.auctions.klaravik.data.usecases.FlowBaseUseCase

open class BaseViewModel : ViewModel() {

    // This is a base ViewModel class that can be extended by other ViewModels.
    // It can contain common properties or methods that all ViewModels might need.

    suspend fun <Input, Output : Any> executeApiCallUseCase(
        inputValue: Input,
        useCase: FlowBaseUseCase<Input, Output>,
        callback: (Output) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        useCase.execute(inputValue).collectLatest { result ->
            when (result) {
                is ApiResult.Success -> {
                    callback(result.response)
                }

                is ApiResult.GenericError -> {

                    onError(Exception("An error occurred ${result.error}"))
                }

                is ApiResult.HttpError -> {

                    onError(Exception("An error occurred ${result.error}"))
                }

                ApiResult.InProgress -> {
                    // Handle loading state if needed

                }

                ApiResult.NoInternet -> {
                    onError(Exception("No internet connection"))
                }
            }
        }
    }



    suspend fun <Input, Output> executeBasicUseCase(
        inputValue: Input,
        useCase: BaseUseCase<Input, Output>,
        callback: (Output) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val output = useCase.execute(inputValue)
        try {
            callback(output)
        } catch (e: Exception) {
            onError(e)
        }
    }
}
