package com.am.searchplayground.network

import retrofit2.http.GET
import retrofit2.http.Query
import rx.Single

interface SearchApi {

    @GET("json")
    fun getSearchResults(@Query("input") input: String, @Query("key") key: String = "AIzaSyAI0VTgOxJVXy1m67SCmXPN1omjWzhhi1g"): Single<SearchApiContract>

}