package com.mercadopago.android.point_smartapp_demo.app.view.payment.result

import androidx.annotation.StringRes

internal data class PaymentStatusResult(
    val title: String,
    @StringRes val subtitle: Int,
    val sections: List<PaymentStatusSection>,
)

internal data class PaymentStatusSection(
    @StringRes val title: Int,
    val fields: List<PaymentStatusField>,
)

internal data class PaymentStatusField(
    @StringRes val label: Int,
    val value: String?,
)
