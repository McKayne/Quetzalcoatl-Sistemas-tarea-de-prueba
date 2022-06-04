package com.el_nico.cappasitytesttask.domain.entity.networking.weather

import com.google.gson.annotations.SerializedName

class Coordinate(
    @SerializedName("lat") var latitude: Double,
    @SerializedName("lon") var longitude: Double
)