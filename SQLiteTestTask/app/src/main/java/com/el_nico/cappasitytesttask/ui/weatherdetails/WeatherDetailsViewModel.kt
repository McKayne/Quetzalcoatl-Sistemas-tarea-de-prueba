package com.el_nico.cappasitytesttask.ui.weatherdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.el_nico.cappasitytesttask.utils.database.WeatherDatabase
import com.el_nico.cappasitytesttask.utils.database.deleteSavedCity
import com.el_nico.cappasitytesttask.utils.database.infoForCityWithID
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class WeatherDetailsViewModel @Inject constructor(): ViewModel() {

    private val shouldDeleteSavedCity = MutableLiveData<Boolean>()

    private val shouldPresentLoadingIndicator = MutableLiveData<Boolean>()

    private val shouldPresentMessage = MutableLiveData<String?>()

    private val shouldReturnAfterDeletion = MutableLiveData<Boolean>()

    private val shouldUpdateCityTitle = MutableLiveData<String?>()

    val deleteSavedCity: LiveData<Boolean> get() = shouldDeleteSavedCity

    val presentLoadingIndicator: LiveData<Boolean> get() = shouldPresentLoadingIndicator

    val presentMessage: LiveData<String?> get() = shouldPresentMessage

    val returnAfterDeletion: LiveData<Boolean> get() = shouldReturnAfterDeletion

    val updateCityTitle: LiveData<String?> get() = shouldUpdateCityTitle

    fun updateCityTitle(id: Int) {
        WeatherDatabase.infoForCityWithID(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Triple<String, Double?, Double?>> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(info: Triple<String, Double?, Double?>) {
                    shouldUpdateCityTitle.value = info.first
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    fun deleteCityFromSavedAndReturn(id: Int) {
        WeatherDatabase.deleteSavedCity(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                    shouldPresentLoadingIndicator.value = true
                }

                override fun onSuccess(t: Boolean) {
                    shouldPresentLoadingIndicator.value = false
                    shouldReturnAfterDeletion.value = true
                }

                override fun onError(e: Throwable) {
                    shouldPresentLoadingIndicator.value = false
                    shouldPresentMessage.value = e.localizedMessage ?: e.stackTraceToString()
                }
            })
    }

    fun presentCityDeletionDialog() {
        shouldDeleteSavedCity.value = true
    }

    /**
     * Если уже содержит значение то при возврате на соотв. фрагмент произойдет лишнее срабатывание
     * обсервера (и как следствие всех привязанных действий)
     */
    fun clearLeaveData() {
        shouldDeleteSavedCity.value = false
        shouldPresentMessage.value = null
        shouldReturnAfterDeletion.value = false
        shouldUpdateCityTitle.value = null
    }
}