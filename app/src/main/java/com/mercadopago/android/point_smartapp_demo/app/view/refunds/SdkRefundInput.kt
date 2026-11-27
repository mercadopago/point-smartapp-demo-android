package com.mercadopago.android.point_smartapp_demo.app.view.refunds

import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundRequest

internal sealed interface SdkRefundInput {
    data class Valid(val request: RefundRequest) : SdkRefundInput
    object InvalidPaymentId : SdkRefundInput
    object InvalidAmount : SdkRefundInput
    object InvalidIdempotencyKey : SdkRefundInput
}

internal fun parseSdkRefundInput(
    paymentId: String,
    amountText: String,
    idempotencyKey: String,
): SdkRefundInput {
    if (paymentId.isEmpty() || !paymentId.all { it in '0'..'9' } || paymentId.all { it == '0' }) {
        return SdkRefundInput.InvalidPaymentId
    }
    val amount = amountText.takeIf { it.isNotEmpty() }?.toBigDecimalOrNull()
    if (amountText.isNotEmpty() && (amount == null || amount.signum() <= 0)) {
        return SdkRefundInput.InvalidAmount
    }
    if (idempotencyKey.isNotEmpty() && (idempotencyKey.isBlank() || idempotencyKey.length > 255)) {
        return SdkRefundInput.InvalidIdempotencyKey
    }
    return SdkRefundInput.Valid(
        RefundRequest(paymentId, amount, idempotencyKey.takeIf { it.isNotEmpty() }),
    )
}
