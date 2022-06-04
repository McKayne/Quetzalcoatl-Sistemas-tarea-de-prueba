package com.el_nico.cappasitytesttask.uicomponents.views

import android.content.Context
import androidx.preference.PreferenceManager
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.el_nico.cappasitytesttask.BuildConfig
import com.el_nico.cappasitytesttask.R
import com.el_nico.cappasitytesttask.databinding.LayoutCityPreviewBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class CityPreviewView(context: Context,
                      attrs: AttributeSet? = null,
                      defStyleAttr: Int = 0): ConstraintLayout(context, attrs, defStyleAttr) {

    val dataBinding: LayoutCityPreviewBinding = LayoutCityPreviewBinding.bind(
        View.inflate(context, R.layout.layout_city_preview, this))

    init {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        dataBinding.osmMap.setTileSource(TileSourceFactory.MAPNIK)
        dataBinding.osmMap.onResume()
    }

    fun moveMapToPosition(latitude: Double, longitude: Double) {
        val geoPoint = GeoPoint(latitude, longitude)
        moveMapToSearchResults(geoPoint)
    }

    private fun moveMapToSearchResults(location: GeoPoint) {
        dataBinding.osmMap.controller.animateTo(location)
        dataBinding.osmMap.controller.setZoom(12.0)

        val marker = Marker(dataBinding.osmMap)
        marker.position = location
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        dataBinding.osmMap.overlays.add(marker)
    }
}