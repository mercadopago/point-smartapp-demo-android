package com.mercadopago.android.point_smartapp_demo.app.view.payment.models

import com.mercadopago.android.point_integration_sdk.nativesdk.payment.data.PayerCondition
import com.mercadopago.android.point_integration_sdk.nativesdk.payment.data.Tax

internal typealias PayerConditionString = String

internal fun PayerConditionString.toTaxes() = PayerCondition.fromString(this)
    ?.let { payerCondition ->
        listOf(Tax(payerCondition))
    }
