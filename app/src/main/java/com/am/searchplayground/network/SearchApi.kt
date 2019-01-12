package com.am.searchplayground.network

import com.am.searchplayground.SEARCH_PLACES_KEY
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Single

interface SearchApi {

    @GET("autocomplete/json")
    fun getSearchResults(@Query("input") keywords: String, @Query("key") key: String = SEARCH_PLACES_KEY): Single<SearchApiContract>

    @GET("textsearch/json")
    fun getTextSearchResults(@Query("query") query: String, @Query("next_page_token") nextPageToken:String?=null,
                             @Query("key") key: String = SEARCH_PLACES_KEY):Single<SearchPlacesResponse>
}