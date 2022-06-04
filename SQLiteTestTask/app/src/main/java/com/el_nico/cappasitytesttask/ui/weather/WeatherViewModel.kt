package com.el_nico.cappasitytesttask.ui.weather

import android.graphics.Bitmap
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.el_nico.cappasitytesttask.domain.entity.networking.ImageLinkResponse
import com.el_nico.cappasitytesttask.utils.networking.ImageNetworking
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(): ViewModel() {

    private val shouldUpdateBackground = MutableLiveData<Bitmap?>()

    val updateBackground: LiveData<Bitmap?> get() = shouldUpdateBackground

    fun changeCityBackground(city: String) {
        ImageNetworking.imageSearch(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<ImageLinkResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(response: ImageLinkResponse) {
                    val items = response.items
                    if (items is Array) {
                        val link = items[0].link
                        if (link is String) {
                            updateCityBackground(link)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    fun updateCityBackground(url: String) {
        ImageNetworking.loadCityBackground(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Bitmap> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(image: Bitmap) {
                    shouldUpdateBackground.value = image
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
        shouldUpdateBackground.value = null
    }
}