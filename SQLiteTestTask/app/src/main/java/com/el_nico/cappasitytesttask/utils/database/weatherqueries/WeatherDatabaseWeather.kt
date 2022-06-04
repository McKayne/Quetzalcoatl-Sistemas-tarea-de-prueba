package com.el_nico.cappasitytesttask.utils.database.weatherqueries

import android.database.Cursor
import com.el_nico.cappasitytesttask.domain.entity.database.WeatherQueryResult
import com.el_nico.cappasitytesttask.domain.entity.networking.weather.WeatherResponse
import com.el_nico.cappasitytesttask.utils.database.WeatherDatabase
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.Executors

fun WeatherDatabase.Companion.updateWeatherForCityWithID(
    id: Int, weather: WeatherResponse
): Single<Boolean> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()

            weatherDatabase.execSQL(
                "DELETE FROM WEATHER WHERE CITY_ID = '$id'"
            )

            weatherDatabase.execSQL(
                "INSERT INTO WEATHER(" +
                        "CITY_ID, VISIBILITY, BASE, DT, TIMEZONE, NAME, COD, " +
                        "COORD_LAT, COORD_LON, CLOUDS_ALL) " +
                        "VALUES($id, ${weather.visibility}, '${weather.base}', ${weather.dt}, " +
                        "${weather.timezone}, '${weather.name}', ${weather.cod}, " +
                        "${weather.coordinate?.latitude ?: 0}, ${weather.coordinate?.longitude ?: 0}, " +
                        "${weather.clouds.all})"
            )

            ///

            var weatherID: Int? = null

            val query = "SELECT ID FROM WEATHER WHERE CITY_ID = '$id' ORDER BY ID DESC LIMIT 1"
            val cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.moveToFirst()) {
                weatherID = cursor.getInt(0)
            }

            cursor.close()

            if (weatherID != null) {
                weatherDatabase.execSQL(
                    "INSERT INTO WIND(WEATHER_ID, SPEED, DEG, GUST) VALUES($weatherID, " +
                            "${weather.wind.speed}, '${weather.wind.deg}', ${weather.wind.gust})"
                )

                weatherDatabase.execSQL(
                    "INSERT INTO SUN_INFO(WEATHER_ID, COUNTRY, SUNRISE, SUNSET) VALUES($weatherID, " +
                            "'${weather.sys.country}', ${weather.sys.sunrise}, ${weather.sys.sunset})"
                )

                weatherDatabase.execSQL(
                    "INSERT INTO WEATHER_MAIN(WEATHER_ID, " +
                            "TEMPERATURE, FEELS_LIKE, TEMP_MIN, TEMP_MAX, PRESSURE, HUMIDITY, " +
                            "SEA_LEVEL, GROUND_LEVEL) VALUES($weatherID, " +
                            "${weather.main.temp}, ${weather.main.feelsLike}, " +
                            "${weather.main.tempMin}, ${weather.main.tempMax}, " +
                            "${weather.main.pressure}, ${weather.main.humidity}, " +
                            "${weather.main.seaLevel}, ${weather.main.groundLevel})"
                )

                for (weatherValue in weather.weather) {
                    weatherDatabase.execSQL(
                        "INSERT INTO WEATHER_LIST(WEATHER_ID, " +
                                "MAIN, DESCRIPTION, ICON) VALUES($weatherID, " +
                                "'${weatherValue.main}', '${weatherValue.description}', '${weatherValue.icon}')"
                    )
                }
            }

            dbSemaphore.release()
            it.onSuccess(true)
        }
    }
}

fun WeatherDatabase.Companion.weatherForCityWithID(
    id: Int
): Single<WeatherQueryResult> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()

            var weatherID = 0

            var visibility = 0
            var temperature = 0.0
            var feelsLike = 0.0
            var tempMin = 0.0
            var tempMax = 0.0
            var pressure = 0
            var humidity = 0
            var seaLevel = 0
            var groundLevel = 0
            var windSpeed = 0.0
            var windDeg = 0.0

            var query = "SELECT ID, VISIBILITY FROM WEATHER WHERE CITY_ID = '$id' ORDER BY ID DESC LIMIT 1"
            var cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.moveToFirst()) {
                weatherID = cursor.getInt(0)
                visibility = cursor.getInt(1)
            }

            cursor.close()

            query = "SELECT TEMPERATURE, FEELS_LIKE, TEMP_MIN, TEMP_MAX, PRESSURE, HUMIDITY, " +
                    "SEA_LEVEL, GROUND_LEVEL FROM WEATHER_MAIN WHERE WEATHER_ID = '$weatherID' " +
                    "ORDER BY ID DESC LIMIT 1"
            cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.moveToFirst()) {
                temperature = cursor.getDouble(0)
                feelsLike = cursor.getDouble(1)
                tempMin = cursor.getDouble(2)
                tempMax = cursor.getDouble(3)
                pressure = cursor.getInt(4)
                humidity = cursor.getInt(5)
                seaLevel = cursor.getInt(6)
                groundLevel = cursor.getInt(7)
            }

            cursor.close()

            query = "SELECT SPEED, DEG FROM WIND WHERE WEATHER_ID = '$weatherID' " +
                    "ORDER BY ID DESC LIMIT 1"
            cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.moveToFirst()) {
                windSpeed = cursor.getDouble(0)
                windDeg = cursor.getDouble(1)
            }

            cursor.close()

            dbSemaphore.release()
            it.onSuccess(WeatherQueryResult(
                visibility, temperature, feelsLike, tempMin, tempMax,
                pressure, humidity, seaLevel, groundLevel, windSpeed, windDeg
            ))
        }
    }
}