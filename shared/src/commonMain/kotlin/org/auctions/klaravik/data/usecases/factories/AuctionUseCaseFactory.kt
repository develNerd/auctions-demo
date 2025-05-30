package org.auctions.klaravik.data.usecases.factories

import org.auctions.klaravik.data.repositories.GetAuctionRepository
import org.auctions.klaravik.data.usecases.GetCategoriesUseCase
import org.auctions.klaravik.data.usecases.GetHomeDataUseCase
import org.auctions.klaravik.data.usecases.GetProductsUseCase
import org.auctions.klaravik.data.usecases.PlaceBidUseCase

class AuctionUseCaseFactory(repository: GetAuctionRepository) {

    // Not in use yet, but will be used in the future. We only need to
    // Make one api call
    val getProductsUseCase = GetProductsUseCase(repository)
    val getCategoriesUseCase = GetCategoriesUseCase(repository)
    val getHomeDataUseCase = GetHomeDataUseCase(repository)


}