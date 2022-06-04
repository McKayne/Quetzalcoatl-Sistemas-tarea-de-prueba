package com.el_nico.cappasitytesttask.domain.entity.database

class WeatherQueryResult(
    val visibility: Int,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val seaLevel: Int,
    val groundLevel: Int,
    var windSpeed: Double,
    var windDeg: Double
)