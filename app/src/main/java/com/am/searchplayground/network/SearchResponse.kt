package com.am.searchplayground.network

import com.google.gson.annotations.SerializedName

data class SearchApiContract(
    @SerializedName("predictions")
    val predictions: List<Prediction>,
    @SerializedName("status")
    val status: String
)

data class Prediction(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("matched_substrings")
    val matched_substrings: List<MatchedSubstring>,
    @SerializedName("place_id")
    val place_id: String,
    @SerializedName("reference")
    val reference: String,
    @SerializedName("structured_formatting")
    val structured_formatting: StructuredFormatting,
    @SerializedName("terms")
    val terms: List<Term>,
    @SerializedName("types")
    val types: List<String>
)

data class StructuredFormatting(
    @SerializedName("main_text")
    val main_text: String,
    @SerializedName("main_text_matched_substrings")
    val main_text_matched_substrings: List<MainTextMatchedSubstring>,
    @SerializedName("secondary_text")
    val secondary_text: String
)

data class MainTextMatchedSubstring(

    @SerializedName("length")
    val length: Int,
    @SerializedName("offset")
    val offset: Int
)

data class Term(
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("value")
    val value: String
)

data class MatchedSubstring(
    @SerializedName("length")
    val length: Int,
    @SerializedName("offset")
    val offset: Int
)