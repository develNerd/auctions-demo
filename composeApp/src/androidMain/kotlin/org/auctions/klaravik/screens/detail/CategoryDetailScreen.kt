package org.auctions.klaravik.screens.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.auctions.klaravik.R
import org.auctions.klaravik.view.data.ProductItem
import org.auctions.klaravik.view.viewmodel.AuctionsHomeViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.lazy.grid.items as gridItems

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    onBackClick: () -> Unit = {},
    onProductClick: (ProductItem) -> Unit = {},
    viewModel: AuctionsHomeViewModel = koinViewModel() // Added default koinViewModel
) {
    val category = viewModel.selectedCategory.collectAsState().value
    val products by viewModel.filteredProducts.collectAsState()

    LaunchedEffect(category) {
        // Ensure the view model has the latest category data
        category?.let { viewModel.getProductsByCategory(it.id) }
        viewModel.filterCategories(category?.children)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category?.headline ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = "https://picsum.photos/seed/category3/300/200",
                contentDescription = category?.headline,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = category?.headline ?: stringResource(R.string.unknown_category), // Added default text
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            var selectedId by mutableIntStateOf(0)

            val categories by viewModel.filteredCategories.collectAsState()

            LazyRow {
                items(categories) { childCategory ->
                    FilterChip(
                        selected = selectedId == childCategory.id,
                        onClick = {
                            viewModel.getProductsByCategory(childCategory.id)
                            selectedId = childCategory.id
                        },
                        label = { Text(childCategory.headline) },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }


            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Defines a 2-column grid
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp) // Apply padding to the grid content
            ) {

                if (products.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) { // maxLineSpan makes it span all columns
                        Text(
                            text = stringResource(R.string.no_products_available),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }


                }

                gridItems(products) { product ->
                    ProductGridItem(
                        product = product,
                        onClick = { onProductClick(product) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductGridItem(
    product: ProductItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = product.image?.thumbUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name ?: "-",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2, // Allow up to 2 lines for name
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Current Bid: ${product.currentBid}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}