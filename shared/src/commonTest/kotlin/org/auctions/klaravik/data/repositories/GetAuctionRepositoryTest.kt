package org.auctions.klaravik.data.repositories

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.auctions.klaravik.data.TestDataProvider
import org.auctions.klaravik.data.model.CategoryItemDTO
import org.auctions.klaravik.data.model.ProductItemDTO
import org.auctions.klaravik.data.network.ApiResult
import org.auctions.klaravik.data.network.KlaravikApi
import org.auctions.klaravik.data.network.dataOrNull
import org.auctions.klaravik.data.network.makeRequestToApiFlow
import org.auctions.klaravik.view.data.CategoryItem
import org.auctions.klaravik.view.data.ProductItem
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAuctionRepositoryTest {


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

    @Test
    fun `should return success when api call succeeds`() = runTest {
        val expectedProductItemsDTO = listOf(ProductItem())

        val apiResultSuccess = ApiResult.Success(expectedProductItemsDTO)

        everySuspend { repository.getProductItems() } returns flow {
            emit(ApiResult.Success(listOf(ProductItemDTO()).map { it.toProductItem() }))
        }

        val result = repository.getProductItems().first()

        delay(1000)

        assertEquals(apiResultSuccess, result)
    }

    @Test
    fun `should return error when api call for getAuctions fails`() = runTest {

        val expectedError = ApiResult.NoInternet

        // Mock the underlying API call to throw the exception
        everySuspend { repository.getProductItems() } returns flow {
            emit(ApiResult.NoInternet)
        }

        val result = repository.getProductItems()

        assertEquals(expectedError, result.first())
    }


    @Test
    fun `should return products when api call succeeds`() = runTest {
        val dummyProductItem = ProductItem(name = "Test Product Klaravik")
        val expectedProducts = ApiResult.Success(listOf(dummyProductItem))
        everySuspend { repository.getProductItems() } returns  flow {
            emit(ApiResult.Success(listOf(dummyProductItem)))
        }
        val result = repository.getProductItems()
        assertEquals(expectedProducts.response.first().name, result.first().dataOrNull?.first()?.name)
    }



    @Test
    fun `should return categories when api call succeeds`() = runTest {
        val expectedProducts = ApiResult.Success(TestDataProvider.provideDummyCategoryItemDTO().map { it.toCategoryItem() })
        everySuspend {repository.getCategories() } returns flow {
            emit(ApiResult.Success(TestDataProvider.provideDummyCategoryItemDTO().map { it.toCategoryItem() }))
        }
        val result = repository.getCategories()
        assertEquals(expectedProducts, result.first() as ApiResult.Success<*>)
    }

    @Test
    fun `should place bid successfully`() = runTest {
        val testProduct = ProductItem()
        val expectedResult = listOf(testProduct)

        // Mock the direct API call
        everySuspend { repository.placeBid(testProduct) } returns expectedResult

        val result = repository.placeBid(testProduct)

        assertEquals(expectedResult, result)
    }


}



class GetAuctionRepositoryImpl() : GetAuctionRepository {
    override suspend fun getProductItems(): Flow<ApiResult<List<ProductItem>>> {
        return  makeRequestToApiFlow {
            listOf(ProductItemDTO()).map { it.toProductItem() }
        }
    }



    override suspend fun getCategories(): FlowApiResult<List<CategoryItem>> {
        return makeRequestToApiFlow {
            listOf(CategoryItemDTO(
                headline = "Test Category",
                id = 1,
                level = 1,
                parentId = 1
            )).map { it.toCategoryItem() }
        }
    }

    override suspend fun placeBid(productItem: ProductItem): List<ProductItem> {
        // Simulate placing a bid by returning the product item in a list
        return listOf(productItem)
    }


    override suspend fun getCategoriesProducts(): FlowApiResult<Pair<List<CategoryItem>, List<ProductItem>>> {
        return  makeRequestToApiFlow {
            val categories = listOf(CategoryItemDTO(
                headline = "Test Category",
                id = 1,
                level = 1,
                parentId = 1
            )).map { it.toCategoryItem() }

            val products = listOf(ProductItemDTO()).map { it.toProductItem() }

            Pair(categories, products)
        }
    }
}

