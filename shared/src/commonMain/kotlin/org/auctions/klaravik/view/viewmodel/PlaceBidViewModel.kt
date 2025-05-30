package org.auctions.klaravik.view.viewmodel

import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.auctions.klaravik.data.usecases.factories.BidUseCaseFactory
import org.auctions.klaravik.view.data.ProductItem

class PlaceBidViewModel(private val bidUseCaseFactory: BidUseCaseFactory) : BaseViewModel() {


    sealed class BidUIState {
        object Loading : BidUIState()
        class Success(private val productItem: ProductItem) : BidUIState()
        class Error(val message: String) : BidUIState()
    }


    private val _uiStatus = MutableStateFlow<BidUIState?>(null)

    @NativeCoroutinesState
    val uiState: StateFlow<BidUIState?> = _uiStatus.asStateFlow()

    fun resetUIState() {
        _uiStatus.value = null
    }

    fun placeBid(productItem: ProductItem, amount: Long) {

        viewModelScope.launch {
            _uiStatus.value = BidUIState.Loading
            executeBasicUseCase(productItem, bidUseCaseFactory.placeBidUseCase, callback = { result ->
                // Handle the result of placing a bid
                Logger.d { "Bid placed successfully for product: ${productItem.id} with amount: $amount" }
                _uiStatus.value = BidUIState.Success(productItem)
            }, onError = {
                _uiStatus.value =
                    BidUIState.Error("Error placing bid for product: ${productItem.name} with amount: $amount")
            })
        }
    }

}