//
//  CategoryDetailView.swift
//  iosApp
//
//  Created by Isaac Akakpo on 2025-05-29.
//

import SwiftUI
import Kingfisher 
import Shared
import KMPObservableViewModelSwiftUI
import KMPNativeCoroutinesAsync


// MARK: - CategoryDetailView
struct CategoryDetailView: View {
    let category: Shared.CategoryItem // Expects a Shared.CategoryItem
    @StateViewModel
    var viewModel : AuctionsHomeViewModel
    // Using SKIE might actually fix this Conversion KotlinInt(value: category.id) not sure if they support this Yet
  

    var body: some View {
        let categoryId  = KotlinInt(value: category.id)
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                KFImage(URL(string: "https://picsum.photos/seed/category1/300/200"))
                    .placeholder {
                        ProgressView()
                            .frame(maxWidth: .infinity, maxHeight: 200)
                            .background(Color.gray.opacity(0.1))
                    }
                    .resizable()
                    .aspectRatio(contentMode: .fill) // Fill the frame for category banner
                    .frame(maxWidth: .infinity, minHeight: 150, maxHeight: 200)
                    .clipped()

                VStack(alignment: .leading, spacing: 10) {
                    Text(category.headline)
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)

                    Text("Category ID: \(category.id)") // Assuming categoryId property
                        .font(.body)
                        .foregroundColor(.secondary)

                    Divider()

                    Text("Explore items in this category.")
                        .font(.body)
                        .foregroundColor(.primary)
                        .padding(.vertical, 5)

                   
                    Text("Products in \(category.headline)")
                        .font(.headline)
                        .padding(.top, 10)
                 

                    LazyVGrid(columns: [GridItem(.adaptive(minimum: 150))], spacing: 10) {
                        // Example: Display a Grid of products if you fetch them
                        ForEach(viewModel.categoryWithProducts[categoryId]!,id : \.id) { product in
                            ProductItemCard(item: product) // Re-use a product card
                        }
                    }
                
                    
                }
                .padding(.horizontal)

                Spacer()
            }
            .padding(.vertical)
        }
        .navigationTitle(category.headline)
        .navigationBarTitleDisplayMode(.inline)
        .background(Color.adaptiveBackground.ignoresSafeArea())
    }
}
