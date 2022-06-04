package com.el_nico.cappasitytesttask.domain.entity.networking.forecast

import com.el_nico.cappasitytesttask.domain.entity.networking.weather.WeatherResponse
import com.google.gson.annotations.SerializedName

class ForecastResponse(
    @SerializedName("cod") var cod: String,
    @SerializedName("message") var message: Int,
    @SerializedName("cnt") var count: Int,
    @SerializedName("city") var city: City,
    @SerializedName("list") var list: Array<WeatherResponse>
)