package com.el_nico.cappasitytesttask.utils.database.weatherqueries

import android.database.Cursor
import com.el_nico.cappasitytesttask.domain.entity.database.ForecastQueryResult
import com.el_nico.cappasitytesttask.domain.entity.networking.forecast.ForecastResponse
import com.el_nico.cappasitytesttask.utils.database.WeatherDatabase
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.Executors

fun WeatherDatabase.Companion.updateForecastForCityWithID(
    id: Int, forecast: ForecastResponse
): Single<Boolean> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()

            weatherDatabase.execSQL(
                "DELETE FROM FORECAST WHERE CITY_ID = '$id'"
            )

            weatherDatabase.execSQL(
                "INSERT INTO FORECAST(" +
                        "CITY_ID, COD, MESSAGE, CNT) VALUES($id, '${forecast.cod}', " +
                        "${forecast.message}, ${forecast.count})"
            )

            ///

            var weatherID: Int? = null

            val query = "SELECT ID FROM FORECAST WHERE CITY_ID = '$id' ORDER BY ID DESC LIMIT 1"
            val cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.moveToFirst()) {
                weatherID = cursor.getInt(0)
            }

            cursor.close()

            if (weatherID != null) {
                weatherDatabase.execSQL(
                    "INSERT INTO CITY(WEATHER_ID, NAME, COORD_LAT, COORD_LON, COUNTRY, " +
                            "POPULATION, TIMEZONE, SUNRISE, SUNSET) VALUES($weatherID, " +
                            "'${forecast.city.name}', " +
                            "${forecast.city.coord.latitude}, ${forecast.city.coord.longitude}, " +
                            "'${forecast.city.country}', ${forecast.city.population}, " +
                            "${forecast.city.timezone}, ${forecast.city.sunrise}, ${forecast.city.sunset})"
                )

                for (response in forecast.list) {
                    weatherDatabase.execSQL(
                        "INSERT INTO FORECAST_WEATHER(WEATHER_ID, VISIBILITY, BASE, DT, " +
                                "TIMEZONE, NAME, COD, COORD_LAT, COORD_LON, CLOUDS_ALL) VALUES($weatherID, " +
                                "${response.visibility}, '${response.base}', ${response.dt}, " +
                                "${response.timezone}, '${response.name}', ${response.cod}, " +
                                "${response.coordinate?.latitude ?: 0}, ${response.coordinate?.longitude ?: 0}, " +
                                "${response.clouds.all})"
                    )

                    var forecastID: Int? = null

                    val forecastQuery = "SELECT ID FROM FORECAST_WEATHER WHERE WEATHER_ID = '$weatherID' ORDER BY ID DESC LIMIT 1"
                    val forecastCursor = weatherDatabase.rawQuery(forecastQuery, null)

                    if (forecastCursor is Cursor && forecastCursor.moveToFirst()) {
                        forecastID = forecastCursor.getInt(0)
                    }

                    forecastCursor.close()

                    if (forecastID != null) {
                        weatherDatabase.execSQL(
                            "INSERT INTO FORECAST_WIND(WEATHER_ID, SPEED, DEG, GUST) VALUES($forecastID, " +
                                    "${response.wind.speed}, '${response.wind.deg}', ${response.wind.gust})"
                        )

                        weatherDatabase.execSQL(
                            "INSERT INTO FORECAST_SUN_INFO(WEATHER_ID, COUNTRY, SUNRISE, SUNSET) VALUES($forecastID, " +
                                    "'${response.sys.country}', ${response.sys.sunrise}, ${response.sys.sunset})"
                        )

                        weatherDatabase.execSQL(
                            "INSERT INTO FORECAST_WEATHER_MAIN(WEATHER_ID, " +
                                    "TEMPERATURE, FEELS_LIKE, TEMP_MIN, TEMP_MAX, PRESSURE, HUMIDITY, " +
                                    "SEA_LEVEL, GROUND_LEVEL) VALUES($forecastID, " +
                                    "${response.main.temp}, ${response.main.feelsLike}, " +
                                    "${response.main.tempMin}, ${response.main.tempMax}, " +
                                    "${response.main.pressure}, ${response.main.humidity}, " +
                                    "${response.main.seaLevel}, ${response.main.groundLevel})"
                        )

                        for (weatherValue in response.weather) {
                            weatherDatabase.execSQL(
                                "INSERT INTO FORECAST_WEATHER_LIST(WEATHER_ID, " +
                                        "MAIN, DESCRIPTION, ICON) VALUES($forecastID, " +
                                        "'${weatherValue.main}', '${weatherValue.description}', '${weatherValue.icon}')"
                            )
                        }
                    }
                }
            }

            dbSemaphore.release()
            it.onSuccess(true)
        }
    }
}

fun WeatherDatabase.Companion.forecastForCityWithID(
    id: Int
): Single<Collection<ForecastQueryResult>> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()

            var weatherID = 0

            val forecastResult = ArrayList<ForecastQueryResult>()

            var query = "SELECT ID FROM FORECAST WHERE CITY_ID = '$id' ORDER BY ID DESC LIMIT 1"
            var cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.moveToFirst()) {
                weatherID = cursor.getInt(0)
            }

            cursor.close()

            ///

            query = "SELECT ID, DT FROM FORECAST_WEATHER WHERE WEATHER_ID = '$weatherID'"
            cursor = weatherDatabase.rawQuery(query, null)

            val forecastList = ArrayList<Pair<Int, String>>()
            if (cursor is Cursor) {
                while (cursor.moveToNext()) {
                    val forecastID = cursor.getInt(0)
                    val dt = cursor.getString(1)
                    forecastList.add(Pair(forecastID, dt))
                }
            }

            cursor.close()

            //

            for (pair in forecastList) {
                query = "SELECT TEMPERATURE, FEELS_LIKE, TEMP_MIN, TEMP_MAX, PRESSURE, HUMIDITY, " +
                        "SEA_LEVEL, GROUND_LEVEL FROM FORECAST_WEATHER_MAIN WHERE WEATHER_ID = '${pair.first}'"
                cursor = weatherDatabase.rawQuery(query, null)

                if (cursor is Cursor) {
                    while (cursor.moveToNext()) {
                        val temperature = cursor.getDouble(0)
                        val feelsLike = cursor.getDouble(1)
                        val tempMin = cursor.getDouble(2)
                        val tempMax = cursor.getDouble(3)
                        val pressure = cursor.getInt(4)
                        val humidity = cursor.getInt(5)

                        forecastResult.add(ForecastQueryResult(
                            pair.second, temperature, feelsLike, tempMin, tempMax,
                            pressure, humidity
                        ))
                    }
                }

                cursor.close()
            }

            dbSemaphore.release()
            it.onSuccess(forecastResult)
        }
    }
}