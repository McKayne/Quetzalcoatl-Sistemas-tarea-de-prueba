package com.el_nico.cappasitytesttask.utils.database.weatherqueries

import android.database.Cursor
import com.el_nico.cappasitytesttask.domain.entity.database.OneCallQueryResult
import com.el_nico.cappasitytesttask.domain.entity.networking.onecall.OneCallResponse
import com.el_nico.cappasitytesttask.utils.database.WeatherDatabase
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.Executors

fun WeatherDatabase.Companion.updateOneCallForCityWithID(
    id: Int, oneCall: OneCallResponse
): Single<Boolean> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()

            weatherDatabase.execSQL(
                "DELETE FROM ONECALL WHERE CITY_ID = '$id'"
            )

            weatherDatabase.execSQL(
                "INSERT INTO ONECALL(CITY_ID, LAT, LON, TIMEZONE, TIMEZONE_OFFSET) " +
                        "VALUES($id, ${oneCall.latitude}, ${oneCall.longitude}, '${oneCall.timezone}', " +
                        "${oneCall.timezoneOffset})"
            )

            ///

            var weatherID: Int? = null

            val query = "SELECT ID FROM ONECALL WHERE CITY_ID = '$id' ORDER BY ID DESC LIMIT 1"
            val cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.moveToFirst()) {
                weatherID = cursor.getInt(0)
            }

            cursor.close()

            if (weatherID != null) {
                val current = oneCall.current

                weatherDatabase.execSQL(
                    "INSERT INTO CURRENT(WEATHER_ID, DT, SUNRISE, SUNSET, TEMPERATURE, " +
                            "FEELS_LIKE, PRESSURE, HUMIDITY, DEW_POINT, UVI, CLOUDS, VISIBILITY, " +
                            "WIND_SPEED, WIND_DEG) VALUES($weatherID, " +
                            "${current.dt}, ${current.sunrise}, ${current.sunset}, ${current.temp}, " +
                            "${current.feelsLike}, ${current.pressure}, ${current.humidity}, " +
                            "${current.dewPoint}, ${current.uvi}, ${current.clouds}, " +
                            "${current.visibility}, ${current.windSpeed}, ${current.windDeg})"
                )

                var onecallID: Int? = null

                val onecallQuery = "SELECT ID FROM CURRENT WHERE WEATHER_ID = '$id' ORDER BY ID DESC LIMIT 1"
                val onecallCursor = weatherDatabase.rawQuery(onecallQuery, null)

                if (onecallCursor is Cursor && onecallCursor.moveToFirst()) {
                    onecallID = onecallCursor.getInt(0)
                }

                onecallCursor.close()

                if (onecallID != null) {
                    for (weather in current.weather) {
                        weatherDatabase.execSQL(
                            "INSERT INTO CURRENT_WEATHER_LIST(WEATHER_ID, MAIN, " +
                                    "DESCRIPTION, ICON) VALUES($onecallID, " +
                                    "'${weather.main}', '${weather.description}', '${weather.icon}')"
                        )
                    }
                }

                val oneCallMinutely = oneCall.minutely
                if (oneCallMinutely is Array) {
                    for (minutely in oneCallMinutely) {
                        weatherDatabase.execSQL(
                            "INSERT INTO MINUTELY(WEATHER_ID, DT, PRECIPITATION) VALUES($weatherID, " +
                                    "${minutely.dt}, ${minutely.precipitation})"
                        )
                    }
                }

                val oneCallHourly = oneCall.hourly
                if (oneCallHourly is Array) {
                    for (hourly in oneCallHourly) {
                        weatherDatabase.execSQL(
                            "INSERT INTO HOURLY(WEATHER_ID, DT, TEMPERATURE, FEELS_LIKE, " +
                                    "PRESSURE, HUMIDITY, DEW_POINT, UVI, CLOUDS, VISIBILITY, " +
                                    "WIND_SPEED, WIND_DEG, WIND_GUST, POP) VALUES($weatherID, " +
                                    "${hourly.dt}, ${hourly.temp}, ${hourly.feelsLike}, ${hourly.pressure}, " +
                                    "${hourly.humidity}, ${hourly.dewPoint}, ${hourly.uvi}, ${hourly.clouds}, " +
                                    "${hourly.visibility}, ${hourly.windSpeed}, ${hourly.windDeg}, " +
                                    "${hourly.windGust}, ${hourly.pop})"
                        )

                        var hourlyID: Int? = null

                        val hourlyQuery = "SELECT ID FROM HOURLY WHERE WEATHER_ID = '$weatherID' ORDER BY ID DESC LIMIT 1"
                        val hourlyCursor = weatherDatabase.rawQuery(hourlyQuery, null)

                        if (hourlyCursor is Cursor && hourlyCursor.moveToFirst()) {
                            hourlyID = hourlyCursor.getInt(0)
                        }

                        hourlyCursor.close()

                        if (hourlyID != null) {
                            for (weather in hourly.weather) {
                                weatherDatabase.execSQL(
                                    "INSERT INTO HOURLY_WEATHER_LIST(WEATHER_ID, MAIN, " +
                                            "DESCRIPTION, ICON) VALUES($hourlyID, " +
                                            "'${weather.main}', '${weather.description}', '${weather.icon}')"
                                )
                            }
                        }
                    }
                }

                val oneCallDaily = oneCall.daily
                if (oneCallDaily is Array) {
                    for (daily in oneCallDaily) {
                        weatherDatabase.execSQL(
                            "INSERT INTO DAILY(WEATHER_ID, DT, SUNRISE, SUNSET, MOONRISE, " +
                                    "MOONSET, MOON_PHASE, PRESSURE, HUMIDITY, DEW_POINT, " +
                                    "WIND_SPEED, WIND_DEG, WIND_GUST, CLOUDS, POP, UVI) VALUES($weatherID, " +
                                    "${daily.dt}, ${daily.sunrise}, ${daily.sunset}, ${daily.moonrise}, " +
                                    "${daily.moonset}, ${daily.moonPhase}, ${daily.pressure}, ${daily.humidity}, " +
                                    "${daily.dewPoint}, ${daily.windSpeed}, ${daily.windDeg}, ${daily.windGust}, " +
                                    "${daily.clouds}, ${daily.pop}, ${daily.uvi})"
                        )

                        var dailyID: Int? = null

                        val dailyQuery = "SELECT ID FROM DAILY WHERE WEATHER_ID = '$weatherID' ORDER BY ID DESC LIMIT 1"
                        val dailyCursor = weatherDatabase.rawQuery(dailyQuery, null)

                        if (dailyCursor is Cursor && dailyCursor.moveToFirst()) {
                            dailyID = dailyCursor.getInt(0)
                        }

                        dailyCursor.close()

                        if (dailyID != null) {
                            for (weather in daily.weather) {
                                weatherDatabase.execSQL(
                                    "INSERT INTO DAILY_WEATHER_LIST(WEATHER_ID, MAIN, " +
                                            "DESCRIPTION, ICON) VALUES($dailyID, " +
                                            "'${weather.main}', '${weather.description}', '${weather.icon}')"
                                )
                            }

                            val temp = daily.temp
                            weatherDatabase.execSQL(
                                "INSERT INTO DAILY_TEMP(WEATHER_ID, DAY, MIN, " +
                                        "MAX, NIGHT, EVE, MORN) VALUES($dailyID, " +
                                        "${temp.day}, ${temp.min}, ${temp.max}, ${temp.night}, " +
                                        "${temp.eve}, ${temp.morn})"
                            )

                            val feelsLike = daily.feelsLike
                            weatherDatabase.execSQL(
                                "INSERT INTO DAILY_FEELS_LIKE(WEATHER_ID, DAY, MIN, " +
                                        "MAX, NIGHT, EVE, MORN) VALUES($dailyID, " +
                                        "${feelsLike.day}, ${feelsLike.min}, ${feelsLike.max}, ${feelsLike.night}, " +
                                        "${feelsLike.eve}, ${feelsLike.morn})"
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

fun WeatherDatabase.Companion.oneCallForCityWithID(
    id: Int
): Single<Collection<OneCallQueryResult>> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()

            var weatherID = 0

            val oneCallResult = ArrayList<OneCallQueryResult>()

            var query = "SELECT ID FROM ONECALL WHERE CITY_ID = '$id' ORDER BY ID DESC LIMIT 1"
            var cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.moveToFirst()) {
                weatherID = cursor.getInt(0)
            }

            cursor.close()

            ///

            query = "SELECT ID, DT, " +
                    "SUNRISE, SUNSET, " +
                    "MOONRISE, MOONSET, " +
                    "PRESSURE, HUMIDITY, " +
                    "WIND_SPEED, WIND_DEG FROM DAILY WHERE WEATHER_ID = '$weatherID'"
            cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor) {
                while (cursor.moveToNext()) {
                    val result = OneCallQueryResult()
                    result.id = cursor.getInt(0)
                    result.dt = cursor.getString(1)
                    result.sunrise = cursor.getInt(2)
                    result.sunset = cursor.getInt(3)
                    result.moonrise = cursor.getInt(4)
                    result.moonset = cursor.getInt(5)
                    result.pressure = cursor.getInt(6)
                    result.humidity = cursor.getInt(7)
                    result.windSpeed = cursor.getDouble(8)
                    result.windDeg = cursor.getInt(9)
                    oneCallResult.add(result)
                }
            }

            cursor.close()

            //

            for (result in oneCallResult) {
                query = "SELECT DAY, MIN, MAX, NIGHT, EVE, MORN " +
                        "FROM DAILY_TEMP WHERE WEATHER_ID = '${result.id}'"
                cursor = weatherDatabase.rawQuery(query, null)

                if (cursor is Cursor && cursor.moveToFirst()) {
                    result.temperature = cursor.getDouble(0)
                    result.feelsLike = cursor.getDouble(1)
                    result.tempMin = cursor.getDouble(2)
                    result.tempMax = cursor.getDouble(3)
                }

                cursor.close()
            }

            dbSemaphore.release()
            it.onSuccess(oneCallResult)
        }
    }
}