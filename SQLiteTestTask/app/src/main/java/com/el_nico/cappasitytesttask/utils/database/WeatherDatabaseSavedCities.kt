package com.el_nico.cappasitytesttask.utils.database

import android.database.Cursor
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.Executors

fun WeatherDatabase.Companion.savedCitiesList(): Single<ArrayList<Int>> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            val savedCities = ArrayList<Int>()

            val query = "SELECT ID FROM SAVED_CITIES"
            val cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor) {
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(0)
                    savedCities.add(id)
                }
            }

            cursor.close()

            it.onSuccess(savedCities)
        }
    }
}

fun WeatherDatabase.Companion.savedCityWithID(id: Int): Single<Pair<String, String?>> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()
            var city = ""
            var lastUpdated: String? = null

            val query = "SELECT CITY_NAME, LAST_UPDATED FROM SAVED_CITIES WHERE ID = $id"
            val cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.moveToFirst()) {
                city = cursor.getString(0)
                lastUpdated = cursor.getString(1)
            }

            cursor.close()

            dbSemaphore.release()
            it.onSuccess(Pair(city, lastUpdated))
        }
    }
}

fun WeatherDatabase.Companion.idForCityWithIndex(
    index: Int
): Single<Int> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()
            var id = 0

            val query = "SELECT ID FROM SAVED_CITIES"
            val cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.move(index + 1)) {
                id = cursor.getInt(0)
            }

            cursor.close()

            dbSemaphore.release()
            it.onSuccess(id)
        }
    }
}

fun WeatherDatabase.Companion.idForCityWithName(
    city: String
): Single<Int> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()
            var id = 0

            val query = "SELECT ID FROM SAVED_CITIES WHERE CITY_NAME = '$city'"
            val cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.moveToFirst()) {
                id = cursor.getInt(0)
            }

            cursor.close()

            dbSemaphore.release()
            it.onSuccess(id)
        }
    }
}

fun WeatherDatabase.Companion.infoForCityWithID(
    id: Int
): Single<Triple<String, Double?, Double?>> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()
            var city = ""
            var latitude: Double? = null
            var longitude: Double? = null

            val query = "SELECT CITY_NAME, LATITUDE, LONGITUDE FROM SAVED_CITIES WHERE ID = $id"
            val cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.moveToFirst()) {
                city = cursor.getString(0)
                latitude = cursor.getDouble(1)
                longitude = cursor.getDouble(2)
            }

            cursor.close()

            dbSemaphore.release()
            it.onSuccess(Triple(city, latitude, longitude))
        }
    }
}

fun WeatherDatabase.Companion.checkIfHasSavedCity(city: String): Single<Boolean> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()
            var count = 0

            val query = "SELECT COUNT(*) FROM SAVED_CITIES WHERE CITY_NAME = '$city'"
            val cursor = weatherDatabase.rawQuery(query, null)

            if (cursor is Cursor && cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }

            cursor.close()

            dbSemaphore.release()
            it.onSuccess(count == 1)
        }
    }
}

fun WeatherDatabase.Companion.saveCity(
    city: String, latitude: Double, longitude: Double
): Single<Boolean> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()

            weatherDatabase.execSQL(
                "INSERT INTO SAVED_CITIES(CITY_NAME, LATITUDE, LONGITUDE) " +
                        "VALUES('$city', $latitude, $longitude)"
            )

            dbSemaphore.release()
            it.onSuccess(true)
        }
    }
}

fun WeatherDatabase.Companion.updateSavedCityUpdateTime(
    id: Int, lastUpdateTime: String
): Single<Boolean> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()

            weatherDatabase.execSQL(
                "UPDATE SAVED_CITIES SET LAST_UPDATED = '$lastUpdateTime' WHERE ID = '$id'"
            )

            dbSemaphore.release()
            it.onSuccess(true)
        }
    }
}

fun WeatherDatabase.Companion.updateSavedCityLatLon(
    city: String, latitude: Double, longitude: Double
): Single<Boolean> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()

            weatherDatabase.execSQL(
                "UPDATE SAVED_CITIES SET LATITUDE = '$latitude', LONGITUDE = '$longitude' " +
                        "WHERE CITY_NAME = '$city'"
            )

            dbSemaphore.release()
            it.onSuccess(true)
        }
    }
}

fun WeatherDatabase.Companion.deleteSavedCity(
    id: Int
): Single<Boolean> {
    return Single.create {
        Executors.newSingleThreadExecutor().execute {
            dbSemaphore.acquire()

            weatherDatabase.execSQL(
                "DELETE FROM SAVED_CITIES WHERE ID = '$id'"
            )

            dbSemaphore.release()
            it.onSuccess(true)
        }
    }
}