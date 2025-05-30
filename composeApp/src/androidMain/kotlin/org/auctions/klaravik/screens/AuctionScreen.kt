package org.auctions.klaravik.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // Changed from Column to LazyColumn for overall scrollability
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward // For stringResource(R.string.view_all)
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.valentinilk.shimmer.shimmer
import org.auctions.klaravik.view.data.CategoryItem
import org.auctions.klaravik.view.data.CategoryTreeResult
import org.auctions.klaravik.view.data.ProductItem
import org.auctions.klaravik.view.viewmodel.AuctionsHomeViewModel
import org.auctions.klaravik.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuctionScreen(
    uiState : AuctionsHomeViewModel.UIState,
    onCategoryClick: (CategoryItem) -> Unit = {},
    onProductClick: (ProductItem) -> Unit = {}
) {


    val uIStatus by rememberUpdatedState(newValue = uiState)
    var categories by remember { mutableStateOf<CategoryTreeResult?>(null) }
    var products by remember { mutableStateOf<List<ProductItem>?>(null) }

    LaunchedEffect(uIStatus) {
        when (uiState) {
            is AuctionsHomeViewModel.UIState.Success -> {
                categories = uiState.categories
                products = uiState.products
            }
            AuctionsHomeViewModel.UIState.Loading -> {
                // Handle loading state if needed
            }
            is AuctionsHomeViewModel.UIState.Error -> {
                // Handle error state if needed
            }
        }
    }




    Scaffold(
        topBar = {
            Column {
                SearchBar()
            }
        }
    ) { paddingValues ->


        Spacer(modifier = Modifier.padding(top = paddingValues.calculateTopPadding()))

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF4F4F4)) // A slightly off-white background
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) } // Reduced spacer

            // We could recommend items that are not in the auction or will end soon
            item {
                RecommendedSection(
                    items = products?.takeLast(20),
                    onProductClick = onProductClick
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) } // Reduced spacer

            item {
                AuctionSection(
                    items = products ?: emptyList(), // Ensure we have a list to display
                    onProductClick = onProductClick
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) } // Reduced spacer

            item {
                CategoriesSection(
                    items = categories?.roots ?: emptyList(),
                    onCategoryClick = { category ->
                        onCategoryClick(category)
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) } // Space at the very bottom
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") }
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color.DarkGray)) {
        Column {
            Spacer(modifier = Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
            TextField( // Changed to TextField for a simpler look like the image
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text(stringResource(R.string.search_placeholder)) }, // Using placeholder
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon", tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp) // Adjusted padding
                    .background(Color.White, RoundedCornerShape(50)) // Fully rounded
                    .height(50.dp), // Fixed height
                shape = RoundedCornerShape(50), // Fully rounded
                colors = TextFieldDefaults.colors( // Using new colors API for TextField
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent, // No indicator line
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true
            )
        }

    }

}

@Composable
fun SectionHeader(title: String, onViewAllClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 4.dp), // Adjusted padding
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), // Slightly smaller title
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onViewAllClicked) {
            Text(stringResource(R.string.view_all), color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.view_all),
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun RecommendedSection(
    items: List<ProductItem>?,
    onProductClick: (ProductItem) -> Unit = {}
) {

    Column {
        SectionHeader(title = stringResource(R.string.recommended_for_you)) { /* TODO: Handle View All */ }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (items == null) {
                // Handle the case where there are no items to display
                items(3) {
                    ShimmerBox(
                        modifier = Modifier
                            .size(width = 160.dp, height = 200.dp)
                            .shimmer()
                    )
                }
            } else {
                // Display the items if they are available
                items(items) { item ->
                    RecommendedItemCard(
                        item = item,
                        onClick = { onProductClick(item) }
                    )
                }
            }

        }
    }
}

@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(200.dp) // Adjusted height
        , // Slightly adjusted width
        shape = RoundedCornerShape(12.dp), // Softer corners
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Subtle shadow
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = { /* TODO: Handle item click */ }
    ) {

    }
}

