package com.mercadopago.android.point_smartapp_demo.app.view.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointMainappDemoAppItemPaymentMethodBinding
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.PaymentMethodModel

class PaymentMethodViewHolder(private val binding: PointMainappDemoAppItemPaymentMethodBinding) : RecyclerView.ViewHolder(binding.root) {

    internal fun render(item: PaymentMethodModel, callback: (String) -> Unit) {
        binding.apply {
            imageviewItemSelected.isVisible = item.isSelected
            textviewPaymentMethodName.text = item.name

            root.setOnClickListener {
                callback(item.name)
            }
        }
    }

    internal companion object {
        fun from(parent: ViewGroup): PaymentMethodViewHolder {
            val binding = PointMainappDemoAppItemPaymentMethodBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return PaymentMethodViewHolder(binding)
        }
    }
}
