package com.mercadopago.android.point_smartapp_demo.app.view.payment.result

import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PaymentResponse
import com.mercadopago.android.point_smartapp_demo.app.R

internal fun PaymentResponse.toPaymentStatusResult(title: String): PaymentStatusResult = PaymentStatusResult(
    title = title,
    subtitle = R.string.point_smartapp_demo_app_payment_status_local_result,
    sections = listOf(
        PaymentStatusSection(
            title = R.string.point_smartapp_demo_app_payment_status_local_section_response,
            fields = listOf(
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_local_field_payment_reference,
                    paymentReference,
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_local_field_external_reference,
                    externalReference,
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_local_field_payment_amount,
                    paymentAmount.toString(),
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_local_field_tip_amount,
                    tipAmount,
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_local_field_installments,
                    paymentInstallments,
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_local_field_method,
                    paymentMethod.toString(),
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_local_field_brand_name,
                    paymentBrandName,
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_local_field_last_four_digits,
                    paymentLastFourDigits,
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_local_field_creation_date,
                    paymentCreationDate,
                ),
            ),
        ),
    ),
)
