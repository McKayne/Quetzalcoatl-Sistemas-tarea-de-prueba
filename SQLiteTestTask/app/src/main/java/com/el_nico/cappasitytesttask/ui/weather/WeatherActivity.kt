package com.el_nico.cappasitytesttask.ui.weather

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.el_nico.cappasitytesttask.R
import com.el_nico.cappasitytesttask.databinding.ActivityWeatherBinding
import com.el_nico.cappasitytesttask.utils.networking.ImageNetworking
import com.el_nico.cappasitytesttask.utils.networking.Networking
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherActivity: AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    internal lateinit var dataBinding: ActivityWeatherBinding

    internal val viewModel by viewModels<WeatherViewModel>()

    internal var savedCityID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        Networking.init(readConfigFile())
        ImageNetworking.init()

        dataBinding = ActivityWeatherBinding.inflate(layoutInflater)
        dataBinding.weatherViewModel = viewModel
        dataBinding.lifecycleOwner = this
        setupActionObserver()

        setContentView(dataBinding.root)
        setSupportActionBar(dataBinding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}