package com.el_nico.cappasitytesttask.utils.database.tablecreation

import com.el_nico.cappasitytesttask.utils.database.WeatherDatabase

fun WeatherDatabase.Companion.createOneCallTable() {
    weatherDatabase.execSQL(
        "CREATE TABLE ONECALL(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CITY_ID INTEGER, " +
                "LAT REAL, LON REAL, TIMEZONE TEXT, TIMEZONE_OFFSET REAL, " +
                "FOREIGN KEY(CITY_ID) REFERENCES SAVED_CITIES(ID))"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE CURRENT(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "DT INTEGER, SUNRISE INTEGER, SUNSET INTEGER, TEMPERATURE REAL, " +
                "FEELS_LIKE REAL, PRESSURE INTEGER, HUMIDITY INTEGER, DEW_POINT REAL, " +
                "UVI READ, CLOUDS INTEGER, VISIBILITY INTEGER, WIND_SPEED REAL, " +
                "WIND_DEG INTEGER, " +
                "FOREIGN KEY(WEATHER_ID) REFERENCES ONECALL(ID))"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE CURRENT_WEATHER_LIST(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "MAIN TEXT, DESCRIPTION TEXT, ICON TEXT, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES CURRENT(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE MINUTELY(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "DT INTEGER, PRECIPITATION REAL, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES ONECALL(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE HOURLY(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "DT INTEGER, TEMPERATURE REAL, " +
                "FEELS_LIKE REAL, PRESSURE INTEGER, HUMIDITY INTEGER, DEW_POINT REAL, " +
                "UVI REAL, CLOUDS INTEGER, VISIBILITY INTEGER, WIND_SPEED REAL, " +
                "WIND_DEG INTEGER, WIND_GUST REAL, POP REAL, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES ONECALL(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE HOURLY_WEATHER_LIST(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "MAIN TEXT, DESCRIPTION TEXT, ICON TEXT, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES HOURLY(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE DAILY(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "DT INTEGER, SUNRISE INTEGER, SUNSET INTEGER, " +
                "MOONRISE INTEGER, MOONSET INTEGER, MOON_PHASE REAL, " +
                "PRESSURE INTEGER, HUMIDITY INTEGER, DEW_POINT REAL, " +
                "WIND_SPEED REAL, WIND_DEG INTEGER, WIND_GUST REAL, " +
                "CLOUDS INTEGER, POP REAL, UVI REAL, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES ONECALL(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE DAILY_WEATHER_LIST(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "MAIN TEXT, DESCRIPTION TEXT, ICON TEXT, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES DAILY(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE DAILY_TEMP(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "DAY REAL, MIN REAL, MAX REAL, NIGHT REAL, EVE REAL, MORN REAL, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES DAILY(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE DAILY_FEELS_LIKE(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "DAY REAL, MIN REAL, MAX REAL, NIGHT REAL, EVE REAL, MORN REAL, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES DAILY(id) " +
                "ON DELETE CASCADE)"
    )
}