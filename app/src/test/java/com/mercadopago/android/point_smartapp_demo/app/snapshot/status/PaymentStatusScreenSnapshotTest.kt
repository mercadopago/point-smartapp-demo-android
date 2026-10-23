package com.mercadopago.android.point_smartapp_demo.app.snapshot.status

import app.cash.paparazzi.Paparazzi
import androidx.compose.ui.res.stringResource
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PaymentMethod
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PaymentResponse
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.model.OnlinePaymentAmounts
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.model.OnlinePaymentData
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.model.OnlinePaymentDates
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.model.OnlinePaymentInstallments
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.model.OnlinePaymentMethod
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.model.OnlinePaymentStatus
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.model.OnlinePaymentStatusDetail
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.snapshot.SnapshotConfiguration
import com.mercadopago.android.point_smartapp_demo.app.view.payment.result.PaymentStatusResult
import com.mercadopago.android.point_smartapp_demo.app.view.payment.result.PaymentStatusScreen
import com.mercadopago.android.point_smartapp_demo.app.view.payment.result.toPaymentStatusResult
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoTheme
import java.math.BigDecimal
import org.junit.Rule
import org.junit.Test

class PaymentStatusScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = SnapshotConfiguration.N950,
        theme = SnapshotConfiguration.THEME,
        showSystemUi = false,
    )

    @Test
    fun initial() = snapshot(reference = "")

    @Test
    fun loading() = snapshot(online = true, loading = true)

    @Test
    fun error() = snapshot(online = true, errorMessage = R.string.point_smartapp_demo_app_payment_status_not_found_warning)

    @Test
    fun localResult() = snapshot(
        result = localPayment().toPaymentStatusResult(
            paparazzi.context.getString(R.string.point_smartapp_demo_app_payment_status_local_found),
        ),
    )

    @Test
    fun onlineResult() = snapshot(online = true, result = onlinePayment().toPaymentStatusResult())

    private fun snapshot(
        reference: String = EXTERNAL_REFERENCE,
        online: Boolean = false,
        loading: Boolean = false,
        result: PaymentStatusResult? = null,
        errorMessage: Int? = null,
    ) {
        paparazzi.snapshot {
            DemoTheme {
                PaymentStatusScreen(
                    reference = reference,
                    online = online,
                    loading = loading,
                    result = result,
                    errorMessage = errorMessage?.let { stringResource(it) },
                    onReferenceChange = {},
                    onModeChange = {},
                    onQuery = {},
                )
            }
        }
    }

    private fun localPayment() = PaymentResponse(
        paymentMethod = PaymentMethod.CREDIT_CARD,
        paymentReference = PAYMENT_ID,
        paymentCreationDate = PAYMENT_DATE,
        paymentAmount = BigDecimal(PAYMENT_AMOUNT),
        paymentInstallments = INSTALLMENT_COUNT.toString(),
        paymentBrandName = CARD_BRAND,
        paymentLastFourDigits = CARD_LAST_FOUR,
        tipAmount = BigDecimal.ZERO.toPlainString(),
        externalReference = EXTERNAL_REFERENCE,
    )

    private fun onlinePayment() = OnlinePaymentData(
        id = PAYMENT_ID,
        externalReference = EXTERNAL_REFERENCE,
        status = OnlinePaymentStatus.APPROVED,
        statusDetail = OnlinePaymentStatusDetail.ACCREDITED,
        description = PAYMENT_DESCRIPTION,
        amounts = OnlinePaymentAmounts(
            baseAmount = BigDecimal(PAYMENT_AMOUNT),
            tipAmount = BigDecimal.ZERO,
            transactionAmount = BigDecimal(PAYMENT_AMOUNT),
            totalPaidAmount = BigDecimal(PAYMENT_AMOUNT),
            transactionAmountRefunded = BigDecimal.ZERO,
            currencyId = CURRENCY_ID,
        ),
        installments = OnlinePaymentInstallments(count = INSTALLMENT_COUNT, amount = BigDecimal(PAYMENT_AMOUNT)),
        paymentMethod = OnlinePaymentMethod(
            paymentMethodId = PAYMENT_METHOD_ID,
            type = PAYMENT_METHOD_TYPE,
            lastFourDigits = CARD_LAST_FOUR,
        ),
        dates = OnlinePaymentDates(
            dateCreated = PAYMENT_DATE,
            dateApproved = PAYMENT_DATE,
            dateLastUpdated = PAYMENT_DATE,
        ),
    )

    private companion object {
        const val EXTERNAL_REFERENCE = "DEMO-001"
        const val PAYMENT_ID = "123456789"
        const val PAYMENT_AMOUNT = "100.00"
        const val PAYMENT_DATE = "2026-10-23T10:00:00Z"
        const val PAYMENT_DESCRIPTION = "Demo payment"
        const val INSTALLMENT_COUNT = 1
        const val CURRENCY_ID = "BRL"
        const val PAYMENT_METHOD_ID = "visa"
        const val PAYMENT_METHOD_TYPE = "credit_card"
        const val CARD_BRAND = "Visa"
        const val CARD_LAST_FOUR = "1111"
    }
}
