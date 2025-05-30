package org.auctions.klaravik.data.network

import org.auctions.klaravik.data.model.CategoryItemDTO
import org.auctions.klaravik.data.model.ProductItemDTO

class KlaravikApi(private val httpClient: KtorHttpClient)  {

     suspend fun getCategories() = httpClient.GET<List<CategoryItemDTO>>("dev-test-api/categories")

     suspend fun getProducts() = httpClient.GET<List<ProductItemDTO>>("dev-test-api/products")
}




