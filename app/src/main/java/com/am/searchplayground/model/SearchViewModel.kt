package com.am.searchplayground.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.am.searchplayground.network.SearchApi
import rx.subscriptions.CompositeSubscription

class SearchViewModel : ViewModel() {

	lateinit var searchApi: SearchApi
	private val compositeSubscription = CompositeSubscription()
	private val searchRepository: SearchRepository by lazy { SearchRepository(searchApi) }
	private var apiLiveDta: LiveData<ApiResponse> = MutableLiveData<ApiResponse>()

	private val searchLiveData = MutableLiveData<SearchFlow>()

	val resultsLiveData = MediatorLiveData<SearchFlow>()

	init {
		searchLiveData.postValue(
			SearchFlow.EmptyState
		)
		resultsLiveData.addSource(searchLiveData) { resultsLiveData.value = it }
	}

	fun fetchSearchResults(keyWords: CharSequence) {
		fetchSuggestions(keyWords)
	}

	private fun fetchSuggestions(it: CharSequence) {

		searchLiveData.postValue(SearchFlow.ProgressState)
		apiLiveDta = searchRepository.fetchSuggestions(it.replace(Regex("\\s+"), "+"))

		resultsLiveData.addSource(apiLiveDta) { it ->
			when (it) {
				is ApiResponse.ApiSuccess -> {
					it.data?.let {
						if (!it.isEmpty()) {
							resultsLiveData.value = SearchFlow.SearchResults(it)
						} else {
							resultsLiveData.value = SearchFlow.EmptyState
						}
					} ?: SearchFlow.EmptyState
				}
				is ApiResponse.ApiError -> {
					resultsLiveData.value = SearchFlow.ErrorState(it.errorState)
				}
			}
		}
	}

	fun onScrolled(
		keyWords: CharSequence,
		visibleItemCount: Int,
		totalItemCount: Int,
		firstVisibleItemPosition: Int
	) {
		if (!searchRepository.nextPageToken.isNullOrEmpty()
			&& (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
			&& firstVisibleItemPosition >= 0 && resultsLiveData.value !is SearchFlow.ProgressState
		) {
			fetchSuggestions(keyWords)
		}
	}

	fun onDestroy() {
		compositeSubscription.clear()
		searchRepository.removeSubscriptions()
	}

	fun updateViewStates(
		isListEmpty: Boolean, paginationView: () -> Unit,
		fullScreenView: () -> Unit
	) {
		if (isListEmpty) {
			fullScreenView.invoke()
		} else {
			paginationView.invoke()
		}
	}

}