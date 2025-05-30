package org.auctions.klaravik.view.viewmodel

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import org.auctions.klaravik.data.TestDataProvider
import org.auctions.klaravik.data.usecases.factories.AuctionUseCaseFactory
import org.auctions.klaravik.view.data.CategoryItem
import org.auctions.klaravik.view.data.ProductItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuctionsHomeViewModelTest {
    private val mockUseCaseFactory: AuctionUseCaseFactory = AuctionUseCaseFactory(mock(MockMode.strict))
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
        val parentCategory = CategoryItem(id = 1, parentId = 0, headline = "Child Category 1", level = 2).apply {
            every { children } returns mutableListOf(childCategory1, childCategory2)
        }

        val result = viewModel.flattenCategories(mutableListOf(parentCategory))
        assertEquals(3, result.size) // Parent + 2 children
        assertTrue { result.contains(parentCategory) }
        assertTrue(result.contains(childCategory1))
        assertTrue(result.contains(childCategory2))
    }
}
