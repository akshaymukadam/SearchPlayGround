package com.am.searchplayground.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.am.searchplayground.R
import com.am.searchplayground.network.Prediction
import kotlinx.android.synthetic.main.row_search_suggestions.view.*


class SearchSuggestionsAdapter(private val list: MutableList<Prediction>) :
    RecyclerView.Adapter<SearchSuggestionsAdapter.SearchSuggestionHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): SearchSuggestionHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_search_suggestions, viewGroup, false)
        return SearchSuggestionHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SearchSuggestionHolder, position: Int) {
        holder.bind(list[position])
    }

    fun updateList(results: List<Prediction>) {
        list.addAll(results)
        notifyDataSetChanged()

    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }


    inner class SearchSuggestionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(prediction: Prediction) {
            itemView.txtTitle.text = prediction.structured_formatting.main_text
            itemView.txtDescription.text = prediction.description
        }
    }
}