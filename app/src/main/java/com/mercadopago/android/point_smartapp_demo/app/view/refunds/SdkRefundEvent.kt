package com.mercadopago.android.point_smartapp_demo.app.view.refunds

import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundError
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundProgressStatus
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundResponse

internal sealed interface SdkRefundEvent {
    data class Progress(val status: RefundProgressStatus) : SdkRefundEvent
    data class Success(val response: RefundResponse) : SdkRefundEvent
    data class Error(val error: RefundError) : SdkRefundEvent
}
