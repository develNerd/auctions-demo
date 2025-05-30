import Kingfisher // For async image loading, similar to Coil
import KMPNativeCoroutinesAsync
import KMPObservableViewModelSwiftUI
import Shared
import SwiftUI

// MARK: - Main Screen

struct AuctionAppScreen: View {
    @StateViewModel
    var viewModel = AuctionsHomeViewModel(
        auctionUseCaseFactory: KoinDependencies().authUseCaseFactory
    )

    var body: some View {
        NavigationStack {
            ZStack(alignment: .top) {
                // Adaptive background color for the entire screen
                Color.adaptiveBackground.ignoresSafeArea()

                ScrollView(.vertical, showsIndicators: false) {
                    VStack(spacing: 16) {
                        Spacer().frame(height: 8)

                        RecommendedSection(items: viewModel.products?.suffix(20),viewModel: viewModel)
                        AuctionSection(items: viewModel.categoryWithProducts.values.flatMap { $0 },viewModel: viewModel)
                        CategoriesSection(items: viewModel.categoryTreeResult?.roots ?? [],viewModel: viewModel)

                        Spacer().frame(height: 16)
                    }
                    .padding(.top, 70) // To account for the fixed search bar
                }

                // Fixed Search Bar at the top
                VStack {
                    SearchBar()
                        .padding(.top, 0)
                }
            }
            .navigationBarHidden(true) // Hide default navigation bar
        }
    }
}

// MARK: - SearchBar

struct SearchBar: View {
    @State private var searchText: String = ""

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(.secondary) // Adaptive gray
                    .padding(.leading, 12)
                TextField("Search...", text: $searchText)
                    .padding(.vertical, 12)
                    // Adaptive background for the text field itself
                    .background(Color.adaptiveCardBackground)
                    .cornerRadius(50) // Fully rounded
                    .frame(height: 50) // Fixed height
            }
            // Adaptive background for the search bar capsule
            .background(Color.adaptiveCardBackground)
            .cornerRadius(50)
            .padding(.horizontal, 16)
            .padding(.bottom, 12)
            // Adaptive background for the entire search bar container
        }
        .ignoresSafeArea(.container, edges: .top) // Ensure it extends to the top edge
    }
}

// MARK: - SectionHeader

struct SectionHeader: View {
    let title: String
    let onViewAllClicked: () -> Void

