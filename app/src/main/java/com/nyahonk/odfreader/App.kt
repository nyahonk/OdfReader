package com.nyahonk.odfreader

import android.app.Application
import com.nyahonk.odfreader.di.AppComponent
import com.nyahonk.odfreader.di.AppModule
import com.nyahonk.odfreader.di.DaggerAppComponent
import com.nyahonk.odfreader.di.DaggerApplication

class App : Application(), DaggerApplication {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        buildDi()
    }

    override fun getComponent() = appComponent

    private fun buildDi() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

    }
}