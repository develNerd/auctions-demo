package org.auctions.klaravik.data.usecases

import org.auctions.klaravik.data.repositories.FlowApiResult
import org.auctions.klaravik.data.repositories.GetAuctionRepository
import org.auctions.klaravik.view.data.CategoryItem
import org.auctions.klaravik.view.data.ProductItem

class GetHomeDataUseCase(private val repository: GetAuctionRepository): FlowBaseUseCase<Unit, Pair<List<CategoryItem>, List<ProductItem>>>() {
    override suspend fun execute(input: Unit) = repository.getCategoriesProducts()
}

