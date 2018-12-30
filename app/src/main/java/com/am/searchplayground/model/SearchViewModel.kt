package com.am.searchplayground.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.am.searchplayground.CHAR_LIMIT
import com.am.searchplayground.DEBOUNCE_TIME
import com.am.searchplayground.R
import com.am.searchplayground.network.Prediction
import com.am.searchplayground.network.SearchApi
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class SearchViewModel(application: Application) : AndroidViewModel(application), NetworkContract {

    lateinit var searchApi: SearchApi
    private val compositeSubscription = CompositeSubscription()
    private val searchRepository: SearchRepository by lazy { SearchRepository(searchApi) }


    val searchLiveData = MutableLiveData<SearchFlow>()
    fun fetchSearchResults(keyWords: CharSequence) {
        val subscription: Subscription =
            Observable.just(keyWords)
                .filter { t -> t.length >= CHAR_LIMIT }
                .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
                .subscribe {
                    searchRepository.fetchSuggestions(input = it.toString(), callBack = this)
                }
        compositeSubscription.add(subscription)
    }

    override fun showProgress() {
        searchLiveData.postValue(SearchFlow.ProgressState)
    }

    override fun noResults() {
        searchLiveData.postValue(
            SearchFlow.EmptyState(
                getApplication<Application>().getString(R.string.search_no_results_title),
                getApplication<Application>().getString(R.string.search_no_results_body),
                0
            )
        )
    }

    override fun loadResults(list: List<Prediction>) {
        if (!list.isNullOrEmpty()) {
            searchLiveData.postValue(SearchFlow.SearchResults(list))
        } else {
            searchLiveData.postValue(
                SearchFlow.EmptyState(
                    getApplication<Application>().getString(R.string.search_no_results_title),
                    getApplication<Application>().getString(R.string.search_no_results_body),
                    0
                )
            )
        }
    }

    override fun onError(t: Throwable?) {
        if (t != null && t is UnknownHostException) {
            searchLiveData.postValue(
                SearchFlow.ErrorState(
                    getApplication<Application>().getString(R.string.search_no_internet_available_title),
                    getApplication<Application>().getString(R.string.search_no_internet_available_body),
                    0
                )
            )
        } else {
            searchLiveData.postValue(
                SearchFlow.ErrorState(
                    getApplication<Application>().getString(R.string.search_server_error_title),
                    getApplication<Application>().getString(R.string.search_server_error_body),
                    0
                )
            )
        }
    }

    fun onDestroy() {
        compositeSubscription.clear()
        searchRepository.removeSubscriptions()
    }

}