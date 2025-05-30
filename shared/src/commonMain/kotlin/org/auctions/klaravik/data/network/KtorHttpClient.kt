package org.auctions.klaravik.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.auctions.klaravik.BuildKonfig


val appBaseUrl: String
    get() = "app.klaravik.dev"

class KtorHttpClient(val httpClient: HttpClient) {


    suspend inline fun <reified T : Any> GET(
        route: String,
        queryPair: List<Pair<String, String>>? = null,
    ): T = httpClient.get {

        addAuthenticationIfRequired(true)
        url {
            protocol = URLProtocol.HTTPS
            path(route)
            queryPair?.forEach { pair ->
                parameters.append(pair.first, pair.second)
            }

        }
    }.body() as T

    fun HttpRequestBuilder.addAuthenticationIfRequired(isAuthorizationRequired: Boolean) {
        if (isAuthorizationRequired) {
            header("X-API-Key", BuildKonfig.apiKey)
        }
    }
}


