package com.el_nico.cappasitytesttask.domain.entity.networking.onecall

import com.google.gson.annotations.SerializedName

class Minutely(
    @SerializedName("dt") var dt: Int,
    @SerializedName("precipitation") var precipitation: Double
)