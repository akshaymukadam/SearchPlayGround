package com.am.searchplayground.model

import com.am.searchplayground.network.Prediction

sealed class SearchFlow {


    object ProgressState : SearchFlow()

    class EmptyState(val txtTitle: String, val txtBody: String, val imageRes: Int) : SearchFlow()


    /**
     * This can be same but would be better if needed to pass any function
     */
    class ErrorState(val txtTitle: String, val txtBody: String, val imageRes: Int) : SearchFlow()


    class SearchResults(val results: List<Prediction>) : SearchFlow()

    class RecentSearchResults(val list: List<String>) : SearchFlow()
}

interface NetworkContract {

    fun showProgress()

    fun noResults()

    fun loadResults(list: List<Prediction>)

    fun onError(t: Throwable?)
}