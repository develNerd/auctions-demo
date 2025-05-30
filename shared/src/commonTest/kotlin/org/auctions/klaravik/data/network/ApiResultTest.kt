package org.auctions.klaravik.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.mock.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.date.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomHttpResponse(
    private val statusCode: Int,
    private val description: String
) :
    HttpResponse() {

    @InternalAPI
    override val rawContent: ByteReadChannel = ByteReadChannel("")

    val mockEngine = MockEngine { request ->
        respond(
            content = ByteReadChannel("""{"ip":"127.0.0.1"}"""),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }

    override val call: HttpClientCall
        get() = HttpClientCall(HttpClient(engine = mockEngine))

    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext

    override val headers: Headers
        get() = Headers.Empty

    override val requestTime: GMTDate
        get() = GMTDate()

    override val responseTime: GMTDate
        get() = GMTDate()

    override val status: HttpStatusCode
        get() = HttpStatusCode(statusCode, description)

    override val version: HttpProtocolVersion
        get() = HttpProtocolVersion(name = "HTTP", major = 1, minor = 1)}

class ApiResultTest {

    private val customResponse = CustomHttpResponse(
        statusCode = HttpStatusCode.BadRequest.value,
        description = "Bad Request"
    )

    @Test
    fun `should emit success when call succeeds`() = runBlocking {
        val expectedResult = "test result"
        val flow = makeRequestToApiFlow { expectedResult }
        
        val results = flow.toList()
        assertEquals(ApiResult.InProgress::class, results[0]::class)
        assertEquals(expectedResult, (results[1] as ApiResult.Success).response)
    }

    val mockIOException = IOException("Mock IO Exception")

    @Test
    fun `should emit http error for no internet exceptions`() = runBlocking {

        val mockException = IOException("Test Exception")

        val flow = makeRequestToApiFlow { throw mockException }
        
        val result = flow.toList().last()
        assertEquals(ApiResult.NoInternet::class, result::class)
    }

    @Test
    fun `should emit generic error for other exceptions`() = runBlocking {
        val mockException = Exception("Test Exception")
        val flow = makeRequestToApiFlow { throw mockException }
        
        val result = flow.toList().last()
        assertEquals(ApiResult.GenericError::class, result::class)
        assertEquals(mockException, (result as ApiResult.GenericError).error)
    }


}