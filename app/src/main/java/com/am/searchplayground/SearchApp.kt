package com.am.searchplayground

import android.app.Application
import com.am.searchplayground.di.AppModule
import com.am.searchplayground.di.DaggerSearchAppComponent
import com.am.searchplayground.di.SearchAppComponent

class SearchApp : Application() {
    lateinit var searchAppComponent: SearchAppComponent

    override fun onCreate() {
        super.onCreate()

        buildDaggerComponent()
    }

    private fun buildDaggerComponent() {
        searchAppComponent = DaggerSearchAppComponent.builder().appModule(AppModule(this)).build()
    }


    fun getSearchComponent(): SearchAppComponent = searchAppComponent
}