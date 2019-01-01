package com.am.searchplayground.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.am.searchplayground.R
import com.am.searchplayground.SearchApp
import com.am.searchplayground.model.SearchFlow
import com.am.searchplayground.model.SearchViewModel
import com.am.searchplayground.network.SearchApi
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.activity_main.*
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var searchApi: SearchApi
    lateinit var searchViewModel: SearchViewModel
    lateinit var searchSuggestionsAdapter: SearchSuggestionsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as SearchApp).getSearchComponent().inject(this)
        setContentView(R.layout.activity_main)
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        searchViewModel.searchApi = searchApi
        searchSuggestionsAdapter = SearchSuggestionsAdapter(mutableListOf())
        rvSuggestions.layoutManager = LinearLayoutManager(this)
        rvSuggestions.adapter = searchSuggestionsAdapter

        RxTextView.textChanges(inputSearch)
            .skip(1)
            .debounce(3, TimeUnit.SECONDS)
            .filter { t: CharSequence ->
                t.trim().length > 3
            }
            .observeOn(AndroidSchedulers.mainThread()).subscribe({ t: CharSequence? ->
                if (t != null)
                    searchViewModel.fetchSearchResults(t)
            }, { t: Throwable? -> t?.printStackTrace() })
        searchViewModel.searchLiveData.observe(this,
            Observer<SearchFlow> { t ->
                t?.let {
                    when (it) {
                        is SearchFlow.ProgressState -> {
                            updateProgressState()
                        }
                        is SearchFlow.ErrorState -> {
                            updateErrorStates(it)
                        }
                        is SearchFlow.EmptyState -> {
                            updateEmptyState(it)
                        }
                        is SearchFlow.SearchResults -> {
                            updateSearchResults(it)
                        }
                        is SearchFlow.RecentSearchResults -> {
                            updateRecentSearchResults()
                        }
                    }
                }
            })


    }

    private fun updateRecentSearchResults() {
        updateViewVisibiltiy(groupOnBoarding, View.GONE)
        updateViewVisibiltiy(progressBar, View.GONE)
        updateViewVisibiltiy(rvSuggestions, View.GONE)
        updateViewVisibiltiy(groupError, View.GONE)
    }

    private fun updateSearchResults(it: SearchFlow.SearchResults) {
        updateViewVisibiltiy(groupOnBoarding, View.GONE)
        searchSuggestionsAdapter.updateList(it.results)
        updateViewVisibiltiy(progressBar, View.GONE)
        updateViewVisibiltiy(rvSuggestions, View.VISIBLE)
        updateViewVisibiltiy(groupError, View.GONE)
    }

    private fun updateEmptyState(it: SearchFlow.EmptyState) {
        imgOnBoarding.setImageResource(it.imageRes)
        txtOnBoardingBody.text = it.txtBody
        txtOnBoardingTitle.text = it.txtTitle
        updateViewVisibiltiy(groupOnBoarding, View.VISIBLE)
        updateViewVisibiltiy(progressBar, View.GONE)
        updateViewVisibiltiy(rvSuggestions, View.GONE)
        updateViewVisibiltiy(groupError, View.GONE)
    }

    private fun updateErrorStates(it: SearchFlow.ErrorState) {
        txtErrorTitle.text = it.txtTitle
        txtErrorBody.text = it.txtBody
        imgError.setImageResource(it.imageRes)
        updateViewVisibiltiy(groupOnBoarding, View.GONE)
        updateViewVisibiltiy(progressBar, View.GONE)
        updateViewVisibiltiy(rvSuggestions, View.GONE)
        updateViewVisibiltiy(groupError, View.VISIBLE)
    }

    private fun updateProgressState() {
        updateViewVisibiltiy(groupOnBoarding, View.GONE)
        updateViewVisibiltiy(progressBar, View.VISIBLE)
        updateViewVisibiltiy(rvSuggestions, View.GONE)
        updateViewVisibiltiy(groupError, View.GONE)
    }


    private fun updateViewVisibiltiy(view: View, visible: Int) {
        view.visibility = visible
    }

    override fun onDestroy() {
        super.onDestroy()
        searchViewModel.onDestroy()
    }
}
