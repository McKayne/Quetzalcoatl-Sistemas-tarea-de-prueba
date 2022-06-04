package com.el_nico.cappasitytesttask.domain.entity.networking.forecast

import com.el_nico.cappasitytesttask.domain.entity.networking.weather.Coordinate
import com.google.gson.annotations.SerializedName

class City(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("coord") var coord: Coordinate,
    @SerializedName("country") var country: String,
    @SerializedName("population") var population: Int,
    @SerializedName("timezone") var timezone: Int,
    @SerializedName("sunrise") var sunrise: Int,
    @SerializedName("sunset") var sunset: Int
)