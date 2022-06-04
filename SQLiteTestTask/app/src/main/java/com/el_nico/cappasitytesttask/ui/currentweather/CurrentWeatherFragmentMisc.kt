package com.el_nico.cappasitytesttask.ui.currentweather

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.el_nico.cappasitytesttask.R
import com.el_nico.cappasitytesttask.ui.weather.WeatherActivity
import com.el_nico.cappasitytesttask.ui.weather.showSnackBar
import com.el_nico.cappasitytesttask.domain.entity.database.ForecastQueryResult
import com.el_nico.cappasitytesttask.domain.entity.database.OneCallQueryResult
import com.el_nico.cappasitytesttask.domain.entity.database.WeatherQueryResult
import java.text.SimpleDateFormat
import java.util.*

fun CurrentWeatherFragment.setupActionObserver() {
    viewModel.presentLoadingIndicator.observe(viewLifecycleOwner) {
        dataBinding.nowLoading.visibility = if (it) View.VISIBLE else View.GONE
    }

    viewModel.presentMessage.observe(viewLifecycleOwner) {
        val message = it
        if (message is String) {
            viewModel.clearLeaveData()
            (activity as WeatherActivity).showSnackBar(message)
        }
    }

    viewModel.updateWeather.observe(viewLifecycleOwner) {
        val result = it
        if (result is WeatherQueryResult) {
            viewModel.clearLeaveData()
            updateWeatherUI(result)
        }
    }

    viewModel.updateForecast.observe(viewLifecycleOwner) {
        val result = it
        if (result is Collection) {
            viewModel.clearLeaveData()
            updateForecastUI(result)
        }
    }

    viewModel.updateOneCall.observe(viewLifecycleOwner) {
        val result = it
        if (result is Collection) {
            viewModel.clearLeaveData()
            updateOneCallUI(result)
        }
    }

    viewModel.presentWeatherFromDatabase.observe(viewLifecycleOwner) {
        val savedCityID = it
        if (savedCityID is Int) {
            viewModel.clearLeaveData()
            viewModel.presentWeatherFromDatabase(savedCityID, false)
        }
    }

    viewModel.presentForecastFromDatabase.observe(viewLifecycleOwner) {
        val savedCityID = it
        if (savedCityID is Int) {
            viewModel.clearLeaveData()
            viewModel.presentForecastFromDatabase(savedCityID, false)
        }
    }

    viewModel.presentOneCallFromDatabase.observe(viewLifecycleOwner) {
        val savedCityID = it
        if (savedCityID is Int) {
            viewModel.clearLeaveData()
            viewModel.presentOneCallFromDatabase(savedCityID, false)
        }
    }
}

fun CurrentWeatherFragment.updateWeatherUI(result: WeatherQueryResult) {
    dataBinding.tempView.text = String.format(requireContext().resources
        .getString(R.string.temperature), result.temperature)
    dataBinding.feelsLikeView.text = String.format(requireContext().resources
        .getString(R.string.temperature), result.feelsLike)
    dataBinding.minTempView.text = String.format(requireContext().resources
        .getString(R.string.temperature), result.tempMin)
    dataBinding.maxTempView.text = String.format(requireContext().resources
        .getString(R.string.temperature), result.tempMax)
    dataBinding.visibilityView.text = String.format(requireContext().resources
        .getString(R.string.visibility), result.visibility)
    dataBinding.windSpeedView.text = String.format(requireContext().resources
        .getString(R.string.wind_speed), result.windSpeed)
    dataBinding.windDegView.text = String.format(requireContext().resources
        .getString(R.string.wind_deg), result.windDeg)
}

fun CurrentWeatherFragment.updateForecastUI(result: Collection<ForecastQueryResult>) {
    dataBinding.forecastLayout.removeAllViews()

    for (forecast in result) {
        val dt = forecast.dt.toIntOrNull()
        if (dt is Int) {
            val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            addTextView(
                dataBinding.forecastLayout,
                "Tiempo:",
                format.format(Date(dt.toLong() * 1000))
            )
        }

        addTextView(dataBinding.forecastLayout, "Temperatura:", "${forecast.temp}°C")
        addTextView(dataBinding.forecastLayout, "Sintiendo como:", "${forecast.feelsLike}°C")
        addTextView(dataBinding.forecastLayout, "Temperatura mín.:", "${forecast.tempMin}°C")
        addTextView(dataBinding.forecastLayout, "Temperatura máx.:", "${forecast.tempMax}°C")
        addTextView(dataBinding.forecastLayout, "Humedad del aire:", "${forecast.humidity}%")
        addTextView(dataBinding.forecastLayout, "Presión atmosférico:", "${forecast.pressure} mmHg")
    }
}

fun CurrentWeatherFragment.updateOneCallUI(result: Collection<OneCallQueryResult>) {
    dataBinding.oneCallLayout.removeAllViews()

    for (oneCall in result) {
        val dt = oneCall.dt.toIntOrNull()
        if (dt is Int) {
            val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            addTextView(dataBinding.oneCallLayout, "Tiempo:", format.format(Date(dt.toLong() * 1000)))
        }

        addTextView(dataBinding.oneCallLayout, "Temperatura:", "${oneCall.temperature}°C")
        addTextView(dataBinding.oneCallLayout, "Sintiendo como:", "${oneCall.feelsLike}°C")
        addTextView(dataBinding.oneCallLayout, "Temperatura mín.:", "${oneCall.tempMin}°C")
        addTextView(dataBinding.oneCallLayout, "Temperatura máx.:", "${oneCall.tempMax}°C")
        addTextView(dataBinding.oneCallLayout, "Humedad del aire:", "${oneCall.humidity}%")
        addTextView(dataBinding.oneCallLayout, "Presión atmosférico:", "${oneCall.pressure} mmHg")

        val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        addTextView(dataBinding.oneCallLayout, "Salida del sol:", format.format(Date(oneCall.sunrise.toLong() * 1000)))
        addTextView(dataBinding.oneCallLayout, "Puesta del sol:", format.format(Date(oneCall.sunset.toLong() * 1000)))
        addTextView(dataBinding.oneCallLayout, "Hay luna desde:", format.format(Date(oneCall.moonrise.toLong() * 1000)))
        addTextView(dataBinding.oneCallLayout, "Hay luna hasta:", format.format(Date(oneCall.moonset.toLong() * 1000)))
    }
}

fun CurrentWeatherFragment.addTextView(layout: LinearLayout?, title: String, text: String) {
    val titleView = TextView(requireContext())
    //textView.setTextAppearance(android.R.style.TextAppearance_Material3_BodyLarge)
    titleView.setTextAppearance(android.R.style.TextAppearance_Material_Large)
    titleView.textSize = 24f
    titleView.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    titleView.text = title
    layout?.addView(titleView)

    val textView = TextView(requireContext())
    textView.setTextAppearance(android.R.style.TextAppearance_Material_Small)
    textView.textSize = 16f
    textView.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    textView.text = text
    layout?.addView(textView)
}