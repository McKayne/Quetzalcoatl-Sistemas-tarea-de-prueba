package com.el_nico.cappasitytesttask.ui.currentweather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.el_nico.cappasitytesttask.R
import com.el_nico.cappasitytesttask.databinding.FragmentCurrentWeatherBinding
import com.el_nico.cappasitytesttask.enums.WeatherDetailsType
import com.el_nico.cappasitytesttask.ui.weather.WeatherActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrentWeatherFragment : Fragment() {

    private lateinit var type: WeatherDetailsType

    internal lateinit var dataBinding: FragmentCurrentWeatherBinding

    internal val viewModel: CurrentWeatherViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_current_weather, container, false)
        dataBinding.currentWeatherViewModel = viewModel
        dataBinding.lifecycleOwner = this

        val typeArgument = arguments?.getString("type")
        if (typeArgument is String) {
            type = jacksonObjectMapper().readValue(typeArgument)
            viewModel.type = type
        } else {
            viewModel.type = WeatherDetailsType.WEATHER
        }

        setupActionObserver()
        return dataBinding.root
    }

    override fun onResume() {
        super.onResume()

        val activity = activity as WeatherActivity
        val savedCityID = activity.savedCityID
        if (savedCityID is Int) {
            dataBinding.weatherLayout.visibility = View.GONE
            dataBinding.forecastLayout.visibility = View.GONE
            dataBinding.oneCallLayout.visibility = View.GONE
            when (type) {
                WeatherDetailsType.WEATHER -> {
                    dataBinding.weatherLayout.visibility = View.VISIBLE
                    viewModel.presentWeatherFromDatabase(savedCityID, true)
                }
                WeatherDetailsType.FORECAST -> {
                    dataBinding.forecastLayout.visibility = View.VISIBLE
                    viewModel.presentForecastFromDatabase(savedCityID, true)
                }
                WeatherDetailsType.ONECALL -> {
                    dataBinding.oneCallLayout.visibility = View.VISIBLE
                    viewModel.presentOneCallFromDatabase(savedCityID, true)
                }
            }
        }
    }
}