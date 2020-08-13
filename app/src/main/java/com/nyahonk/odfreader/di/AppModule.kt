package com.nyahonk.odfreader.di

import android.content.Context
import com.nyahonk.odfreader.presentation.ErrorPublisher
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Provides
    fun provideContext(): Context = context

    @Singleton
    @Provides
    fun bindErrorPublisher(): ErrorPublisher = ErrorPublisher()
}