package com.el_nico.cappasitytesttask.domain.entity.database

class ForecastQueryResult(
    val dt: String,
    val temp: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int
)