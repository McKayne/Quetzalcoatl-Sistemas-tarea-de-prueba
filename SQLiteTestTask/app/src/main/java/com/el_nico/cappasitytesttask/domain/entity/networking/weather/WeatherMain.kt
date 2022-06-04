package com.el_nico.cappasitytesttask.domain.entity.networking.weather

import com.google.gson.annotations.SerializedName

class WeatherMain(
    @SerializedName("temp") var temp: Double,
    @SerializedName("feels_like") var feelsLike: Double,
    @SerializedName("temp_min") var tempMin: Double,
    @SerializedName("temp_max") var tempMax: Double,
    @SerializedName("pressure") var pressure: Int,
    @SerializedName("humidity") var humidity: Int,
    @SerializedName("sea_level") var seaLevel: Int,
    @SerializedName("grnd_level") var groundLevel: Int
)