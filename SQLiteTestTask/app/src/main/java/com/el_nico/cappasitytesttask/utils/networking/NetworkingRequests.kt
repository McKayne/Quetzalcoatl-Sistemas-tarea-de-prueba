package com.el_nico.cappasitytesttask.utils.networking

import com.el_nico.cappasitytesttask.domain.entity.networking.forecast.ForecastResponse
import com.el_nico.cappasitytesttask.domain.entity.networking.onecall.OneCallResponse
import com.el_nico.cappasitytesttask.domain.entity.networking.weather.WeatherResponse
import io.reactivex.rxjava3.core.Single

fun Networking.Companion.weather(city: String): Single<WeatherResponse> {
    val apiKey = Networking.apiKey
    return if (apiKey is String) {
        requestType.weather(city, apiKey, "metric", "ru").flatMap {
            val responseBody = it.body()
            if (responseBody is WeatherResponse) {
                Single.just(responseBody)
            } else {
                Single.error(Throwable(it.errorBody()?.string()))
            }
        }
    } else {
        Single.error(Throwable("No hay API key"))
    }
}

fun Networking.Companion.forecast(city: String): Single<ForecastResponse> {
    val apiKey = Networking.apiKey
    return if (apiKey is String) {
        requestType.forecast(city, apiKey, "metric", "ru").flatMap {
            val responseBody = it.body()
            if (responseBody is ForecastResponse) {
                Single.just(responseBody)
            } else {
                Single.error(Throwable(it.errorBody()?.string()))
            }
        }
    } else {
        Single.error(Throwable("No hay API key"))
    }
}

fun Networking.Companion.oneCall(latitude: Double, longitude: Double): Single<OneCallResponse> {
    val apiKey = Networking.apiKey
    return if (apiKey is String) {
        requestType.onecall(latitude, longitude, apiKey, "metric", "ru").flatMap {
            val responseBody = it.body()
            if (responseBody is OneCallResponse) {
                Single.just(responseBody)
            } else {
                Single.error(Throwable(it.errorBody()?.string()))
            }
        }
    } else {
        Single.error(Throwable("No hay API key"))
    }
}