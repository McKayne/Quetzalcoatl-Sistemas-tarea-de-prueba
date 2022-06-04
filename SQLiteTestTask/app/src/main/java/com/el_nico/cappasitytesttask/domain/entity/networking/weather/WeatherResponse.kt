package com.el_nico.cappasitytesttask.domain.entity.networking.weather

import com.google.gson.annotations.SerializedName

class WeatherResponse(
    @SerializedName("coord") var coordinate: Coordinate?,
    @SerializedName("weather") var weather: Array<Weather>,
    @SerializedName("base") var base: String,
    @SerializedName("main") var main: WeatherMain,
    @SerializedName("visibility") var visibility: Int,
    @SerializedName("wind") var wind: Wind,
    @SerializedName("clouds") var clouds: Clouds,
    @SerializedName("dt") var dt: Int,
    @SerializedName("sys") var sys: SunInfo,
    @SerializedName("timezone") var timezone: Int,
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("cod") var cod: Int
)