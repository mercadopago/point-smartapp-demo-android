package com.mercadopago.android.point_smartapp_demo.app.view.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.InstallmentAmount
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointSmartappDemoAppItemInstallmentAmountBinding

internal class PaymentInstallmentViewHolder(private val binding: PointSmartappDemoAppItemInstallmentAmountBinding) :
    RecyclerView.ViewHolder(binding.root) {

    internal fun onRender(item: InstallmentAmount, callback: (InstallmentAmount) -> Unit) {
        binding.apply {
            "${item.installment}X ${item.amount}".also { textItemInstallment.text = it }
            root.setOnClickListener {
                callback(item)
            }
        }
    }

    internal companion object {
        fun from(parent: ViewGroup): PaymentInstallmentViewHolder {
            val binding = PointSmartappDemoAppItemInstallmentAmountBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return PaymentInstallmentViewHolder(binding)
        }
    }
}
