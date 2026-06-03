package com.example.androidakademijaprojekt

import android.app.Application
import com.example.androidakademijaprojekt.DI.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AndroidAkademijaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AndroidAkademijaApplication)
            modules(appModule)
        }
    }
}