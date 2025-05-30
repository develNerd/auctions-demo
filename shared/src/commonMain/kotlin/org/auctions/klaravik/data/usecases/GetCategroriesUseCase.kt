package org.auctions.klaravik.data.usecases

import org.auctions.klaravik.data.repositories.GetAuctionRepository
import org.auctions.klaravik.view.data.CategoryItem

class GetCategoriesUseCase(private val repository: GetAuctionRepository): FlowBaseUseCase<Unit, List<CategoryItem>>() {
    override suspend fun execute(input: Unit) =  repository.getCategories()
}