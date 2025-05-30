//
//  ProductDetailView.swift
//  iosApp
//
//  Created by Isaac Akakpo on 2025-05-29.
//

import Kingfisher
import KMPNativeCoroutinesAsync
import KMPObservableViewModelSwiftUI
import Shared
import SwiftUI

struct ProductDetailView: View {
    let product: Shared.ProductItem

    @ObservedObject var viewModel: AuctionsHomeViewModel
    @State private var showBidSheet = false
    @State private var bidAmountInput: String = ""
    @State private var bidValidationMessage: String = ""

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                KFImage(URL(string: product.image?.largeUrl ?? product.image?.thumbUrl ?? "")) // Use full image URL if available, else thumbnail
                    .placeholder {
                        ProgressView() // Show a loading indicator
                            .frame(maxWidth: .infinity, maxHeight: 250)
                            .background(Color.gray.opacity(0.1))
                    }
                    .resizable()
                    .aspectRatio(contentMode: .fit) // Use .fit to show entire image
                    .frame(maxWidth: .infinity, maxHeight: 250)
                    .clipped()

                VStack(alignment: .leading, spacing: 10) {
                    Text(product.name ?? "Product Details")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)

                    Text("Current Bid: $\(product.currentBid ?? 0)")
                        .font(.title2)
                        .fontWeight(.semibold)
                        .foregroundColor(.accentColor)

                    Divider()

                    if let description = product.description_ { // Assuming 'description_' in KMP
                        Text("Description:")
                            .font(.headline)
                            .padding(.top, 5)
                        Text(description)
                            .font(.body)
                            .foregroundColor(.primary)
                    } else {
                        Text("No detailed description available.")
                            .font(.body)
                            .foregroundColor(.secondary)
                    }
                }
                .padding(.horizontal)

                Spacer()

                Button(action: {
                    showBidSheet = true
                    bidAmountInput = ""
                    bidValidationMessage = ""
                }) {
                    Text("Place Bid")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.accentColor)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                .padding(.horizontal)
                .padding(.bottom)
            }
            .padding(.vertical)
        }
        .navigationTitle(product.name ?? "Product") // Set navigation title
        .navigationBarTitleDisplayMode(.inline)
        .background(Color.adaptiveBackground.ignoresSafeArea())
        .sheet(isPresented: $showBidSheet) {
            VStack(alignment: .leading, spacing: 20) {
                KFImage(URL(string: product.image?.largeUrl ?? product.image?.thumbUrl ?? "")) // Use full image URL if available, else thumbnail
                    .placeholder {
                        ProgressView() // Show a loading indicator
                            .frame(maxWidth: .infinity, maxHeight: 250)
                            .background(Color.gray.opacity(0.1))
                    }
                    .resizable()
                    .aspectRatio(contentMode: .fit) // Use .fit to show entire image
                    .frame(maxWidth: .infinity, maxHeight: 250)
                    .clipped()

                let currentBidString = "\(product.currentBid)"
                let currentBidCleaned = currentBidString.filter { "0123456789.".contains($0) }
                let currentBidValue = Double(currentBidCleaned) ?? 0.0

                let enteredBidAsDouble = Double(bidAmountInput) ?? 0.0

                HStack {
                    Text("Place Your Bid")
                        .font(.headline)
                    Spacer()
                    Button(action: {
                        showBidSheet = false
                    }) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title2)
                            .foregroundColor(.secondary)
                    }
                }
                .padding(.bottom, 10)

                Divider()

                Text("Current Bid: \(product.currentBid)")
                    .font(.title2)
                    .fontWeight(.semibold)
                    .foregroundColor(.accentColor)

                TextField("Enter your bid amount", text: $bidAmountInput)
                    .keyboardType(.numberPad)
                    .textFieldStyle(.roundedBorder)
                    .padding(.horizontal)
                    .numbersOnly($bidAmountInput)
                    .onChange(of: bidAmountInput) { _, newValue in
                        let groupingSeparator: String = Locale.current.groupingSeparator ?? ","
                        
                        // Remove commas for comparison
                        let cleanedValue = newValue.replacingOccurrences(of: groupingSeparator, with: "")
                        let latestEnteredValue = Double(cleanedValue) ?? 0.0
                        if latestEnteredValue <= 0 {
                            bidValidationMessage = "Bid must be a positive number."
                        } else if latestEnteredValue <= currentBidValue {
                            bidValidationMessage = "Your bid must be higher than the current bid."
                        } else {
                            bidValidationMessage = ""
                        }
                    }

                if !bidValidationMessage.isEmpty {
                    Text(bidValidationMessage)
                        .foregroundColor(.red)
                        .font(.caption)
                }
                let isBidButtonEnabled = bidAmountInput.toDoubleFromFormattedString() > currentBidValue && bidAmountInput.toDoubleFromFormattedString() > 0.0

                Button(action: {
                    showBidSheet = false
                }) {
                    Text("Bid Now")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(isBidButtonEnabled ? Color.blue : Color.gray)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                .disabled(!isBidButtonEnabled)
            }
            .padding()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .toolbar {
                ToolbarItem(placement: .keyboard) {
                    Spacer()
                }
            }
            .onAppear {
                UITextField.appearance().clearButtonMode = .whileEditing
            }
        }
    }
}
