package com.el_nico.cappasitytesttask.domain.entity.networking.weather

import com.google.gson.annotations.SerializedName

class Weather(
    @SerializedName("id") var id: Int,
    @SerializedName("main") var main: String,
    @SerializedName("description") var description: String,
    @SerializedName("icon") var icon: String
)