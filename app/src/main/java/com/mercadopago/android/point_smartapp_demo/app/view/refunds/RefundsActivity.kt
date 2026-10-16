package com.mercadopago.android.point_smartapp_demo.app.view.refunds

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.KeyboardType
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoMessage
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedback
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoField
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoPrimaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent

class RefundsActivity : AppCompatActivity() {

    private val viewModel by viewModels<RefundsViewModel>()
    private var paymentId by mutableStateOf("")
    private var accessToken by mutableStateOf("")
    private var feedback by mutableStateOf<DemoFeedback?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE,
        )
        val title = getString(R.string.point_smartapp_demo_app_button_refunds)
        val description = getString(R.string.point_smartapp_demo_app_description_refunds)
        val paymentIdLabel = getString(R.string.point_smartapp_demo_app_lab_refund_payment_id)
        val accessTokenLabel = getString(R.string.point_smartapp_demo_app_lab_access_token)
        val refundLabel = getString(R.string.point_smartapp_demo_app_lab_send_refund_action)

        setDemoContent {
            DemoScreen(title = title) {
                Text(text = description)
                DemoField(
                    label = paymentIdLabel,
                    value = paymentId,
                    onValueChange = { paymentId = it },
                    keyboardType = KeyboardType.Number,
                )
                DemoField(
                    label = accessTokenLabel,
                    value = accessToken,
                    onValueChange = { accessToken = it },
                    password = true,
                )
                DemoPrimaryButton(
                    text = refundLabel,
                    onClick = {
                        val parsedPaymentId = paymentId.toLongOrNull()?.takeIf { it > 0L }
                        when {
                            parsedPaymentId == null -> feedback = DemoFeedback(
                                getString(R.string.point_smartapp_demo_app_refund_invalid_payment_id),
                                isError = true,
                            )
                            accessToken.isBlank() -> feedback = DemoFeedback(
                                getString(R.string.point_smartapp_demo_app_refund_access_token_required),
                                isError = true,
                            )
                            else -> {
                                feedback = null
                                viewModel.performRefund(parsedPaymentId, accessToken.trim())
                            }
                        }
                    },
                )
                feedback?.let { result ->
                    DemoMessage(
                        message = result.message,
                        isError = result.isError,
                        onDismiss = { feedback = null },
                    )
                }
            }
        }
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.successResult.observe(this) { result ->
            feedback = DemoFeedback(String.format(
                resources.getString(R.string.point_smartapp_demo_app_lab_refund_result_text),
                result,
            ))
        }
        viewModel.errorResult.observe(this) { errorCode ->
            val message = if (errorCode == 0) {
                getString(R.string.point_smartapp_demo_app_refund_failed_connection)
            } else {
                getString(R.string.point_smartapp_demo_app_refund_failed_http, errorCode)
            }
            feedback = DemoFeedback(message, isError = true)
        }
    }
}
