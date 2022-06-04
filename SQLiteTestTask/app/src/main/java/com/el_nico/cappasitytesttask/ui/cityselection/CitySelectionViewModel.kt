package com.el_nico.cappasitytesttask.ui.cityselection

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.el_nico.cappasitytesttask.utils.GeocodingUtil
import com.el_nico.cappasitytesttask.utils.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class CitySelectionViewModel @Inject constructor(): ViewModel() {

    private val isDatabaseInitialized = MutableLiveData<Boolean>()

    private val shouldPresentCitySelection = MutableLiveData<Boolean>()

    private val shouldPresentLoadingIndicator = MutableLiveData<Boolean>()

    private val shouldPresentMessage = MutableLiveData<String?>()

    private val shouldPresentSavedCity = MutableLiveData<Triple<String, Double?, Double?>?>()

    private val shouldChangeSavedCityID = MutableLiveData<Int?>()

    private val shouldUpdateList = MutableLiveData<ArrayList<Int>?>()

    private val shouldPresentSelectedCityDialog = MutableLiveData<Triple<String, Double, Double>?>()

    private val shouldSaveCityToDatabase = MutableLiveData<String?>()

    val dbInitialized: LiveData<Boolean> get() = isDatabaseInitialized

    val presentCitySelection: LiveData<Boolean> get() = shouldPresentCitySelection

    val presentLoadingIndicator: LiveData<Boolean> get() = shouldPresentLoadingIndicator

    val presentMessage: LiveData<String?> get() = shouldPresentMessage

    val presentSavedCity: LiveData<Triple<String, Double?, Double?>?> get() = shouldPresentSavedCity

    val presentChangeSavedCityID: LiveData<Int?> get() = shouldChangeSavedCityID

    val updateList: LiveData<ArrayList<Int>?> get() = shouldUpdateList

    val presentSelectedCityDialog: LiveData<Triple<String, Double, Double>?>
        get() = shouldPresentSelectedCityDialog

    val saveCityToDB: LiveData<String?> get() = shouldSaveCityToDatabase

    fun initWeatherDatabase(context: Context, config: String) {
        WeatherDatabase.init(context, config)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: Boolean) {
                    isDatabaseInitialized.value = true
                }

                override fun onError(e: Throwable) {
                    isDatabaseInitialized.value = false
                }
            })
    }

    fun updateList() {
        WeatherDatabase.savedCitiesList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<ArrayList<Int>> {
                override fun onSubscribe(d: Disposable) {
                    shouldPresentLoadingIndicator.value = true
                }

                override fun onSuccess(list: ArrayList<Int>) {
                    shouldPresentLoadingIndicator.value = false
                    shouldUpdateList.value = list
                }

                override fun onError(e: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    shouldPresentMessage.value = e.localizedMessage ?: e.stackTraceToString()
                }
            })
    }

    fun idForCityWithIndex(index: Int) {
        WeatherDatabase.idForCityWithIndex(index)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Int> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(id: Int) {
                    shouldChangeSavedCityID.value = id
                    loadAndPresentSavedCity(id)
                }

                override fun onError(e: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    shouldPresentMessage.value = e.localizedMessage ?: e.stackTraceToString()
                }
            })
    }

    fun loadAndPresentSavedCity(id: Int) {
        WeatherDatabase.infoForCityWithID(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Triple<String, Double?, Double?>> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(city: Triple<String, Double?, Double?>) {
                    shouldPresentSavedCity.value = city
                }

                override fun onError(e: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    shouldPresentMessage.value = e.localizedMessage ?: e.stackTraceToString()
                }
            })
    }

    fun requestLatLonAndContinue(context:Context, city: String) {
        GeocodingUtil.searchForCity(context, city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Pair<Double?, Double?>> {
                override fun onSubscribe(d: Disposable) {
                    shouldPresentLoadingIndicator.value = true
                }

                override fun onSuccess(location: Pair<Double?, Double?>) {
                    val latitude = location.first
                    val longitude = location.second

                    if (latitude is Double && longitude is Double) {
                        updateLatLonAndContinue(city, latitude, longitude)
                    } else {
                        shouldPresentLoadingIndicator.value = false
                        shouldPresentMessage.value = "Error"
                    }
                }

                override fun onError(e: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    shouldPresentMessage.value = e.localizedMessage ?: e.stackTraceToString()

                    updateLatLonAndContinue(city, 0.0, 0.0)
                }
            })
    }

    fun updateLatLonAndContinue(city: String, latitude: Double, longitude: Double) {
        WeatherDatabase.updateSavedCityLatLon(city, latitude, longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: Boolean) {
                    shouldPresentLoadingIndicator.value = false
                    shouldPresentSelectedCityDialog.value = Triple(city, latitude, longitude)
                }

                override fun onError(e: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    shouldPresentMessage.value = e.localizedMessage ?: e.stackTraceToString()
                }
            })
    }

    fun checkIfCityIsAlreadySavedAndContinue(context: Context, city: String) {
        WeatherDatabase.checkIfHasSavedCity(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                    shouldPresentLoadingIndicator.value = true
                }

                override fun onSuccess(alreadyExists: Boolean) {
                    if (!alreadyExists) {
                        searchForCityAndContinue(context, city)
                    } else {
                        shouldPresentLoadingIndicator.value = false
                        shouldPresentMessage.value = "Ya hay en lista esta ciudad"
                    }
                }

                override fun onError(e: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    shouldPresentMessage.value = e.localizedMessage ?: e.stackTraceToString()
                }
            })
    }

    fun searchForCityAndContinue(context: Context, city: String) {
        GeocodingUtil.searchForCity(context, city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Pair<Double?, Double?>> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(location: Pair<Double?, Double?>) {
                    shouldPresentLoadingIndicator.value = false

                    val latitude = location.first
                    val longitude = location.second

                    if (latitude is Double && longitude is Double) {
                        saveCityToDatabaseAndContinue(city, latitude, longitude)
                    } else {
                        shouldPresentMessage.value = "Ciudad con este nombre no está encontrado"
                    }
                }

                override fun onError(e: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    shouldPresentMessage.value = e.localizedMessage ?: e.stackTraceToString()

                    saveCityToDatabaseAndContinue(city, 0.0, 0.0)
                }
            })
    }

    fun saveCityToDatabaseAndContinue(
        city: String, latitude: Double, longitude: Double
    ) {
        WeatherDatabase.saveCity(city, latitude, longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: Boolean) {
                    shouldSaveCityToDatabase.value = city
                }

                override fun onError(e: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    shouldPresentMessage.value = e.localizedMessage ?: e.stackTraceToString()
                }
            })
    }

    fun idForCityWithName(city: String) {
        WeatherDatabase.idForCityWithName(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Int> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(id: Int) {
                    shouldChangeSavedCityID.value = id
                    loadAndPresentSavedCity(id)
                }

                override fun onError(e: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    shouldPresentMessage.value = e.localizedMessage ?: e.stackTraceToString()
                }
            })
    }

    fun presentCitySelectionDialog() {
        shouldPresentCitySelection.value = true
    }

    /**
     * Если уже содержит значение то при возврате на соотв. фрагмент произойдет лишнее срабатывание
     * обсервера (и как следствие всех привязанных действий)
     */
    fun clearLeaveData() {
        shouldPresentCitySelection.value = false
        shouldPresentMessage.value = null
        shouldPresentSavedCity.value = null
        shouldChangeSavedCityID.value = null
        shouldUpdateList.value = null
        shouldPresentSelectedCityDialog.value = null
        shouldSaveCityToDatabase.value = null
    }
}