package org.auctions.klaravik.data.usecases

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.auctions.klaravik.data.model.CategoryItemDTO
import org.auctions.klaravik.data.model.ProductItemDTO
import org.auctions.klaravik.data.network.ApiResult
import org.auctions.klaravik.data.repositories.GetAuctionRepository
import org.auctions.klaravik.view.data.ProductItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PlaceBidUseCaseTest {
    private val repository = mock<GetAuctionRepository>(MockMode.strict) {

        everySuspend { getCategories() } returns flow {
            emit(ApiResult.InProgress)
            emit(ApiResult.Success(listOf(CategoryItemDTO(
                headline = "Test Category",
                id = 1,
                level = 1,
                parentId = 1
            )).map { it.toCategoryItem() }))
        }

        everySuspend { placeBid(ProductItem()) } returns listOf(ProductItem())
    }
    private val useCase = PlaceBidUseCase(repository)


    @Test
    fun `should return updated products when bid placement succeeds`() = runTest {
        val testProduct = ProductItem()
        val expectedProducts = listOf(ProductItemDTO())

        everySuspend { repository.placeBid(testProduct) } returns expectedProducts.map { it.toProductItem() }

        val result = useCase.execute(testProduct)

        assertEquals(expectedProducts.map { it.toProductItem()}, result)
    }

    @Test
    fun `should propagate repository exception`() = runTest {
        val testProduct = ProductItem()
        val expectedException = RuntimeException("Bid failed")

        everySuspend { repository.placeBid(testProduct) } throws expectedException

        val actualException = assertFailsWith<RuntimeException> {
            useCase.execute(testProduct)
        }

        assertEquals(expectedException, actualException)
    }
}
