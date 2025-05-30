package org.auctions.klaravik.data.usecases

import org.auctions.klaravik.data.repositories.GetAuctionRepository
import org.auctions.klaravik.view.data.ProductItem

class GetProductsUseCase(private val repository: GetAuctionRepository): FlowBaseUseCase<Unit, List<ProductItem>>() {
    override suspend fun execute(input: Unit) = repository.getProductItems()
}