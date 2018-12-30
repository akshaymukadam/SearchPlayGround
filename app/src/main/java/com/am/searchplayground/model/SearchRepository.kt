package com.am.searchplayground.model

import com.am.searchplayground.network.SearchApi
import com.am.searchplayground.network.SearchApiContract
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription


class SearchRepository(private val searchApi: SearchApi) {

    private val compositeSubscription: CompositeSubscription = CompositeSubscription()

    fun fetchSuggestions(input: String, callBack: NetworkContract) {
        callBack.showProgress()
        val subscription = searchApi.getSearchResults(keywords = input)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ t: SearchApiContract? ->
                t?.let {
                    if (!it.predictions.isNullOrEmpty()) {
                        callBack.loadResults(it.predictions)
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