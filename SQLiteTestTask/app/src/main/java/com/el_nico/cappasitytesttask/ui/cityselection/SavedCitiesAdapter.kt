package com.el_nico.cappasitytesttask.ui.cityselection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import com.el_nico.cappasitytesttask.R
import com.el_nico.cappasitytesttask.utils.CityDiffUtil
import com.el_nico.cappasitytesttask.uicomponents.viewholders.CityViewHolder
import javax.inject.Inject

class SavedCitiesAdapter @Inject constructor(): ListAdapter<Int, CityViewHolder>(CityDiffUtil()) {

    internal var selectedIndex: MutableLiveData<Int?> = MutableLiveData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        return CityViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_saved_city, parent, false),
            this, parent.context
        )
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Если уже содержит значение то при возврате на соотв. фрагмент произойдет лишнее срабатывание
     * обсервера (и как следствие всех привязанных действий)
     */
    fun clearLeaveData() {
        selectedIndex.value = null
    }
}