package org.auctions.klaravik

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.auctions.klaravik.navigation.auctionNavGraph
import org.auctions.klaravik.navigation.AUCTION_ROUTE
import org.auctions.klaravik.view.viewmodel.AuctionsHomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val auctionsHomeViewModel: AuctionsHomeViewModel = koinViewModel()
        NavHost(
            navController = navController,
            startDestination = AUCTION_ROUTE
        ) {

            auctionNavGraph(navController,auctionsHomeViewModel)
        }
    }
}