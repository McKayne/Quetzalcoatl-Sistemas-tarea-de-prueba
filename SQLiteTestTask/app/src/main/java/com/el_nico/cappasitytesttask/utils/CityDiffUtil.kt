package com.el_nico.cappasitytesttask.utils

import androidx.recyclerview.widget.DiffUtil

class CityDiffUtil: DiffUtil.ItemCallback<Int>() {

    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }
}