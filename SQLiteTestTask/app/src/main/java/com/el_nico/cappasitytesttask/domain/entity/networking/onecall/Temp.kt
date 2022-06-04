package com.el_nico.cappasitytesttask.domain.entity.networking.onecall

import com.google.gson.annotations.SerializedName

class Temp(
    @SerializedName("day") var day: Double,
    @SerializedName("min") var min: Double,
    @SerializedName("max") var max: Double,
    @SerializedName("night") var night: Double,
    @SerializedName("eve") var eve: Double,
    @SerializedName("morn") var morn: Double
)