package com.el_nico.cappasitytesttask.ui.cityselection

import android.app.AlertDialog
import android.graphics.Color
import android.view.View
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.el_nico.cappasitytesttask.R
import com.el_nico.cappasitytesttask.ui.weather.WeatherActivity
import com.el_nico.cappasitytesttask.ui.weather.readConfigFile
import com.el_nico.cappasitytesttask.ui.weather.showSnackBar
import com.el_nico.cappasitytesttask.uicomponents.views.CityPreviewView

fun CitySelectionFragment.setupActionObserver() {
    viewModel.presentCitySelection.observe(viewLifecycleOwner) {
        if (it) {
            viewModel.clearLeaveData()
            presentCityAdditionDialog()
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

    viewModel.presentSavedCity.observe(viewLifecycleOwner) {
        val city = it
        if (city is Triple) {
            viewModel.clearLeaveData()
            adapter.clearLeaveData()

            val latitude = city.second
            val longitude = city.third

            if (latitude is Double && longitude is Double
                && !(latitude == 0.0 && longitude == 0.0)) {
                presentSelectedCityDialog(city.first, latitude, longitude)
            } else {
                viewModel.requestLatLonAndContinue(requireContext(), city.first)
            }
        }
    }

    viewModel.presentChangeSavedCityID.observe(viewLifecycleOwner) {
        val savedCityID = it
        if (savedCityID is Int) {
            viewModel.clearLeaveData()
            (activity as WeatherActivity).savedCityID = savedCityID
        }
    }

    viewModel.updateList.observe(viewLifecycleOwner) {
        val list = it
        if (list is ArrayList) {
            viewModel.clearLeaveData()
            adapter.submitList(list)
        }
    }

    viewModel.presentSelectedCityDialog.observe(viewLifecycleOwner) {
        val city = it
        if (city is Triple) {
            viewModel.clearLeaveData()
            presentSelectedCityDialog(city.first, city.second, city.third)
        }
    }

    viewModel.saveCityToDB.observe(viewLifecycleOwner) {
        val city = it
        if (city is String) {
            viewModel.clearLeaveData()
            viewModel.updateList()

            adapter.clearLeaveData()
            viewModel.idForCityWithName(city)
        }
    }

    adapter.selectedIndex.observe(viewLifecycleOwner) {
        if (it is Int) {
            adapter.clearLeaveData()
            viewModel.idForCityWithIndex(it)
        }
    }
}

fun CitySelectionFragment.setupList() {
    dataBinding.recyclerView.layoutManager = LinearLayoutManager(context)
    dataBinding.recyclerView.adapter = adapter

    viewModel.updateList()
}

fun CitySelectionFragment.presentCityAdditionDialog() {
    val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Dialog_Alert)
    builder.setMessage(R.string.btn_add_city)

    val editText = EditText(requireContext())
    editText.setHint(R.string.btn_city_hint)
    editText.setTextColor(Color.WHITE)
    editText.textAlignment = View.TEXT_ALIGNMENT_CENTER
    builder.setView(editText)

    builder.setPositiveButton(R.string.btn_ok) { dialog, _ ->
        val city = editText.text.toString()
        viewModel.checkIfCityIsAlreadySavedAndContinue(requireContext(), city)
        dialog.dismiss()
    }.setNegativeButton(R.string.btn_cancel) { dialog, _ ->
        dialog.dismiss()
    }.setOnCancelListener { dialog ->
        dialog.dismiss()
    }.create()
    builder.show()
}

fun CitySelectionFragment.presentSelectedCityDialog(city: String, latitude: Double, longitude: Double) {
    val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Dialog_Alert)
    builder.setMessage(city)

    val preview = CityPreviewView(requireContext())
    preview.moveMapToPosition(latitude, longitude)
    builder.setView(preview)

    builder.setPositiveButton(R.string.btn_select) { dialog, _ ->
        dialog.dismiss()
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }.setNegativeButton(R.string.btn_cancel) { dialog, _ ->
        dialog.dismiss()
    }.setOnCancelListener { dialog ->
        dialog.dismiss()
    }.create()
    builder.show()
}

fun CitySelectionFragment.initializeDatabaseAndContinue() {
    viewModel.dbInitialized.observe(viewLifecycleOwner) {
        dataBinding.nowLoading.visibility = View.GONE

        if (it) {
            setupList()
        } else {
            (activity as WeatherActivity).showSnackBar("DB error")
        }
    }

    viewModel.initWeatherDatabase(requireContext(), (activity as WeatherActivity).readConfigFile())
}