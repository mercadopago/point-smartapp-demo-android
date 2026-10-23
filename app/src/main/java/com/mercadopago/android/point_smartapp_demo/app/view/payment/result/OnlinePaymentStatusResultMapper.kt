package com.mercadopago.android.point_smartapp_demo.app.view.payment.result

import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.model.OnlinePaymentData
import com.mercadopago.android.point_smartapp_demo.app.R
import java.math.BigDecimal
import java.util.Locale

internal fun OnlinePaymentData.toPaymentStatusResult(): PaymentStatusResult = PaymentStatusResult(
    title = status.displayName(),
    subtitle = R.string.point_smartapp_demo_app_payment_status_online_result,
    sections = listOf(
        PaymentStatusSection(
            title = R.string.point_smartapp_demo_app_payment_status_online_section_payment,
            fields = listOf(
                PaymentStatusField(R.string.point_smartapp_demo_app_payment_status_online_field_id, id),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_external_reference,
                    externalReference,
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_status,
                    status.displayName(),
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_status_detail,
                    statusDetail?.displayName(),
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_description,
                    description,
                ),
            ),
        ),
        PaymentStatusSection(
            title = R.string.point_smartapp_demo_app_payment_status_online_section_amounts,
            fields = listOf(
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_base_amount,
                    amounts.baseAmount.displayValue(),
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_tip_amount,
                    amounts.tipAmount.displayValue(),
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_transaction_amount,
                    amounts.transactionAmount.displayValue(),
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_total_paid_amount,
                    amounts.totalPaidAmount.displayValue(),
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_refunded_amount,
                    amounts.transactionAmountRefunded.displayValue(),
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_currency,
                    amounts.currencyId,
                ),
            ),
        ),
        PaymentStatusSection(
            title = R.string.point_smartapp_demo_app_payment_status_online_section_installments,
            fields = listOf(
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_installment_count,
                    installments?.count?.toString(),
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_installment_amount,
                    installments?.amount.displayValue(),
                ),
            ),
        ),
        PaymentStatusSection(
            title = R.string.point_smartapp_demo_app_payment_status_online_section_method,
            fields = listOf(
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_method_id,
                    paymentMethod.paymentMethodId,
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_method_type,
                    paymentMethod.type,
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_last_four_digits,
                    paymentMethod.lastFourDigits,
                ),
            ),
        ),
        PaymentStatusSection(
            title = R.string.point_smartapp_demo_app_payment_status_online_section_dates,
            fields = listOf(
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_date_created,
                    dates.dateCreated,
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_date_approved,
                    dates.dateApproved,
                ),
                PaymentStatusField(
                    R.string.point_smartapp_demo_app_payment_status_online_field_date_updated,
                    dates.dateLastUpdated,
                ),
            ),
        ),
    ),
)

private fun BigDecimal?.displayValue(): String? = this?.toPlainString()

private fun Enum<*>.displayName(): String =
    name.lowercase(Locale.US).replaceFirstChar { it.titlecase(Locale.US) }.replace('_', ' ')