    var body: some View {
        HStack {
            Text(title)
                .font(.system(size: 18, weight: .bold)) // Slightly smaller title
                .foregroundColor(.primary) // Adaptive text color
            Spacer()
            Button(action: onViewAllClicked) {
                HStack(spacing: 4) {
                    Text("View all")
                        .font(.system(size: 13, weight: .regular))
                        .foregroundColor(.secondary) // Adaptive gray
                    Image(systemName: "arrow.forward")
                        .resizable()
                        .frame(width: 12, height: 12) // Adjusted size
                        .foregroundColor(.secondary) // Adaptive gray
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
    }
}

// MARK: - RecommendedSection

struct RecommendedSection: View {
    let items: [ProductItem]?
    var viewModel: AuctionsHomeViewModel

    var body: some View {
        VStack(alignment: .leading) {
            SectionHeader(title: "Recommended for you") {
                print("View All Recommended clicked!")
            }
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 12) {
                    if let items = items {
                        ForEach(items, id: \.mainId) { item in
                            NavigationLink(destination: ProductDetailView(product: item,viewModel: viewModel)) { // Navigate to ProductDetailView
                                ProductItemCard(item: item)
                            }
                            .buttonStyle(PlainButtonStyle())
                        }
                    } else {
                        // Shimmer effect
                        ForEach(0 ..< 3) { _ in
                            ShimmerBox()
                                .shimmer(active: true)
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
            }
        }
    }
}

// MARK: - ShimmerBox (Conceptual)

struct ShimmerBox: View {
    var body: some View {
        RoundedRectangle(cornerRadius: 12)
            // Adaptive background for shimmer boxes (cards)
            .fill(Color.adaptiveCardBackground)
            .frame(width: 150, height: 200)
            // Adaptive shadow color
            .shadow(color: Color.adaptiveShadow, radius: 2, x: 0, y: 2)
    }
}

// MARK: - RecommendedItemCard

struct ProductItemCard: View {

    let item: Shared.ProductItem

    var body: some View {
        VStack(alignment: .leading) {
            ZStack(alignment: .topTrailing) {
                KFImage(URL(string:item.image?.thumbUrl ?? ""))
                    .placeholder {
                        Color.gray.opacity(0.2) // Placeholder still okay
                    }
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: 150, height: 100)
                    .clipped()
            }
            VStack(alignment: .leading) {
                Text(item.name ?? "Unknown Product") // Use KMP product name
                    .font(.system(size: 13, weight: .semibold))
                    .lineLimit(1)
                    .truncationMode(.tail)
                    .foregroundColor(.primary) // Adaptive text color
                Spacer().frame(height: 4)
                Text("$\(item.currentBid ?? 0)") // Use KMP currentBid
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(.accentColor) // Uses the app's accent color (adaptive)
                Spacer().frame(height: 6)
                Text(item.getAuctionCountdownString()) // Use the new formatted method
                    .font(.system(size: 11))
                    .foregroundColor(.secondary) // Adaptive gray
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 10)
        }
        .frame(width: 150)
        .background(Color.adaptiveCardBackground)
        .cornerRadius(12)
        .shadow(color: Color.adaptiveShadow, radius: 2, x: 0, y: 2)
  
    }
}


// MARK: - AuctionSection

struct AuctionSection: View {
    let items: [ProductItem]
    var viewModel: AuctionsHomeViewModel

    var body: some View {
        VStack(alignment: .leading) {
            SectionHeader(title: "Now in the auction") {
                print("View All Auction clicked!")
            }
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 16) {
                    ForEach(items, id: \.mainId ) { item in
                        NavigationLink(destination: ProductDetailView(product: item,viewModel: viewModel)) { // Navigate to ProductDetailView
                            AuctionItemCard(item: item)
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
            }
        }
    }
}

// MARK: - AuctionItemCard

struct AuctionItemCard: View {
    let item: ProductItem

    var body: some View {
        VStack(alignment: .leading) {
            KFImage(URL(string: item.image?.thumbUrl ?? ""))
                .placeholder {
                    Color.gray.opacity(0.2)
                }
                .resizable()
                .aspectRatio(contentMode: .fill)
                .frame(width: 280, height: 160)
                .clipped()
                .cornerRadius(12, corners: [.topLeft, .topRight])

            VStack(alignment: .leading) {
                Text(item.name ?? "")
                    .font(.system(size: 15, weight: .bold))
                    .lineLimit(1)
                    .truncationMode(.tail)
                    .foregroundColor(.primary)
                Spacer().frame(height: 8)
                HStack {
                    Image(systemName: "info.circle.fill")
                        .resizable()
                        .frame(width: 14, height: 14)
                        .foregroundColor(.accentColor) // Uses the app's accent color
                    Text(item.getAuctionCountdownString())
                        .font(.system(size: 12, weight: .semibold))
                        .foregroundColor(.accentColor) // Uses the app's accent color
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
        }
        .frame(width: 280)
        // Adaptive background for the card
        .background(Color.adaptiveCardBackground)
        .cornerRadius(12)
        // Adaptive shadow color
        .shadow(color: Color.adaptiveShadow, radius: 2, x: 0, y: 2)
    }
}

// MARK: - CategoriesSection

struct CategoriesSection: View {
    let items: [CategoryItem]
    
    var viewModel: AuctionsHomeViewModel

    var body: some View {
        VStack(alignment: .leading) {
            SectionHeader(title: "Categories") {
                print("View All Categories clicked!")
            }
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 12) {
                    ForEach(items, id: \.id) { item in
                        NavigationLink(destination: CategoryDetailView(category: item, viewModel: viewModel)) { // Navigate to CategoryDetailView
                            CategoryItemCard(item: item)
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
            }
        }
    }
}

// MARK: - CategoryItemCard

struct CategoryItemCard: View {
    let item: CategoryItem

    var body: some View {
        ZStack {
            KFImage(URL(filePath: "https://picsum.photos/seed/category1/300/200"))
                .placeholder {
                    Color.gray.opacity(0.2)
                }
                .resizable()
                .aspectRatio(contentMode: .fill)
                .frame(width: 120, height: 70)
                .clipped()

            // Semi-transparent black scrim works in both modes as it darkens the image
            Color.black.opacity(0.45)

            Text(item.headline)
                .font(.system(size: 13, weight: .semibold))
                // White text on a dark scrim works in both modes
                .foregroundColor(.white)
                .multilineTextAlignment(.center)
                .lineLimit(2)
                .padding(.horizontal, 8)
        }
        .frame(width: 120, height: 70)
        .cornerRadius(10)
        // Adaptive shadow color
        .shadow(color: Color.adaptiveShadow, radius: 1, x: 0, y: 1)
    }
}

// MARK: - Extensions for convenience and adaptive colors

extension Color {
    // Primary background color for the main screen. Automatically light/dark.
    static let adaptiveBackground = Color(uiColor: .systemBackground)
    // Background color for elements like the search bar background area.
    static let adaptiveSearchBarBackground = Color(uiColor: .secondarySystemBackground)
    // Background color for cards and input fields.
    static let adaptiveCardBackground = Color(uiColor: .systemBackground)

    // Adaptive shadow color.
    // In light mode, it's a subtle black.
    // In dark mode, shadows are typically less pronounced,
    // so we make it even more subtle (or you could use .clear for no shadow).
    static var adaptiveShadow: Color {
        Color(uiColor: UIColor { traitCollection in
            if traitCollection.userInterfaceStyle == .dark {
                return UIColor.black.withAlphaComponent(0.3) // More visible in dark mode
            } else {
                return UIColor.black.withAlphaComponent(0.1) // Subtle in light mode
            }
        })
    }
}

// Extension to apply corner radius to specific corners
extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape(RoundedCorner(radius: radius, corners: corners))
    }
}

private struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}

// MARK: - Shimmer Modifier (Conceptual)

extension View {
    func shimmer(active: Bool) -> some View {
        modifier(ShimmerModifier(active: active))
    }
}

struct ShimmerModifier: ViewModifier {
    var active: Bool
    @State private var phase: CGFloat = 0

    func body(content: Content) -> some View {
        content
            .mask(
                LinearGradient(gradient: Gradient(colors: [.clear, .white.opacity(0.8), .clear]), startPoint: .leading, endPoint: .trailing)
                    .offset(x: active ? phase * 400 - 200 : 0) // Adjust speed and range
            )
            .animation(
                active ? Animation.linear(duration: 1.5).repeatForever(autoreverses: false) : .default,
                value: phase
            )
            .onAppear {
                if active {
                    phase = 1.0
                }
            }
    }
}

// MARK: - Preview

struct AuctionAppScreen_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            AuctionAppScreen()
                .preferredColorScheme(.light) // Preview in Light Mode
            AuctionAppScreen()
                .preferredColorScheme(.dark) // Preview in Dark Mode
        }
    }
}

// For Unique Ids
extension Shared.ProductItem:  @retroactive Identifiable {
    public var mainId: String {
        return "\(self.id)" + UUID().uuidString
    }
}
