package org.auctions.klaravik.navigation

import androidx.compose.runtime.collectAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.auctions.klaravik.screens.AuctionScreen
import org.auctions.klaravik.screens.detail.CategoryDetailScreen
import org.auctions.klaravik.screens.detail.ProductDetailScreen
import org.auctions.klaravik.view.viewmodel.AuctionsHomeViewModel

const val AUCTION_ROUTE = "auction"
const val PRODUCT_DETAIL_ROUTE = "product/{productId}"
const val CATEGORY_DETAIL_ROUTE = "category/{categoryId}"

fun NavGraphBuilder.auctionNavGraph(navController: NavHostController,auctionsHomeViewModel: AuctionsHomeViewModel) {
    composable(AUCTION_ROUTE) {
        AuctionScreen (
            uiState = auctionsHomeViewModel.uiState.collectAsState().value ?: AuctionsHomeViewModel.UIState.Loading,
            onProductClick = { product ->
                auctionsHomeViewModel.selectProduct(product)
                navController.navigate("product/${product.id}")
            },
            onCategoryClick = { category ->
                auctionsHomeViewModel.selectCategory(category)
                navController.navigate("category/${category.id}")
            }
        )
    }
    
    composable(PRODUCT_DETAIL_ROUTE) { backStackEntry ->
        val product = auctionsHomeViewModel.selectedProduct.collectAsState().value
        ProductDetailScreen(product = product, onBackClick = { navController.popBackStack() })
    }
    
    composable(CATEGORY_DETAIL_ROUTE) { backStackEntry ->
        // Here we passed the auctionsHomeViewModel to CategoryDetailScreen, However
        // Preview does not really support ViewModels. So passing data class directly could be a better option.
        // Then again, if the CategoryDetailScreen has to many functionalities, it might be better to keep the ViewModel.
        CategoryDetailScreen(viewModel = auctionsHomeViewModel, onBackClick = { navController.popBackStack() })
    }
}