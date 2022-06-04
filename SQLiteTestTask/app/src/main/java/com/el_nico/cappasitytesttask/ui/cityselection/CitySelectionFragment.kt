package com.el_nico.cappasitytesttask.ui.cityselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.el_nico.cappasitytesttask.R
import com.el_nico.cappasitytesttask.databinding.FragmentCitySelectionBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CitySelectionFragment: Fragment() {

    internal lateinit var dataBinding: FragmentCitySelectionBinding

    internal val viewModel: CitySelectionViewModel by viewModels()

    @Inject
    lateinit var adapter: SavedCitiesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_city_selection, container, false)

        dataBinding.citySelectionViewModel = viewModel
        dataBinding.lifecycleOwner = this

        setupActionObserver()
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isDatabaseInitialized = viewModel.dbInitialized.value
        if (isDatabaseInitialized is Boolean && isDatabaseInitialized) {
            setupList()
        } else {
            initializeDatabaseAndContinue()
        }
    }
}