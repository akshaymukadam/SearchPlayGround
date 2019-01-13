package com.am.searchplayground.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.am.searchplayground.network.SearchApi
import com.am.searchplayground.network.SearchPlacesResponse
import com.am.searchplayground.network.SearchPlacesResult
import retrofit2.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.net.UnknownHostException


class SearchRepository(private val searchApi: SearchApi) {

	private val compositeSubscription: CompositeSubscription = CompositeSubscription()
	var nextPageToken: String? = null
		private set

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


	fun fetchSuggestions(input: String): LiveData<ApiResponse> {
		val data = MutableLiveData<ApiResponse>()
		val subscription = searchApi.getTextSearchResults(query = input).subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread()).subscribe({ it ->
				nextPageToken = it.next_page_token
				it?.let {
					if (!it.results.isNullOrEmpty()) {
						data.postValue(ApiResponse.ApiSuccess(it.results))
					} else {
						data.postValue(ApiResponse.ApiSuccess(it.results))
					}
				} ?: ApiResponse.ApiSuccess()

			}, {
				when (it) {
					is UnknownHostException -> data.postValue(ApiResponse.ApiError(ErrorState.INTERNET))
					is HttpException -> data.postValue(ApiResponse.ApiError(ErrorState.SERVER, "321", "Random Error"))
					else -> data.postValue(ApiResponse.ApiError(ErrorState.UNKNOWN))
				}
			})
		compositeSubscription.add(subscription)
		return data
	}

	fun removeSubscriptions() {
		compositeSubscription.unsubscribe()
	}
}

sealed class ApiResponse {
	class ApiSuccess(val data: List<SearchPlacesResult>? = null) : ApiResponse()

	class ApiError(val errorState: ErrorState, val code: String? = null, val data: String? = null) : ApiResponse()
}

enum class ErrorState {

	UNKNOWN,
	SERVER,
	INTERNET
}