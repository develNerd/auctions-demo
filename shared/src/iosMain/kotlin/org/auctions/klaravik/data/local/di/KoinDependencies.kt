package org.auctions.klaravik.data.local.di

import org.auctions.klaravik.data.usecases.factories.AuctionUseCaseFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KoinDependencies : KoinComponent {
    val authUseCaseFactory: AuctionUseCaseFactory by inject()
}
