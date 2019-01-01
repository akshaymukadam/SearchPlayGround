package com.am.searchplayground.model

import com.am.searchplayground.network.SearchApi
import com.am.searchplayground.network.SearchPlacesResponse
import com.am.searchplayground.network.SearchPlacesResult
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription


class SearchRepository(private val searchApi: SearchApi) {

    private val compositeSubscription: CompositeSubscription = CompositeSubscription()

    fun fetchSuggestions(input: String, callBack: NetworkContract<List<SearchPlacesResult>>) {
        callBack.showProgress()
        val subscription = searchApi.getTextSearchResults(query = input)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ t: SearchPlacesResponse? ->
                t?.let {
                    if (!it.results.isNullOrEmpty()) {
                        callBack.loadResults(it.results)
                    } else {
                        callBack.noResults()
                    }
                } ?: callBack.noResults()
            }, { t: Throwable? ->
                callBack.onError(t)
            })
        compositeSubscription.add(subscription)
    }

    fun removeSubscriptions() {
        compositeSubscription.unsubscribe()
    }
}