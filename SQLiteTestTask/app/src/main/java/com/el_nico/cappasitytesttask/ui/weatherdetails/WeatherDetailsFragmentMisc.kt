package com.el_nico.cappasitytesttask.ui.weatherdetails

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.el_nico.cappasitytesttask.R
import com.el_nico.cappasitytesttask.ui.weather.WeatherActivity
import com.el_nico.cappasitytesttask.ui.weather.changeCityBackground
import com.el_nico.cappasitytesttask.ui.weather.showSnackBar
import com.el_nico.cappasitytesttask.enums.WeatherDetailsType
import com.el_nico.cappasitytesttask.ui.currentweather.CurrentWeatherFragment
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun WeatherDetailsFragment.setupActionObserver() {
    viewModel.deleteSavedCity.observe(viewLifecycleOwner) {
        if (it) {
            viewModel.clearLeaveData()
            presentCityDeletionDialog()
        }
    }

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

    viewModel.returnAfterDeletion.observe(viewLifecycleOwner) {
        if (it) {
            viewModel.clearLeaveData()
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    viewModel.updateCityTitle.observe(viewLifecycleOwner) {
        val city = it
        if (city is String) {
            viewModel.clearLeaveData()
            (activity as WeatherActivity).dataBinding.toolbar.title = city
            (activity as WeatherActivity).changeCityBackground(city)
        }
    }
}

fun WeatherDetailsFragment.presentCityDeletionDialog() {
    val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Dialog_Alert)
    builder.setMessage(R.string.confirmation)
        .setPositiveButton(R.string.yes) { dialog, _ ->
            dialog.dismiss()
            deleteCityFromSavedAndReturn()
        }.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.dismiss()
        }.setOnCancelListener { dialog ->
            dialog.dismiss()
        }.create()
    builder.show()
}

fun WeatherDetailsFragment.deleteCityFromSavedAndReturn() {
    val activity = activity as WeatherActivity
    val savedCityID = activity.savedCityID
    if (savedCityID is Int) {
        viewModel.deleteCityFromSavedAndReturn(savedCityID)
    }
}

fun WeatherDetailsFragment.createWeatherFragment(type: WeatherDetailsType): CurrentWeatherFragment {
    val weatherFragment = CurrentWeatherFragment()

    val bundle = Bundle()
    bundle.putString("type", jacksonObjectMapper().writeValueAsString(type))
    weatherFragment.arguments = bundle

    return weatherFragment
}