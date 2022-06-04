package com.el_nico.cappasitytesttask.domain.entity.networking.onecall

import com.el_nico.cappasitytesttask.domain.entity.networking.weather.Weather
import com.google.gson.annotations.SerializedName

class Daily(
    @SerializedName("dt") var dt: Int,
    @SerializedName("sunrise") var sunrise: Int,
    @SerializedName("sunset") var sunset: Int,
    @SerializedName("moonrise") var moonrise: Int,
    @SerializedName("moonset") var moonset: Int,
    @SerializedName("moon_phase") var moonPhase: Double,
    @SerializedName("pressure") var pressure: Int,
    @SerializedName("humidity") var humidity: Int,
    @SerializedName("dew_point") var dewPoint: Double,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_deg") var windDeg: Int,
    @SerializedName("wind_gust") var windGust: Double,
    @SerializedName("weather") var weather: Array<Weather>,
    @SerializedName("clouds") var clouds: Int,
    @SerializedName("pop") var pop: Double,
    @SerializedName("uvi") var uvi: Double,
    @SerializedName("temp") var temp: Temp,
    @SerializedName("feels_like") var feelsLike: Temp
)