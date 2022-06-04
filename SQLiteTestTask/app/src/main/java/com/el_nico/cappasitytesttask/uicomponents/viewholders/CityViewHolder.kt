package com.el_nico.cappasitytesttask.uicomponents.viewholders

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.el_nico.cappasitytesttask.R
import com.el_nico.cappasitytesttask.ui.cityselection.SavedCitiesAdapter
import com.el_nico.cappasitytesttask.utils.database.WeatherDatabase
import com.el_nico.cappasitytesttask.utils.database.savedCityWithID
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class CityViewHolder(
    itemView: View,
    private var adapter: SavedCitiesAdapter,
    private val context: Context
): RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private var cityNameView: TextView = itemView.findViewById(R.id.cityNameView)

    private var updateDateView: TextView = itemView.findViewById(R.id.updateDateView)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        adapter.selectedIndex.value = adapterPosition
    }

    fun bind(id: Int) {
        WeatherDatabase.savedCityWithID(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Pair<String, String?>> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(pair: Pair<String, String?>) {
                    val lastUpdated = String.format(
                        context.resources.getString(R.string.last_updated),
                        pair.second ?: "--/--"
                    )

                    cityNameView.text = pair.first
                    updateDateView.text = lastUpdated
                }

                override fun onError(e: Throwable) {
                }
            })
    }
}