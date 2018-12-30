package com.am.searchplayground.network

import com.google.gson.annotations.SerializedName

data class SearchPlacesResponse(
    @SerializedName("html_attributions")
    val html_attributions: List<Any>,
    @SerializedName("next_page_token")
    val next_page_token: String,
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("status")
    val status: String
)


class Result(
    @SerializedName("formatted_address")
    val formatted_address: String,
    @SerializedName("geometry")
    val geometry: Geometry,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("opening_hours")
    val opening_hours: OpeningHours,
    @SerializedName("photos")
    val photos: List<Photo>,
    @SerializedName("place_id")
    val place_id: String,
    @SerializedName("plus_code")
    val plus_code: PlusCode,
    @SerializedName("price_level")
    val price_level: Int,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("reference")
    val reference: String,
    @SerializedName("types")
    val types: List<String>
)

data class Photo(
    @SerializedName("height")
    val height: Int,
    @SerializedName("html_attributions")
    val html_attributions: List<String>,
    @SerializedName("photo_reference")
    val photo_reference: String,
    @SerializedName("width")
    val width: Int
)

data class PlusCode(
    @SerializedName("compound_code")
    val compound_code: String,
    @SerializedName("global_code")
    val global_code: String
)

data class Geometry(
    @SerializedName("location")
    val location: Location,
    @SerializedName("viewport")
    val viewport: Viewport
)

data class OpeningHours(
    @SerializedName("open_now")
    val open_now: Boolean
)

data class Location(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)

data class Northeast(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)

data class Southwest(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)

data class Viewport(
    @SerializedName("northeast")
    val northeast: Northeast,
    @SerializedName("southwest")
    val southwest: Southwest
)