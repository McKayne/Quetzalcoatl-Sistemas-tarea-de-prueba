package com.el_nico.cappasitytesttask.domain.entity.networking.onecall

import com.google.gson.annotations.SerializedName

class OneCallResponse(
    @SerializedName("lat") var latitude: Double,
    @SerializedName("lon") var longitude: Double,
    @SerializedName("timezone") var timezone: String,
    @SerializedName("timezone_offset") var timezoneOffset: Double,
    @SerializedName("current") var current: Current,
    @SerializedName("minutely") var minutely: Array<Minutely>?,
    @SerializedName("hourly") var hourly: Array<Hourly>?,
    @SerializedName("daily") var daily: Array<Daily>?
)