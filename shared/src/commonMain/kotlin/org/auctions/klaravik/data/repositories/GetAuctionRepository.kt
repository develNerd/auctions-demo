package org.auctions.klaravik.data.repositories

import kotlinx.coroutines.flow.Flow
import org.auctions.klaravik.data.network.ApiResult
import org.auctions.klaravik.view.data.CategoryItem
import org.auctions.klaravik.view.data.ProductItem


typealias FlowApiResult<T> = Flow<ApiResult<T>>

interface GetAuctionRepository {
    /**
     * Fetches the list of auction items.
     *
     * @return A list of auction items.
     */
    suspend fun getProductItems(): FlowApiResult<List<ProductItem>>

    /**
     * Fetches the list of categories.
     *
     * @return A list of CategoryResponseItem.
     */
    suspend fun getCategories(): FlowApiResult<List<CategoryItem>>

    suspend fun placeBid(productItem: ProductItem): List<ProductItem>

    suspend fun getCategoriesProducts(): FlowApiResult<Pair<List<CategoryItem>, List<ProductItem>>>


}