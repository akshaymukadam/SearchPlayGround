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

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var searchApi: SearchApi
    private val compositeSubscription = CompositeSubscription()
    private val searchRepository: SearchRepository by lazy { SearchRepository(searchApi) }


    val searchLiveData = MutableLiveData<SearchFlow>()

    init {
        searchLiveData.postValue(
            SearchFlow.EmptyState(
                getApplication<Application>().getString(R.string.enter_keywords),
                getApplication<Application>().getString(R.string.results_will_be_displayed_here),
                R.drawable.search_no_internet
            )
        )
    }

    fun fetchSearchResults(keyWords: CharSequence) {
        val subscription: Subscription =
            Observable.just(keyWords)
                .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
                .filter { t -> t.length >= CHAR_LIMIT }
                .subscribe {
                    fetchSuggestions(it)
                }
        compositeSubscription.add(subscription)
    }

    private fun fetchSuggestions(it: CharSequence) {
        searchRepository.fetchSuggestions(
            input = it.toString(),
            callBack = object : NetworkContract<List<Prediction>> {
                override fun showProgress() {
                    searchLiveData.postValue(SearchFlow.ProgressState)
                }

                override fun noResults() {
                    searchLiveData.postValue(
                        SearchFlow.EmptyState(
                            getApplication<Application>().getString(R.string.search_no_results_title),
                            getApplication<Application>().getString(R.string.search_no_results_body),
                            R.drawable.search_no_internet
                        )
                    )
                }

                override fun loadResults(list: List<Prediction>) {
                    handleData(list)
                }

                override fun onError(t: Throwable?) {
                    handleOnError(t)
                }

            })
    }

    private fun handleData(list: List<Prediction>) {
        if (!list.isNullOrEmpty()) {
            searchLiveData.postValue(SearchFlow.SearchResults(list))
        } else {
            searchLiveData.postValue(
                SearchFlow.EmptyState(
                    getApplication<Application>().getString(R.string.search_no_results_title),
                    getApplication<Application>().getString(R.string.search_no_results_body),
                    R.drawable.search_no_internet
                )
            )
        }
    }

    private fun handleOnError(t: Throwable?) {
        if (t != null && t is UnknownHostException) {
            searchLiveData.postValue(
                SearchFlow.ErrorState(
                    getApplication<Application>().getString(R.string.search_no_internet_available_title),
                    getApplication<Application>().getString(R.string.search_no_internet_available_body),
                    R.drawable.search_no_internet
                )
            )
        } else {
            searchLiveData.postValue(
                SearchFlow.ErrorState(
                    getApplication<Application>().getString(R.string.search_server_error_title),
                    getApplication<Application>().getString(R.string.search_server_error_body),
                    R.drawable.search_no_internet
                )
            )
        }
    }

    fun onDestroy() {
        compositeSubscription.clear()
        searchRepository.removeSubscriptions()
    }

}