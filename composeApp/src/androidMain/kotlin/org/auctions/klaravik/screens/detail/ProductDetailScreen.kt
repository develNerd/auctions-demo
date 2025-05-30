package org.auctions.klaravik.screens.detail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.auctions.klaravik.view.data.ProductItem
import org.auctions.klaravik.view.viewmodel.PlaceBidViewModel
import org.koin.androidx.compose.koinViewModel
import org.auctions.klaravik.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: ProductItem?,
    placeBidViewModel: PlaceBidViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    ) {

    // State to control the visibility of the bid bottom sheet
    var showBidBottomSheet by remember { mutableStateOf(false) }

    var showSuccessModal by remember { mutableStateOf(false) }


    // State for the bid input text field
    var bidAmountInput by remember { mutableStateOf("") }

    // Bottom sheet state for controlling its behavior
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // Makes the sheet either full-height or hidden
    )
    val scope = rememberCoroutineScope() // Coroutine scope for sheet actions

    val uiState by placeBidViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: stringResource(R.string.product_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState()) // Allow content to scroll
        )
        {
            AsyncImage(
                model = product?.image?.largeUrl,
                contentDescription = product?.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) { // Apply horizontal padding once
                Text(
                    text = product?.name ?: "-",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Current bid: ${product?.currentBid ?: "-"}",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = product?.description ?: stringResource(R.string.no_description),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = product?.getAuctionCountdownString() ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                    // Use diff color for countdown if it gets close or there's more than 24 hours left etc
                )

                Spacer(modifier = Modifier.height(32.dp)) // Add space before the bid button

                // --- Place Bid Button ---
                Button(
                    onClick = {
                        showBidBottomSheet = true
                        bidAmountInput = "" // Reset bid input when opening sheet
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.place_bid))
                }
            }
        }

        if (showSuccessModal){
            Dialog(onDismissRequest = {
                showSuccessModal = false // Dismiss the dialog when tapped outside
                placeBidViewModel.resetUIState()
            }) {
                // Bid Confirmation Dialog
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp)
                        .navigationBarsPadding(), // Handle system bars for bottom padding
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (uiState) {
                        is PlaceBidViewModel.BidUIState.Success -> {
                            Text(stringResource(R.string.bid_success))
                        }

                        else  -> {
                            Text(stringResource(R.string.bid_error))
                        }
                    }
                    Button(
                        onClick = {
                            showSuccessModal = false // Close the dialog
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.ok))
                    }

                }
            }
        }

        // --- Bid Bottom Sheet ---
        if (showBidBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBidBottomSheet = false // Dismiss when swiped down or tapped outside
                },
                sheetState = sheetState // Control sheet state
            ) {
                // Content of the Bottom Sheet
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(), // Handle system bars for bottom padding
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.place_your_bid),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        IconButton(onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBidBottomSheet = false // Hide state after closing
                                }
                            }

                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Bid Sheet")
                        }
                    }


                    val currentBidValue = product?.currentBid.toString()
                        .filter { it.isDigit() || it == '.' } // Remove non-numeric chars for parsing
                        .toDoubleOrNull() ?: 0.0 // Convert current bid string to Double




                    HorizontalDivider()

                    Text(
                        text = "Current bid: ${product?.currentBid ?: "-"}",
                        style = MaterialTheme.typography.titleLarge
                    )

                    OutlinedTextField(
                        value = bidAmountInput,
                        onValueChange = { newValue ->
                            // Allow only digits and a single decimal point (optional, depending on currency)
                            val filteredValue = newValue.filter { it.isDigit() || (it == '.' && !newValue.contains('.')) }
                            bidAmountInput = filteredValue
                        },
                        label = { Text(stringResource(R.string.your_bid)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Parse entered bid, handling invalid input
                    val enteredBidValue = bidAmountInput.toLongOrNull() ?: 0

                    // Determine if the "Bid Now" button should be enabled
                    val isBidButtonEnabled = enteredBidValue > currentBidValue && enteredBidValue > 0.0


                    Button(
                        onClick = {
                            if (product != null && enteredBidValue > 0.0) {
                                Log.d("PlaceBidViewModel", "Place bid called with $enteredBidValue")
                                placeBidViewModel.placeBid(product, enteredBidValue)
                            }
                        },
                        enabled = isBidButtonEnabled, // Button enabled based on validation
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        when (uiState) {
                            is PlaceBidViewModel.BidUIState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            is PlaceBidViewModel.BidUIState.Success -> {
                                LaunchedEffect(key1 = Unit) {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBidBottomSheet = false // Hide state after bid
                                            showSuccessModal = true
                                        }
                                    }
                                }

                            }
                            else -> {
                                Text(stringResource(R.string.make_bid_now))
                            }
                        }
                    }

                    if (bidAmountInput.isNotEmpty() && enteredBidValue <= currentBidValue && enteredBidValue > 0.0) {
                        Text(
                            text = stringResource(R.string.bid_too_low),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else if (bidAmountInput.isNotEmpty() && enteredBidValue <= 0.0) {
                        Text(
                            text = stringResource(R.string.invalid_bid),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}