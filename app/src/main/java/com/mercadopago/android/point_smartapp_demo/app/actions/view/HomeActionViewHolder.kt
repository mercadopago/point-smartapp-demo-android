package com.mercadopago.android.point_smartapp_demo.app.actions.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mercadopago.android.point_smartapp_demo.app.actions.contract.HomeActions
import com.mercadopago.android.point_smartapp_demo.app.actions.model.ActionModel
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointMainappDemoAppItemHomeActionBinding

class HomeActionViewHolder(private val binding: PointMainappDemoAppItemHomeActionBinding) : RecyclerView.ViewHolder(binding.root) {

    internal fun render(item: ActionModel, callback: (HomeActions) -> Unit) {
        binding.root.apply {
            text = item.title
            icon = item.icon

            setOnClickListener {
                callback(item.action)
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): HomeActionViewHolder {
            val binding = PointMainappDemoAppItemHomeActionBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return HomeActionViewHolder(binding)
        }
    }
}
