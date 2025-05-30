package org.auctions.klaravik

import android.app.Application
import org.auctions.klaravik.di.initKoin
import org.auctions.klaravik.view.viewmodel.AuctionsHomeViewModel
import org.koin.dsl.module

class MuseumApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            listOf(
                module {
                    factory { AuctionsHomeViewModel(get()) }
                }
            )
        )
    }
}
