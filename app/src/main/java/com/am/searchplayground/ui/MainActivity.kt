package com.am.searchplayground.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.am.searchplayground.R
import com.am.searchplayground.SearchApp
import com.am.searchplayground.model.SearchFlow
import com.am.searchplayground.model.SearchViewModel
import com.am.searchplayground.network.SearchApi
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.activity_main.*
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

	@Inject
	lateinit var searchApi: SearchApi
	lateinit var searchViewModel: SearchViewModel
	lateinit var searchSuggestionsAdapter: SearchSuggestionsAdapter
	lateinit var linearLayoutManager: LinearLayoutManager
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		(application as SearchApp).getSearchComponent().inject(this)
		setContentView(R.layout.activity_main)
		searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
		searchViewModel.searchApi = searchApi
		searchSuggestionsAdapter = SearchSuggestionsAdapter(mutableListOf())
		linearLayoutManager = LinearLayoutManager(this)
		rvSuggestions.layoutManager = linearLayoutManager
		rvSuggestions.adapter = searchSuggestionsAdapter

		setTextChangeListener()
		observeSearchSuggestion()
		setScrollListener()
	}

	private fun setTextChangeListener() {
		RxTextView.textChanges(inputSearch)
			.skip(1)
			.debounce(3, TimeUnit.SECONDS)
			.filter { t: CharSequence ->
				t.trim().length > 3
			}
			.observeOn(AndroidSchedulers.mainThread()).subscribe({ t: CharSequence? ->
				if (t != null) {
					searchViewModel.fetchSearchResults(t)
				}
			}, { t: Throwable? -> t?.printStackTrace() })
	}

	private fun observeSearchSuggestion() {
		searchViewModel.resultsLiveData.observe(this, Observer { t: SearchFlow? ->
			t?.let {
				when (it) {
					is SearchFlow.ProgressState -> {
						updateProgressState()
					}
					is SearchFlow.ErrorState -> {
						updateErrorStates(it)
					}
					is SearchFlow.EmptyState -> {
						searchSuggestionsAdapter.removePaginationErrorProgress()

						updateEmptyState(it)
					}
					is SearchFlow.SearchResults -> {
						searchSuggestionsAdapter.removePaginationErrorProgress()
						updateSearchResults(it)
					}
					is SearchFlow.RecentSearchResults -> {
						updateRecentSearchResults()
					}
				}
			}
		})
	}

	private fun removeScrollListener() {
		rvSuggestions.removeOnScrollListener(scrollListener)
	}

	private fun updateRecentSearchResults() {
		updateViewVisibilty(groupOnBoarding, View.GONE)
		updateViewVisibilty(progressBar, View.GONE)
		updateViewVisibilty(rvSuggestions, View.GONE)
		updateViewVisibilty(groupError, View.GONE)
	}

	private fun updateSearchResults(it: SearchFlow.SearchResults) {
		searchSuggestionsAdapter.notifyAdapter(it.results)
		updateViewVisibilty(groupOnBoarding, View.GONE)
		updateViewVisibilty(progressBar, View.GONE)
		updateViewVisibilty(rvSuggestions, View.VISIBLE)
		updateViewVisibilty(groupError, View.GONE)
		setScrollListener()
	}

	private fun setScrollListener() {
		rvSuggestions.addOnScrollListener(scrollListener)
	}

	private fun updateEmptyState(it: SearchFlow.EmptyState) {
		searchViewModel.updateViewStates(
			searchSuggestionsAdapter.isAdapterEmpty(),
			paginationView = {},
			fullScreenView = {
				imgOnBoarding.setImageResource(R.drawable.search_no_internet)
				txtOnBoardingBody.text = getString(R.string.enter_keywords)
				txtOnBoardingTitle.text = getString(R.string.results_will_be_displayed_here)
				updateViewVisibilty(groupOnBoarding, View.VISIBLE)
				updateViewVisibilty(progressBar, View.GONE)
				updateViewVisibilty(rvSuggestions, View.GONE)
				updateViewVisibilty(groupError, View.GONE)
			})
	}

	private fun updateErrorStates(it: SearchFlow.ErrorState) {
		searchViewModel.updateViewStates(
			isListEmpty = searchSuggestionsAdapter.isAdapterEmpty(),
			paginationView = { searchSuggestionsAdapter.showPaginationError() }, fullScreenView = {
				txtErrorTitle.text = getString(R.string.search_no_results_title)
				txtErrorBody.text = getString(R.string.search_no_results_body)
				imgError.setImageResource(R.drawable.search_no_internet)
				updateViewVisibilty(groupOnBoarding, View.GONE)
				updateViewVisibilty(progressBar, View.GONE)
				updateViewVisibilty(rvSuggestions, View.GONE)
				updateViewVisibilty(groupError, View.VISIBLE)
			})
	}

	private fun updateProgressState() {
		removeScrollListener()
//		searchSuggestionsAdapter.removePaginationErrorProgress()
		searchViewModel.updateViewStates(
			searchSuggestionsAdapter.isAdapterEmpty(),
			paginationView = {
				searchSuggestionsAdapter.showPaginationProgress()
				rvSuggestions.scrollToPosition(searchSuggestionsAdapter.itemCount - 1)
			},
			fullScreenView = {
				Single.just("").delay(50, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {
					val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
					inputMethodManager.hideSoftInputFromWindow(inputSearch.windowToken, 0)
				}
				updateViewVisibilty(groupOnBoarding, View.GONE)
				updateViewVisibilty(progressBar, View.VISIBLE)
				updateViewVisibilty(rvSuggestions, View.GONE)
				updateViewVisibilty(groupError, View.GONE)
			})
	}


	private fun updateViewVisibilty(view: View, visible: Int) {
		view.visibility = visible
	}

	private val scrollListener = object : RecyclerView.OnScrollListener() {

		override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
			super.onScrollStateChanged(recyclerView, newState)

			val visibleItemCount = linearLayoutManager.childCount
			val totalItemCount = linearLayoutManager.itemCount
			val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
			searchViewModel.onScrolled(inputSearch.text, visibleItemCount, totalItemCount, firstVisibleItemPosition)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		searchViewModel.onDestroy()
	}
}
