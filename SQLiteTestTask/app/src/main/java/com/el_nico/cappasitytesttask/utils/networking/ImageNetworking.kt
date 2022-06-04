package com.el_nico.cappasitytesttask.utils.networking

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.el_nico.cappasitytesttask.interfaces.networking.ImageRequestType
import com.el_nico.cappasitytesttask.domain.entity.networking.ImageLinkResponse
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ImageNetworking {

    companion object {

        private const val baseURL = "https://www.googleapis.com/"

        lateinit var requestType: ImageRequestType

        lateinit var client: OkHttpClient

        fun init() {
            val rxAdapter = RxJava3CallAdapterFactory
                .createWithScheduler(Schedulers.io())

            client = OkHttpClient.Builder()
                .connectTimeout(55, TimeUnit.SECONDS)
                .readTimeout(55, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build()

            requestType = retrofit.create(ImageRequestType::class.java)
        }

        fun imageSearch(city: String): Single<ImageLinkResponse> {
            return requestType.imageSearch(
                "AIzaSyDuXx51y_PsPeLS40Kde1nQ0mf263pVoj8",
                "876a8f77a03bb527d",
                city, "image").flatMap {
                val responseBody = it.body()
                if (responseBody is ImageLinkResponse) {
                    Single.just(responseBody)
                } else {
                    Single.error(Throwable(it.errorBody()?.string()))
                }
            }
        }

        fun loadCityBackground(url: String): Single<Bitmap> {
            return Single.create {
                requestType.image(url)
                    .enqueue(object: Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                if (responseBody is ResponseBody) {
                                    val bitmap = BitmapFactory.decodeStream(responseBody.byteStream())
                                    it.onSuccess(bitmap)
                                } else {
                                    it.onError(Throwable(response.errorBody().toString()))
                                }
                            } else {
                                it.onError(Throwable(response.errorBody().toString()))
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            it.onError(t)
                        }
                    })
            }
        }
    }
}