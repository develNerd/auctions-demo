package org.auctions.klaravik.di

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.auctions.klaravik.data.network.KlaravikApi
import org.auctions.klaravik.data.network.KtorHttpClient
import org.auctions.klaravik.data.network.appBaseUrl
import org.auctions.klaravik.data.repositories.GetAuctionRepository
import org.auctions.klaravik.data.repositoryImpl.GetAuctionRepositoryImpl
import org.auctions.klaravik.data.usecases.factories.AuctionUseCaseFactory
import org.auctions.klaravik.data.usecases.factories.BidUseCaseFactory
import org.auctions.klaravik.view.viewmodel.AuctionsHomeViewModel
import org.auctions.klaravik.view.viewmodel.PlaceBidViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val commonModule = module {
    single {
         HttpClient( ) {

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = appBaseUrl
                }
                header("Content-Type", "application/json")
                header("Accept", "application/json")
            }

            val json = Json {
                ignoreUnknownKeys = true // This will skip unknown keys
                coerceInputValues = true // Optional: Coerce invalid values to defaults
            }

            install(ContentNegotiation) {
                json(json)
            }



            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        co.touchlab.kermit.Logger.v { message }
                    }
                }
                level = LogLevel.INFO
            }

        }
    }
    singleOf(::KtorHttpClient)
    single { KlaravikApi(get()) }
    single<GetAuctionRepository> { GetAuctionRepositoryImpl(get()) }
    single {
        AuctionUseCaseFactory(get())
    }
    single {
        BidUseCaseFactory(get())
    }
    factory {
        PlaceBidViewModel(get())
    }
    single {
        AuctionsHomeViewModel(get())
    }

}
fun initKoin() = initKoin(emptyList())

fun initKoin(extraModules: List<Module>) {
    startKoin {
        modules(
            commonModule,
            *extraModules.toTypedArray(),
        )
    }
}