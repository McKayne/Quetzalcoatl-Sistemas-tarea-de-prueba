package com.el_nico.cappasitytesttask.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class WeatherDetailsType(@JsonValue val typeName: String) {
    WEATHER("typeWeather"), FORECAST("typeForecast"), ONECALL("typeOneCall")
}