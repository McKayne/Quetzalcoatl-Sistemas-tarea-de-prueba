package com.el_nico.cappasitytesttask.ui.currentweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.el_nico.cappasitytesttask.enums.WeatherDetailsType
import com.el_nico.cappasitytesttask.domain.entity.database.ForecastQueryResult
import com.el_nico.cappasitytesttask.domain.entity.database.OneCallQueryResult
import com.el_nico.cappasitytesttask.domain.entity.database.WeatherQueryResult
import com.el_nico.cappasitytesttask.domain.entity.networking.forecast.ForecastResponse
import com.el_nico.cappasitytesttask.domain.entity.networking.onecall.OneCallResponse
import com.el_nico.cappasitytesttask.domain.entity.networking.weather.WeatherResponse
import com.el_nico.cappasitytesttask.utils.database.*
import com.el_nico.cappasitytesttask.utils.database.weatherqueries.*
import com.el_nico.cappasitytesttask.utils.networking.Networking
import com.el_nico.cappasitytesttask.utils.networking.forecast
import com.el_nico.cappasitytesttask.utils.networking.oneCall
import com.el_nico.cappasitytesttask.utils.networking.weather
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(): ViewModel() {

    internal lateinit var type: WeatherDetailsType

    private val shouldPresentLoadingIndicator = MutableLiveData<Boolean>()

    private val shouldPresentMessage = MutableLiveData<String?>()

    private val shouldUpdateWeather = MutableLiveData<WeatherQueryResult?>()

    private val shouldUpdateForecast = MutableLiveData<Collection<ForecastQueryResult>?>()

    private val shouldUpdateOneCall = MutableLiveData<Collection<OneCallQueryResult>?>()

    private val shouldPresentWeatherFromDatabase = MutableLiveData<Int?>()

    private val shouldPresentForecastFromDatabase = MutableLiveData<Int?>()

    private val shouldPresentOneCallFromDatabase = MutableLiveData<Int?>()

    val presentLoadingIndicator: LiveData<Boolean> get() = shouldPresentLoadingIndicator

    val presentMessage: LiveData<String?> get() = shouldPresentMessage

    val updateWeather: LiveData<WeatherQueryResult?> get() = shouldUpdateWeather

    val updateForecast: LiveData<Collection<ForecastQueryResult>?> get() = shouldUpdateForecast

    val updateOneCall: LiveData<Collection<OneCallQueryResult>?> get() = shouldUpdateOneCall

    val presentWeatherFromDatabase: LiveData<Int?> get() = shouldPresentWeatherFromDatabase

    val presentForecastFromDatabase: LiveData<Int?> get() = shouldPresentForecastFromDatabase

    val presentOneCallFromDatabase: LiveData<Int?> get() = shouldPresentOneCallFromDatabase

    fun presentOneCallFromDatabase(id: Int, updateForecast: Boolean) {
        WeatherDatabase.oneCallForCityWithID(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Collection<OneCallQueryResult>> {
                override fun onSubscribe(d: Disposable) {
                    shouldPresentLoadingIndicator.value = true
                }

                override fun onSuccess(result: Collection<OneCallQueryResult>) {
                    shouldUpdateOneCall.value = result

                    if (updateForecast) {
                        updateWeatherRequest(id)
                    } else {
                        shouldPresentLoadingIndicator.value = false
                    }
                }

                override fun onError(throwable: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    val errorMessage = throwable.localizedMessage ?: throwable.stackTraceToString()
                    shouldPresentMessage.value = errorMessage
                }
            })
    }

    fun presentForecastFromDatabase(id: Int, updateForecast: Boolean) {
        WeatherDatabase.forecastForCityWithID(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Collection<ForecastQueryResult>> {
                override fun onSubscribe(d: Disposable) {
                    shouldPresentLoadingIndicator.value = true
                }

                override fun onSuccess(result: Collection<ForecastQueryResult>) {
                    shouldUpdateForecast.value = result

                    if (updateForecast) {
                        updateWeatherRequest(id)
                    } else {
                        shouldPresentLoadingIndicator.value = false
                    }
                }

                override fun onError(throwable: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    val errorMessage = throwable.localizedMessage ?: throwable.stackTraceToString()
                    shouldPresentMessage.value = errorMessage
                }
            })
    }

    fun presentWeatherFromDatabase(id: Int, updateWeather: Boolean) {
        WeatherDatabase.weatherForCityWithID(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<WeatherQueryResult> {
                override fun onSubscribe(d: Disposable) {
                    shouldPresentLoadingIndicator.value = true
                }

                override fun onSuccess(result: WeatherQueryResult) {
                    shouldUpdateWeather.value = result

                    if (updateWeather) {
                        updateWeatherRequest(id)
                    } else {
                        shouldPresentLoadingIndicator.value = false
                    }
                }

                override fun onError(throwable: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    val errorMessage = throwable.localizedMessage ?: throwable.stackTraceToString()
                    shouldPresentMessage.value = errorMessage
                }
            })
    }

    fun updateWeatherRequest(id: Int) {
        WeatherDatabase.infoForCityWithID(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Triple<String, Double?, Double?>> {
                override fun onSubscribe(d: Disposable) {
                    shouldPresentLoadingIndicator.value = true
                }

                override fun onSuccess(info: Triple<String, Double?, Double?>) {
                    requestInfoUpdate(id, info)
                }

                override fun onError(throwable: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    val errorMessage = throwable.localizedMessage ?: throwable.stackTraceToString()
                    shouldPresentMessage.value = errorMessage
                }
            })
    }

    fun requestInfoUpdate(savedCityID: Int, info: Triple<String, Double?, Double?>) {
        when (type) {
            WeatherDetailsType.WEATHER -> {
                requestWeather(savedCityID, info.first)
            }
            WeatherDetailsType.FORECAST -> {
                requestForecast(savedCityID, info.first)
            }
            WeatherDetailsType.ONECALL -> {
                val latitude = info.second
                val longitude = info.third

                if (latitude is Double && longitude is Double
                    && !(latitude == 0.0 && longitude == 0.0)) {
                    requestOneCall(savedCityID, latitude, longitude)
                } else {
                    shouldPresentLoadingIndicator.value = false
                    shouldPresentMessage.value = "¡No hay las coordenadas!"
                }
            }
        }
    }

    private fun requestOneCall(savedCityID: Int, latitude: Double, longitude: Double) {
        Networking.oneCall(latitude, longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<OneCallResponse> {
                override fun onSubscribe(d: Disposable) {
                    shouldPresentLoadingIndicator.value = true
                }

                override fun onSuccess(response: OneCallResponse) {
                    updateSavedCityLastUpdateTime(savedCityID)
                    updateOneCallForCityWithID(savedCityID, response)
                }

                override fun onError(throwable: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    val errorMessage = throwable.localizedMessage ?: throwable.stackTraceToString()
                    shouldPresentMessage.value = errorMessage
                }
            })
    }

    private fun requestForecast(savedCityID: Int, city: String) {
        Networking.forecast(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<ForecastResponse> {
                override fun onSubscribe(d: Disposable) {
                    shouldPresentLoadingIndicator.value = true
                }

                override fun onSuccess(response: ForecastResponse) {
                    updateSavedCityLastUpdateTime(savedCityID)
                    updateForecastForCityWithID(savedCityID, response)
                }

                override fun onError(throwable: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    val errorMessage = throwable.localizedMessage ?: throwable.stackTraceToString()
                    shouldPresentMessage.value = errorMessage
                }
            })
    }

    private fun requestWeather(savedCityID: Int, city: String) {
        Networking.weather(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<WeatherResponse> {
                override fun onSubscribe(d: Disposable) {
                    shouldPresentLoadingIndicator.value = true
                }

                override fun onSuccess(response: WeatherResponse) {
                    shouldPresentLoadingIndicator.value = false

                    updateSavedCityLastUpdateTime(savedCityID)
                    updateWeatherForCityWithID(savedCityID, response)
                }

                override fun onError(throwable: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    val errorMessage = throwable.localizedMessage ?: throwable.stackTraceToString()
                    shouldPresentMessage.value = errorMessage
                }
            })
    }

    fun updateSavedCityLastUpdateTime(savedCityID: Int) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        val lastUpdateTime = dateFormat.format(Date())

        WeatherDatabase.updateSavedCityUpdateTime(savedCityID, lastUpdateTime)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: Boolean) {
                    when (type) {
                        WeatherDetailsType.WEATHER -> {
                            shouldPresentWeatherFromDatabase.value = savedCityID
                        }
                        WeatherDetailsType.FORECAST -> {
                            shouldPresentForecastFromDatabase.value = savedCityID
                        }
                        WeatherDetailsType.ONECALL -> {
                            shouldPresentOneCallFromDatabase.value = savedCityID
                        }
                    }
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    fun updateWeatherForCityWithID(id: Int, weather: WeatherResponse) {
        WeatherDatabase.updateWeatherForCityWithID(id, weather)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    fun updateForecastForCityWithID(id: Int, forecast: ForecastResponse) {
        WeatherDatabase.updateForecastForCityWithID(id, forecast)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    fun updateOneCallForCityWithID(id: Int, oneCall: OneCallResponse) {
        WeatherDatabase.updateOneCallForCityWithID(id, oneCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    /**
     * Если уже содержит значение то при возврате на соотв. фрагмент произойдет лишнее срабатывание
     * обсервера (и как следствие всех привязанных действий)
     */
    fun clearLeaveData() {
        shouldPresentMessage.value = null
        shouldUpdateWeather.value = null
        shouldUpdateForecast.value = null
        shouldUpdateOneCall.value = null
        shouldPresentWeatherFromDatabase.value = null
        shouldPresentForecastFromDatabase.value = null
        shouldPresentOneCallFromDatabase.value = null
    }
}