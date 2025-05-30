package org.auctions.klaravik.data.repositoryImpl

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.auctions.klaravik.data.model.CategoryItemDTO
import org.auctions.klaravik.data.model.ProductItemDTO
import org.auctions.klaravik.data.network.KlaravikApi
import org.auctions.klaravik.data.network.makeRequestToApiFlow
import org.auctions.klaravik.data.repositories.FlowApiResult
import org.auctions.klaravik.data.repositories.GetAuctionRepository
import org.auctions.klaravik.view.data.CategoryItem
import org.auctions.klaravik.view.data.ProductItem
import kotlin.time.Duration.Companion.seconds

class GetAuctionRepositoryImpl(private val api: KlaravikApi) : GetAuctionRepository {

    private val bidsPlaced = mutableListOf<ProductItem>()

    override suspend fun getProductItems() = makeRequestToApiFlow {

        api.getProducts().map { productItem -> productItem.toProductItem() }
    }


    override suspend fun getCategories() = makeRequestToApiFlow {
        api.getCategories().map { categoryItem -> categoryItem.toCategoryItem() }
    }

    override suspend fun placeBid(productItem: ProductItem): List<ProductItem> {
        bidsPlaced.add(productItem)
        delay(3.seconds)
        return bidsPlaced
    }

    override suspend fun getCategoriesProducts(): FlowApiResult<Pair<List<CategoryItem>, List<ProductItem>>> {
        return makeRequestToApiFlow {
            val products: Deferred<List<ProductItemDTO>> = coroutineScope { async { api.getProducts() } }
            val categories: Deferred<List<CategoryItemDTO>> = coroutineScope { async { api.getCategories() } }
            Pair(categories.await().map { it.toCategoryItem() }, products.await().map { it.toProductItem() })
        }
    }


}