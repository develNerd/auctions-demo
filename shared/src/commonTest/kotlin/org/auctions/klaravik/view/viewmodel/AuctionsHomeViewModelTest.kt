package org.auctions.klaravik.view.viewmodel

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.auctions.klaravik.data.TestDataProvider
import org.auctions.klaravik.data.model.CategoryItemDTO
import org.auctions.klaravik.data.network.ApiResult
import org.auctions.klaravik.data.repositories.GetAuctionRepository
import org.auctions.klaravik.data.usecases.factories.AuctionUseCaseFactory
import org.auctions.klaravik.view.data.CategoryItem
import org.auctions.klaravik.view.data.ProductItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuctionsHomeViewModelTest {

    private val repository = mock<GetAuctionRepository>(MockMode.strict) {

        everySuspend { getCategoriesProducts() } returns flow {
            emit(ApiResult.InProgress)
            emit(ApiResult.Success(
                Pair(
                    TestDataProvider.provideDummyCategoryItemDTO().map { it.toCategoryItem() },
                    listOf()
                )
            ))
        }

    }
    private val mockUseCaseFactory: AuctionUseCaseFactory = AuctionUseCaseFactory(repository)
    private val viewModel = AuctionsHomeViewModel(mockUseCaseFactory)

    @Test
    fun `should update selected category`() = runTest {
        val testCategory = TestDataProvider.provideDummyCategoryItemDTO().first().toCategoryItem()
        viewModel.selectCategory(testCategory)
        assertEquals(testCategory, viewModel.selectedCategory.value)
    }

    @Test
    fun `should update selected product`() = runTest {
        val testProduct = ProductItem()
        viewModel.selectProduct(testProduct)
        assertEquals(testProduct, viewModel.selectedProduct.value)
    }


    @Test
    fun `should flatten categories correctly`() = runTest {
        val childCategory1 = CategoryItem(id = 1, parentId = 0, headline = "Child Category 1", level = 2)
        val childCategory2 = CategoryItem(id = 1, parentId = 0, headline = "Child Category 2", level = 2)
        val parentCategory = CategoryItem(id = 1, parentId = 0, headline = "Parent Category 1", level = 2, children = mutableListOf()).apply {
            children?.add(childCategory1)
            children?.add(childCategory2)
        }

        val result = viewModel.flattenCategories(mutableListOf(parentCategory))
        assertEquals(3, result.size) // Parent + 2 children
        assertTrue { result.contains(parentCategory) }
        assertTrue(result.contains(childCategory1))
        assertTrue(result.contains(childCategory2))
    }
}
