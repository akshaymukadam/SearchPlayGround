package com.am.searchplayground.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.am.searchplayground.R
import com.am.searchplayground.ROW_ITEM
import com.am.searchplayground.ROW_PAGINATION_ERROR
import com.am.searchplayground.ROW_PAGINATION_PROGRESS
import com.am.searchplayground.network.SearchPlacesResult
import kotlinx.android.synthetic.main.row_pagination_error.view.*
import kotlinx.android.synthetic.main.row_search_suggestions.view.*


class SearchSuggestionsAdapter(private val searchList: MutableList<SearchPlacesResult>) :
	RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		when (viewType) {
			ROW_ITEM -> {
				val view =
					LayoutInflater.from(viewGroup.context).inflate(R.layout.row_search_suggestions, viewGroup, false)
				return SearchSuggestionHolder(view)
			}
			ROW_PAGINATION_PROGRESS -> {
				val view =
					LayoutInflater.from(viewGroup.context).inflate(R.layout.row_pagination_progress, viewGroup, false)
				return SearchPaginationProgressHolder(view)
			}
			else -> {
				val view =
					LayoutInflater.from(viewGroup.context).inflate(R.layout.row_pagination_error, viewGroup, false)
				return SearchPaginationErrorHolder(view)
			}
		}

	}

	override fun getItemCount(): Int = searchList.size

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is SearchSuggestionHolder)
			holder.bind(searchList[position])
		else if (holder is SearchPaginationErrorHolder) {
			holder.bind()
		} else if (holder is SearchPaginationProgressHolder) {

		}
	}

	fun clear() {
		searchList.clear()
		notifyDataSetChanged()
	}

	override fun getItemViewType(position: Int): Int {
		return if (searchList.isEmpty())
			super.getItemViewType(position)
		else
			searchList[position].rowType
	}

	fun isAdapterEmpty() = searchList.isEmpty()

	fun notifyAdapter(list: List<SearchPlacesResult>) {
		if (isAdapterEmpty()) {
			setVoucherList(list)
		} else {
			updateVoucherList(list)
		}
	}

	private fun updateVoucherList(list: List<SearchPlacesResult>) {
		val insertPosition: Int = searchList.size
		searchList.addAll(list)
		notifyItemRangeChanged(insertPosition, searchList.size)
	}

	private fun setVoucherList(list: List<SearchPlacesResult>) {
		searchList.addAll(list)
		notifyDataSetChanged()
	}

	fun showPaginationProgress() {
		searchList.add(SearchPlacesResult(ROW_PAGINATION_PROGRESS))
		notifyItemInserted(searchList.size - 1)
	}

	fun showPaginationError() {
		searchList.add(SearchPlacesResult(ROW_PAGINATION_ERROR))
		notifyItemInserted(searchList.size - 1)
	}


	/**
	 * This would be a repeated code for both removal of pagination or progress, hence this odd name
	 */
	fun removePaginationErrorProgress() {
		if (!isAdapterEmpty()) {
			val type = searchList[searchList.size - 1].rowType
			if (type == ROW_PAGINATION_PROGRESS || type == ROW_PAGINATION_ERROR) {
				searchList.removeAt(searchList.size - 1)
				notifyItemRemoved(searchList.size)
			}


		}
	}

	inner class SearchSuggestionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		fun bind(results: SearchPlacesResult) {
			itemView.txtTitle.text = results.name
			itemView.txtDescription.text = results.formatted_address
		}
	}

	inner class SearchPaginationProgressHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

	inner class SearchPaginationErrorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		fun bind() {
			itemView.btnRetry.setOnClickListener { }
		}
	}
}