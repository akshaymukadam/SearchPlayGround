package com.am.searchplayground.model

import com.am.searchplayground.network.SearchPlacesResult

sealed class SearchFlow {


	object ProgressState : SearchFlow()

	object EmptyState : SearchFlow()


	/**
	 * This can be same but would be better if needed to pass any function
	 */
	class ErrorState(
		val errorState: com.am.searchplayground.model.ErrorState,
		val txtTitle: String? = null,
		val txtBody: String? = null
	) : SearchFlow()


	class SearchResults(val results: List<SearchPlacesResult>) : SearchFlow()

	class RecentSearchResults(val list: List<String>) : SearchFlow()
}

interface NetworkContract<T> {

	fun showProgress()

	fun noResults()

	fun loadResults(data: T)

	fun onError(t: Throwable?)
}