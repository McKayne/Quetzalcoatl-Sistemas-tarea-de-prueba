package com.el_nico.cappasitytesttask.domain.entity.networking.weather

import com.google.gson.annotations.SerializedName

class SunInfo(
    @SerializedName("country") var country: String,
    @SerializedName("sunrise") var sunrise: Int,
    @SerializedName("sunset") var sunset: Int,
)