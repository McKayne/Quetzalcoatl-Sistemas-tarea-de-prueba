package com.el_nico.cappasitytesttask.ui.weather

import android.graphics.Bitmap
import com.el_nico.cappasitytesttask.R
import com.google.android.material.snackbar.Snackbar

fun WeatherActivity.setupActionObserver() {
    viewModel.updateBackground.observe(this) {
        val image = it
        if (image is Bitmap) {
            viewModel.clearLeaveData()
            dataBinding.backgroundView.setImageBitmap(image)
        }
    }
}

fun WeatherActivity.readConfigFile(): String {
    val inputStream = resources.openRawResource(R.raw.config)
    return inputStream.bufferedReader().use { it.readText() }
}

fun WeatherActivity.showSnackBar(message: String) {
    val rootView = window.decorView.rootView
    Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
}

fun WeatherActivity.changeCityBackground(city: String) {
    //updateCityBackground("https://media.nomadicmatt.com/2020/bergen2.jpg")
    //viewModel.updateCityBackground("https://media.nomadicmatt.com/2020/bergen2.jpg")

    viewModel.changeCityBackground(city)
}