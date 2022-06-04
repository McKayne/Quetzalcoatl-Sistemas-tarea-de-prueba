package com.el_nico.cappasitytesttask.utils.database.tablecreation

import com.el_nico.cappasitytesttask.utils.database.WeatherDatabase

fun WeatherDatabase.Companion.createWeatherTable() {
    weatherDatabase.execSQL(
        "CREATE TABLE WEATHER(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CITY_ID INTEGER, " +
                "VISIBILITY INTEGER, " +
                "BASE TEXT, " +
                "DT INTEGER, " +
                "TIMEZONE INTEGER, " +
                "NAME TEXT, " +
                "COD INTEGER, " +
                "COORD_LAT REAL, " +
                "COORD_LON REAL, " +
                "CLOUDS_ALL INTEGER, " +
                "FOREIGN KEY(CITY_ID) REFERENCES SAVED_CITIES(ID))"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE WIND(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, SPEED REAL, DEG INTEGER, GUST REAL, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES WEATHER(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE SUN_INFO(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, COUNTRY TEXT, SUNRISE INTEGER, SUNSET INTEGER, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES WEATHER(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE WEATHER_MAIN(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "TEMPERATURE REAL, FEELS_LIKE REAL, " +
                "TEMP_MIN REAL, TEMP_MAX REAL, " +
                "PRESSURE INTEGER, HUMIDITY INTEGER, " +
                "SEA_LEVEL INTEGER, GROUND_LEVEL INTEGER," +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES WEATHER(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE WEATHER_LIST(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "MAIN TEXT, DESCRIPTION TEXT, ICON TEXT, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES WEATHER(id) " +
                "ON DELETE CASCADE)"
    )
}