package org.auctions.klaravik.data.usecases

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.auctions.klaravik.data.TestDataProvider
import org.auctions.klaravik.data.model.CategoryItemDTO
import org.auctions.klaravik.data.network.ApiResult
import org.auctions.klaravik.data.repositories.GetAuctionRepository
import org.auctions.klaravik.view.data.ProductItem
import kotlin.test.Test
import kotlin.test.assertEquals

class GetCategoriesUseCaseTest {


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
    private val useCase = GetCategoriesUseCase(repository)

    @Test
    fun `should return categories when repository call succeeds`() = runTest {
        val expectedCategories = TestDataProvider.provideDummyCategoryItemDTO().map { it.toCategoryItem() }

        everySuspend { repository.getCategories() } returns flow {
            emit(ApiResult.Success(expectedCategories))
        }

        val result = useCase.execute(Unit).first()

        assertEquals(expectedCategories, (result as ApiResult.Success).response)
    }

    @Test
    fun `should return error when repository call fails`() = runTest {

        val expectedError = ApiResult.NoInternet

        everySuspend { repository.getCategories() } returns flow {
            emit(expectedError)
        }

        val result = useCase.execute(Unit).first()

        assertEquals(expectedError, result)
    }


}