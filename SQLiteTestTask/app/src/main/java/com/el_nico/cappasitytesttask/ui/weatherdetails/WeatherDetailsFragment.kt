package com.el_nico.cappasitytesttask.ui.weatherdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.el_nico.cappasitytesttask.R
import com.el_nico.cappasitytesttask.databinding.FragmentWeatherDetailsBinding
import com.el_nico.cappasitytesttask.enums.WeatherDetailsType
import com.el_nico.cappasitytesttask.ui.weather.WeatherActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherDetailsFragment : Fragment() {

    internal lateinit var dataBinding: FragmentWeatherDetailsBinding

    internal val viewModel: WeatherDetailsViewModel by viewModels()

    override fun onStart() {
        super.onStart()

        val savedCityID = (activity as WeatherActivity).savedCityID
        if (savedCityID is Int) {
            viewModel.updateCityTitle(savedCityID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_weather_details, container, false)
        dataBinding.weatherDetailsViewModel = viewModel
        dataBinding.lifecycleOwner = this

        setupActionObserver()
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TabsAdapter(requireActivity())
        adapter.addFragment(createWeatherFragment(WeatherDetailsType.WEATHER), "Ahora")
        adapter.addFragment(createWeatherFragment(WeatherDetailsType.FORECAST), "Próximos 5 días")
        adapter.addFragment(createWeatherFragment(WeatherDetailsType.ONECALL), "Esta semana")

        dataBinding.viewpager.adapter = adapter
        dataBinding.viewpager.currentItem = 0

        val tabLayout = dataBinding.tabLayout
        val viewPager = dataBinding.viewpager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()
    }
}