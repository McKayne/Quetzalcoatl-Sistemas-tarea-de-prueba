package com.el_nico.cappasitytesttask.interfaces.networking

import com.el_nico.cappasitytesttask.domain.entity.networking.forecast.ForecastResponse
import com.el_nico.cappasitytesttask.domain.entity.networking.onecall.OneCallResponse
import com.el_nico.cappasitytesttask.domain.entity.networking.weather.WeatherResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RequestType {

    @GET("weather")
    fun weather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): Single<Response<WeatherResponse>>

    @GET("forecast")
    fun forecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): Single<Response<ForecastResponse>>

    @GET("onecall")
    fun onecall(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): Single<Response<OneCallResponse>>
}