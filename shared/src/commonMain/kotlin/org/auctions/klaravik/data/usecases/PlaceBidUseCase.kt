package org.auctions.klaravik.data.usecases

import org.auctions.klaravik.data.repositories.GetAuctionRepository
import org.auctions.klaravik.view.data.ProductItem

class PlaceBidUseCase(private val repository: GetAuctionRepository): BaseUseCase<ProductItem, List<ProductItem>>() {
    override suspend fun execute(input: ProductItem): List<ProductItem> {
        return repository.placeBid(input)
    }

}