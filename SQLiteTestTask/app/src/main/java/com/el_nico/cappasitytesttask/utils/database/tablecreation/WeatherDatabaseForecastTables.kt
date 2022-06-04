package com.el_nico.cappasitytesttask.utils.database.tablecreation

import com.el_nico.cappasitytesttask.utils.database.WeatherDatabase

fun WeatherDatabase.Companion.createForecastTable() {
    weatherDatabase.execSQL(
        "CREATE TABLE FORECAST(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CITY_ID INTEGER, " +
                "COD TEXT, " +
                "MESSAGE INTEGER, " +
                "CNT INTEGER, " +
                "FOREIGN KEY(CITY_ID) REFERENCES SAVED_CITIES(ID))"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE CITY(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "NAME TEXT, COORD_LAT REAL, COORD_LON REAL, " +
                "COUNTRY TEXT, POPULATION INTEGER, TIMEZONE INTEGER, " +
                "SUNRISE INTEGER, SUNSET INTEGER, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES FORECAST(id) " +
                "ON DELETE CASCADE)"
    )

    ///

    weatherDatabase.execSQL(
        "CREATE TABLE FORECAST_WEATHER(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "VISIBILITY INTEGER, " +
                "BASE TEXT, " +
                "DT INTEGER, " +
                "TIMEZONE INTEGER, " +
                "NAME TEXT, " +
                "COD INTEGER, " +
                "COORD_LAT REAL, " +
                "COORD_LON REAL, " +
                "CLOUDS_ALL INTEGER, " +
                "FOREIGN KEY(WEATHER_ID) REFERENCES FORECAST(ID))"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE FORECAST_WIND(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, SPEED REAL, DEG INTEGER, GUST REAL, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES FORECAST_WEATHER(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE FORECAST_SUN_INFO(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, COUNTRY TEXT, SUNRISE INTEGER, SUNSET INTEGER, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES FORECAST_WEATHER(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE FORECAST_WEATHER_MAIN(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "TEMPERATURE REAL, FEELS_LIKE REAL, " +
                "TEMP_MIN REAL, TEMP_MAX REAL, " +
                "PRESSURE INTEGER, HUMIDITY INTEGER, " +
                "SEA_LEVEL INTEGER, GROUND_LEVEL INTEGER," +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES FORECAST_WEATHER(id) " +
                "ON DELETE CASCADE)"
    )

    weatherDatabase.execSQL(
        "CREATE TABLE FORECAST_WEATHER_LIST(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WEATHER_ID INTEGER, " +
                "MAIN TEXT, DESCRIPTION TEXT, ICON TEXT, " +
                "CONSTRAINT fk_weather FOREIGN KEY (WEATHER_ID) REFERENCES FORECAST_WEATHER(id) " +
                "ON DELETE CASCADE)"
    )
}