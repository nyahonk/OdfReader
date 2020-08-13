package com.nyahonk.odfreader.di


import com.nyahonk.odfreader.presentation.DocumentFragmentViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class
    ]
)
interface AppComponent {

    fun documentFragmentViewModel(): DocumentFragmentViewModel
}