package com.mercadopago.android.point_smartapp_demo.app.view.payment.result

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PaymentResponse
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedback
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedbackBanner
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoField
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoInfoRow
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoPrimaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent

class PaymentStatusApprovedActivity : AppCompatActivity() {

    private val paymentStatus = MPManager.paymentStatus
    private var paymentId by mutableStateOf("")
    private var paymentResponse by mutableStateOf<PaymentResponse?>(null)
    private var feedback by mutableStateOf<DemoFeedback?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = getString(R.string.point_smartapp_demo_app_lab_text_go_to_payment_status)
        val paymentIdLabel = getString(R.string.point_smartapp_demo_app_lab_hind_external_reference)
        val searchButtonLabel = getString(R.string.point_smartapp_demo_app_lab_text_go_to_payment_status)
        val labels = listOf(
            getString(R.string.point_smartapp_demo_app_lab_text_go_to_payment_status_amount),
            getString(R.string.point_smartapp_demo_app_lab_text_go_to_payment_status_payment_installment),
            getString(R.string.point_smartapp_demo_app_lab_text_go_to_payment_status_payment_method),
            getString(R.string.point_smartapp_demo_app_lab_text_go_to_payment_status_payment_reference),
            getString(R.string.point_smartapp_demo_app_lab_text_go_to_payment_status_payment_brand_name),
            getString(R.string.point_smartapp_demo_app_lab_text_go_to_payment_status_payment_creation_date),
            getString(R.string.point_smartapp_demo_app_lab_text_go_to_payment_status_payment_last_four_digits),
            getString(R.string.point_smartapp_demo_app_lab_text_go_to_payment_status_payment_tip_amount),
        )

        setDemoContent {
            DemoScreen(title = title) {
                DemoField(
                    label = paymentIdLabel,
                    value = paymentId,
                    onValueChange = { paymentId = it },
                    keyboardType = KeyboardType.Text,
                )
                DemoPrimaryButton(
                    text = searchButtonLabel,
                    onClick = { getPaymentStatus(paymentId) },
                )
                paymentResponse?.let { response ->
                    val values = listOf(
                        response.paymentAmount.toString(),
                        response.paymentInstallments.orEmpty(),
                        response.paymentMethod.toString(),
                        response.paymentReference.orEmpty(),
                        response.paymentBrandName.orEmpty(),
                        response.paymentCreationDate.orEmpty(),
                        response.paymentLastFourDigits.orEmpty(),
                        response.tipAmount.orEmpty(),
                    )
                    labels.zip(values).forEach { (label, value) -> DemoInfoRow(label, value) }
                }
                DemoFeedbackBanner(feedback) { feedback = null }
            }
        }
    }

    private fun getPaymentStatus(paymentId: String) {
        paymentStatus.getPaymentStatus(paymentId) { response ->
            response.doIfSuccess {
                paymentResponse = it
                feedback = null
            }
            response.doIfError {
                feedback = DemoFeedback(it.message ?: "Error", isError = true)
            }
        }
    }
}
