package com.mercadopago.android.point_smartapp_demo.app.actions.view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.mercadopago.android.point_smartapp_demo.app.actions.contract.HomeActions
import com.mercadopago.android.point_smartapp_demo.app.actions.model.ActionModel

class HomeActionAdapter(private val callbacks: (HomeActions) -> Unit) :
    ListAdapter<ActionModel, HomeActionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeActionViewHolder.from(parent)

    override fun onBindViewHolder(holder: HomeActionViewHolder, position: Int) {
        holder.render(currentList[position], callbacks)
    }
}

private class DiffCallback : DiffUtil.ItemCallback<ActionModel>() {
    override fun areItemsTheSame(oldItem: ActionModel, newItem: ActionModel): Boolean = oldItem.title == newItem.title

    override fun areContentsTheSame(oldItem: ActionModel, newItem: ActionModel): Boolean = oldItem == newItem
}
