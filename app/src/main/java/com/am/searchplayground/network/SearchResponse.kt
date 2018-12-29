package com.am.searchplayground.network

data class SearchApiContract(
    val predictions: List<Prediction>,
    val status: String
)

data class Prediction(
    val description: String,
    val id: String,
    val matched_substrings: List<MatchedSubstring>,
    val place_id: String,
    val reference: String,
    val structured_formatting: StructuredFormatting,
    val terms: List<Term>,
    val types: List<String>
)

data class StructuredFormatting(
    val main_text: String,
    val main_text_matched_substrings: List<MainTextMatchedSubstring>,
    val secondary_text: String
)

data class MainTextMatchedSubstring(
    val length: Int,
    val offset: Int
)

data class Term(
    val offset: Int,
    val value: String
)

data class MatchedSubstring(
    val length: Int,
    val offset: Int
)