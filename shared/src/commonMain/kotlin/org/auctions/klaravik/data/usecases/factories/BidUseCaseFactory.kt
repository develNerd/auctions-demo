package org.auctions.klaravik.data.usecases.factories

import org.auctions.klaravik.data.repositories.GetAuctionRepository
import org.auctions.klaravik.data.usecases.PlaceBidUseCase


// Could use a diff repository for bids, but for now we use the same one as auctions
class BidUseCaseFactory (repository: GetAuctionRepository) {
    val placeBidUseCase = PlaceBidUseCase(repository)
}