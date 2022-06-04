package com.el_nico.cappasitytesttask.utils.networking

import com.el_nico.cappasitytesttask.interfaces.networking.RequestType
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Networking {

    companion object {

        private const val baseURL = "https://api.openweathermap.org/data/2.5/"

        lateinit var requestType: RequestType

        private val interceptor = HttpLoggingInterceptor()

        private lateinit var client: OkHttpClient

        internal var apiKey: String? = null

        fun init(config: String) {
            setupApiKey(config)

            val rxAdapter = RxJava3CallAdapterFactory
                .createWithScheduler(Schedulers.io())

            interceptor.level = HttpLoggingInterceptor.Level.BODY
            client = OkHttpClient.Builder()
                .connectTimeout(55, TimeUnit.SECONDS)
                .readTimeout(55, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build()

            requestType = retrofit.create(RequestType::class.java)
        }

        private fun setupApiKey(config: String) {
            val configJSON = JSONObject(config)

            if (configJSON.has("openweathermap_api_key")) {
                apiKey = configJSON.getString("openweathermap_api_key")
                println(apiKey)
                println()
            }
        }
    }
}