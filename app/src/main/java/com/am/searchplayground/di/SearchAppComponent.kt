package com.am.searchplayground.di

import com.am.searchplayground.ui.MainActivity
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, StorageModule::class])
interface SearchAppComponent {

    fun inject(activity: MainActivity)
}