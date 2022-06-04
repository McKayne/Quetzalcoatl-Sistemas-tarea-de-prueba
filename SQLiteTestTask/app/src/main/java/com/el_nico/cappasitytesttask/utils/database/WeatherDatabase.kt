package com.el_nico.cappasitytesttask.utils.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.el_nico.cappasitytesttask.utils.database.tablecreation.createForecastTable
import com.el_nico.cappasitytesttask.utils.database.tablecreation.createOneCallTable
import com.el_nico.cappasitytesttask.utils.database.tablecreation.createWeatherTable
import io.reactivex.rxjava3.core.Single
import org.json.JSONObject
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

/**
 * БД приложения для хранения и отображения данных о погоде в offline-режиме
 */
class WeatherDatabase {

    companion object {

        /**
         * Экземпляр объекта БД SQLite
         */
        internal lateinit var weatherDatabase: SQLiteDatabase

        /**
         * SQLite однопоточный, поэтому нужно использовать инструменты синхронизации для контроля обращений к БД
         */
        internal val dbSemaphore = Semaphore(1)

        /**
         * Инициализация БД с созданием БД и таблиц (если они еще не существуют в ФС)
         */
        fun init(context: Context, config: String): Single<Boolean> {
            return Single.create {
                Executors.newSingleThreadExecutor().execute {
                    val dbFile = createWeatherDatabaseFile(context)
                    weatherDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile,null)

                    if (!tableExists("SAVED_CITIES")) {
                        setupHistoryTable(config)
                    }

                    if (!tableExists("WEATHER")) {
                        createWeatherTable()
                    }

                    if (!tableExists("FORECAST")) {
                        createForecastTable()
                    }

                    if (!tableExists("ONECALL")) {
                        createOneCallTable()
                    }

                    it.onSuccess(true)
                }
            }
        }

        /**
         * Возвращает файл БД
         */
        private fun createWeatherDatabaseFile(context: Context): File {
            val dbPath = File(context.filesDir, "DatabasePath")
            if (!dbPath.exists()) {
                dbPath.mkdir()
            }

            return File(dbPath.path, "weather.sqlite")
        }

        /**
         * Проверка существования таблицы в БД
         */
        private fun tableExists(name: String): Boolean {
            val query = "SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '$name'"

            val cursor = weatherDatabase.rawQuery(query, null)
            val tableExists = cursor is Cursor && cursor.count > 0

            cursor.close()
            return tableExists
        }

        private fun setupHistoryTable(config: String) {
            weatherDatabase.execSQL(
                "CREATE TABLE SAVED_CITIES(" +
                        "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "CITY_NAME TEXT, " +
                        "LAST_UPDATED TEXT, " +
                        "LATITUDE REAL, LONGITUDE REAL" +
                        ")"
            )

            val configJSON = JSONObject(config)

            if (configJSON.has("default_locations")) {
                val defaultLocations = configJSON.getJSONArray("default_locations")
                for (i in 0 until defaultLocations.length()) {
                    val location = defaultLocations[i]
                    weatherDatabase.execSQL(
                        "INSERT INTO SAVED_CITIES(CITY_NAME) VALUES('$location')"
                    )
                }
            }
        }
    }
}