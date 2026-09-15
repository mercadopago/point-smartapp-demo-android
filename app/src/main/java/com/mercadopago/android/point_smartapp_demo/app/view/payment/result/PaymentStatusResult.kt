@file:Suppress("DEPRECATION")

package com.mercadopago.android.point_smartapp_demo.app.view.payment.result

import androidx.annotation.StringRes
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PaymentResponse
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.model.OnlinePaymentData
import com.mercadopago.android.point_smartapp_demo.app.R
import java.math.BigDecimal
import java.util.Locale

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

private fun BigDecimal?.displayValue(): String? = this?.toPlainString()

private fun Enum<*>.displayName(): String =
    name.lowercase(Locale.US).replaceFirstChar { it.titlecase(Locale.US) }.replace('_', ' ')
