package org.auctions.klaravik.view.viewmodel

import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.launch
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.auctions.klaravik.view.data.CategoryItem
import org.auctions.klaravik.view.data.CategoryTreeResult
import org.auctions.klaravik.view.data.ProductItem
import org.auctions.klaravik.data.usecases.factories.AuctionUseCaseFactory

open class AuctionsHomeViewModel(private val auctionUseCaseFactory: AuctionUseCaseFactory) : BaseViewModel() {

    sealed class UIState {
        object Loading : UIState()
        class Success(val categories: CategoryTreeResult?, val products: List<ProductItem>) : UIState()
        class Error(val message: String) : UIState()
    }


    private val _selectedCategory = MutableStateFlow<CategoryItem?>(null)
    val selectedCategory: StateFlow<CategoryItem?> = _selectedCategory.asStateFlow()


    private val _selectedProduct = MutableStateFlow<ProductItem?>(null)
    val selectedProduct: StateFlow<ProductItem?> = _selectedProduct.asStateFlow()


    private val _products = MutableStateFlow<List<ProductItem>?>(null)

    @NativeCoroutinesState
    val products: StateFlow<List<ProductItem>?> =
        _products.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _categoryTreeResult = MutableStateFlow<CategoryTreeResult?>(null)

    @NativeCoroutinesState
    val categoryTreeResult: StateFlow<CategoryTreeResult?> = _categoryTreeResult.asStateFlow()

    private val _categoryToProducts = MutableStateFlow<Map<Int, List<ProductItem>>>(emptyMap())

    @NativeCoroutinesState
    val categoryWithProducts: StateFlow<Map<Int, List<ProductItem>>> = _categoryToProducts.asStateFlow()


    fun selectCategory(categoryItem: CategoryItem) {
        _selectedCategory.value = categoryItem
    }

    fun selectProduct(productItem: ProductItem) {
        _selectedProduct.value = productItem
    }

    private val _filteredProducts = MutableStateFlow<List<ProductItem>>(emptyList())
    val filteredProducts: StateFlow<List<ProductItem>> = _filteredProducts.asStateFlow()

    private val _filteredCategories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val filteredCategories: StateFlow<List<CategoryItem>> = _filteredCategories.asStateFlow()

    fun getProductsByCategory(categoryId: Int) {
        _filteredProducts.value = _categoryToProducts.value[categoryId] ?: emptyList()
    }

    fun filterCategories(categories: List<CategoryItem>?) {
        _filteredCategories.value = flattenCategories(categories?.toMutableList())
    }


    private val _uiStatus = MutableStateFlow<UIState?>(null)

    @NativeCoroutinesState
    val uiState: StateFlow<UIState?> = _uiStatus.asStateFlow()




    init {
        initViews()
    }

    fun initViews() {
        // Initialize views or perform any setup required for the ViewModel
        // This could include setting up observers, fetching initial data, etc.
        _uiStatus.value = UIState.Loading
        viewModelScope.launch {
            getHomeData()
        }
    }


    suspend fun getHomeData() {
        executeApiCallUseCase(
            inputValue = Unit,
            useCase = auctionUseCaseFactory.getHomeDataUseCase,
            callback = { result ->
                val categories = result.first
                val productItems= result.second
                // Process categories and product items in Default dispatcher, mainly for CPU-bound tasks
                viewModelScope.launch(Dispatchers.Default) {

                    // May not be required in a real application, but here we are associating products to categories
                    // there could be a call to the API to get products by category
                    _categoryToProducts.value = productItems.associateToCategories()
                    _categoryTreeResult.value = categories.buildCategoryTree()
                    _products.value = productItems
                    _uiStatus.value = UIState.Success(
                        categories = _categoryTreeResult.value,
                        products = _products.value ?: emptyList()
                    )
                }
            },
            onError = { exception ->
                // Handle the error
                _uiStatus.value = UIState.Error("Error fetching products: ${exception.message}")
                Logger.e { "Error: ${exception.message}" }
            }
        )

    }



    fun List<CategoryItem>.buildCategoryTree(): CategoryTreeResult {
        val childrenByParentId = mutableMapOf<Int, MutableList<CategoryItem>>()

        this.forEach { category ->
            if (category.parentId != null) {
                childrenByParentId.getOrPut(category.parentId) { mutableListOf() }.add(category)
            }
        }

        fun buildCategoryWithChildren(category: CategoryItem): CategoryItem {
            val directChildren = childrenByParentId[category.id] ?: emptyList()

            if (directChildren.isEmpty()) {
                return category.copy(children = null)
            }

            val updatedChildren = directChildren.map { child ->
                buildCategoryWithChildren(child)
            }


            return category.copy(children = updatedChildren.toMutableList())
        }

        val roots = this.filter { it.parentId == null }
            .map { buildCategoryWithChildren(it) }
            .sortedBy { it.headline }

        val finalIdMap = this.associateBy { it.id }

        return CategoryTreeResult(
            roots = roots,
            idMap = finalIdMap
        )
    }

    // For fast lookup of products by category. We might not need this if we can get products by category from the API
    fun List<ProductItem>.associateToCategories(): Map<Int, List<ProductItem>> {
        val categoryToProducts = mutableMapOf<Int, MutableList<ProductItem>>()

        fun addProductToCategory(catId: Int?, product: ProductItem) {
            if (catId != null) {
                categoryToProducts.getOrPut(catId) { mutableListOf() }.add(product)
            }
        }

        forEach { product ->
            addProductToCategory(product.categoryLevel1, product)
            addProductToCategory(product.categoryLevel2, product)
            addProductToCategory(product.categoryLevel3, product)
        }

        return categoryToProducts
    }

    // Flattens a list of categories into a single list, including all children recursively. O(n) time complexity
    fun flattenCategories(categories: MutableList<CategoryItem>?): List<CategoryItem> {
        val flatList = mutableListOf<CategoryItem>()
        val queue = ArrayDeque<CategoryItem>()

        if (categories.isNullOrEmpty()) {
            return flatList
        }

        // Add all initial categories to the queue
        categories.forEach { category ->
            queue.addLast(category)
        }

        while (queue.isNotEmpty()) {
            val currentCategory = queue.removeFirst()
            flatList.add(currentCategory)

            // If this category has children, add them to the end of the queue
            currentCategory.children?.let { children ->
                children.forEach { child ->
                    queue.addLast(child)
                }
            }
        }
        return flatList
    }


}