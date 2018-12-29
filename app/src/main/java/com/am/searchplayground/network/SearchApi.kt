package com.am.searchplayground.network

import com.am.searchplayground.SEARCH_PLACES_KEY
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Single

interface SearchApi {

    @GET("json")
    fun getSearchResults(@Query("input") keywords: String, @Query("key") key: String = SEARCH_PLACES_KEY): Single<SearchApiContract>

}