@Composable
fun RecommendedItemCard(
    item: ProductItem,
    onClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(item.isFavorite) }
    Card(
        modifier = Modifier
            .width(150.dp), // Slightly adjusted width
        shape = RoundedCornerShape(12.dp), // Softer corners
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Subtle shadow
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(item.image?.thumbUrl ?: "https://picsum.photos/seed/default/300/200")
                            .crossfade(true) // Smooth transition
                            .build()
                    ),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp) // Adjusted padding
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .size(32.dp) // Explicit size
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(18.dp) // Icon size
                    )
                }
            }
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)) {
                Text(
                    text = item.name ?: stringResource(R.string.unknown_item),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1, // Ensure it fits well
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp // Slightly smaller
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.currentBid}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.getAuctionCountdownString(), // Fallback text
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontSize = 11.sp // Consistent small font size
                    )
                }
            }
        }
    }
}

@Composable
fun AuctionSection(
    items: List<ProductItem>,
    onProductClick: (ProductItem) -> Unit
) {
    Column {
        SectionHeader(title = stringResource(R.string.now_in_auction)) { /* TODO: Handle View All */ }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                AuctionItemCard(
                    item = item,
                    onClick = {
                        onProductClick(item) }
                )
            }
        }
    }
}

@Composable
fun AuctionItemCard(
    item: ProductItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(280.dp), // Adjusted width
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp) // Main image area
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(item.image?.thumbUrl ?: "https://picsum.photos/seed/default/300/200") // Fallback URL
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )

                // Overlay for secondary images

            }

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = item.name ?: stringResource(R.string.unknown_item), // Fallback text
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Time left",
                        tint = MaterialTheme.colorScheme.primary, // Use primary color for emphasis
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.getAuctionCountdownString(), // Label "Closes in" is in the string from dummy data
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary, // Use primary color
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriesSection(
    items: List<CategoryItem>,
    onCategoryClick: (CategoryItem) -> Unit
) {
    Column {
        SectionHeader(title = stringResource(R.string.categories)) { /* TODO: Handle View All */ }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                CategoryItemCard(
                    item = item,
                    onClick = { onCategoryClick(item) }
                )
            }
        }
    }
}

@Composable
fun CategoryItemCard(
    item: CategoryItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp) // Adjusted width
            .height(70.dp), // Adjusted height
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) { // Center text
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data("https://picsum.photos/seed/default/300/200") // Fallback URL
                        .crossfade(true)
                        .build()
                ),
                contentDescription = item.headline,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Scrim for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)) // Slightly darker scrim
            )
            Text(
                text = item.headline,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp), // Padding for text
                textAlign = TextAlign.Center,
                maxLines = 2, // Allow for two lines if needed
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=380dp,height=800dp,dpi=440")
@Composable
fun DefaultPreview() {
    MaterialTheme {
        // Preview the AuctionScreen with dummy data
        val dummyCategories = CategoryTreeResult(
            roots = listOf(
                CategoryItem(
                    id = 1, headline = "Cars",
                    level = 1,
                    parentId = 1,
                    children = mutableListOf()
                ),
                CategoryItem(
                    id = 1, headline = "Bikes",
                    level = 1,
                    parentId = 1,
                    children = mutableListOf()
                ),
                CategoryItem(
                    id = 1, headline = "Electronics",
                    level = 1,
                    parentId = 1,
                    children = mutableListOf()
                )
            ),
            idMap = mapOf(
                1 to CategoryItem(
                    id = 1, headline = "Cars",
                    level = 1,
                    parentId = 1,
                    children = mutableListOf()
                ),
                2 to CategoryItem(
                    id = 1, headline = "Cars",
                    level = 1,
                    parentId = 1,
                    children = mutableListOf()
                ),
                3 to CategoryItem(
                    id = 1, headline = "Cars",
                    level = 1,
                    parentId = 1,
                    children = mutableListOf()
                )
            )
        )
        val uiState = AuctionsHomeViewModel.UIState.Success(categories = dummyCategories, products = listOf(
            ProductItem(
                id = 1,
                name = "Car 1",
                currentBid = 1000,
                image = null,
                isFavorite = false,
                categoryLevel1 = 1,
                categoryLevel2 = null,
                categoryLevel3 = null
            ),
            ProductItem(
                id = 2,
                name = "Bike 1",
                currentBid = 333,
                image = null,
                isFavorite = true,
                categoryLevel1 = 2,
                categoryLevel2 = null,
                categoryLevel3 = null
            )
        ))
        AuctionScreen(uiState = uiState)
    }
}