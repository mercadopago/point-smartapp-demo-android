package com.mercadopago.android.point_smartapp_demo.app.view.payment.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.PaymentMethodModel

internal class PaymentMethodAdapter(private val callback: (String) -> Unit) :
    ListAdapter<PaymentMethodModel, PaymentMethodViewHolder>(DiffCallback()) {

    private fun selectedItem(name: String) {
        var itemSelected: Boolean
        currentList.forEachIndexed { index, item ->
            itemSelected = item.name == name
            item.isSelected = itemSelected
            notifyItemChanged(index)
        }
    }

    fun clear() {
        submitList(emptyList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder = PaymentMethodViewHolder.from(parent)

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        holder.render(currentList[position]) {
            selectedItem(it)
            callback(it)
        }
    }
}

private class DiffCallback : DiffUtil.ItemCallback<PaymentMethodModel>() {
    override fun areItemsTheSame(oldItem: PaymentMethodModel, newItem: PaymentMethodModel): Boolean = oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: PaymentMethodModel, newItem: PaymentMethodModel): Boolean = oldItem == newItem
}
