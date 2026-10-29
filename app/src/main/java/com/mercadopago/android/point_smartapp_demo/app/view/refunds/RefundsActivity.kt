package com.mercadopago.android.point_smartapp_demo.app.view.refunds

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundErrorCode
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundException
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundResponse
import com.mercadolibre.android.point_integration_sdk.nativesdk.exception.SDKException
import com.mercadolibre.android.point_integration_sdk.nativesdk.exception.GenericErrorCode
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.util.hideKeyboard
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedback
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoField
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoMessage
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoPrimaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent

class RefundsActivity : AppCompatActivity() {

    private val viewModel by viewModels<RefundsViewModel>()
    private var selectedTab by mutableStateOf(0)

    // Existing public-API example.
    private var paymentId by mutableStateOf("")
    private var accessToken by mutableStateOf("")
    private var feedback by mutableStateOf<DemoFeedback?>(null)

    // New SDK example.
    private var sdkPaymentId by mutableStateOf("")
    private var sdkAmount by mutableStateOf("")
    private var sdkIdempotencyKey by mutableStateOf("")
    private var sdkLoading by mutableStateOf(false)
    private var sdkFeedback by mutableStateOf<DemoFeedback?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setDemoContent {
            DemoScreen(title = getString(R.string.point_smartapp_demo_app_button_refunds)) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(getString(R.string.point_smartapp_demo_app_refund_tab_api)) },
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(getString(R.string.point_smartapp_demo_app_refund_tab_sdk)) },
                    )
                }
                if (selectedTab == 0) {
                    PublicApiRefundTab()
                } else {
                    SdkRefundTab()
                }
            }
        }
        setupObservers()
    }

    @androidx.compose.runtime.Composable
    private fun PublicApiRefundTab() {
        Text(text = getString(R.string.point_smartapp_demo_app_description_refunds))
        DemoField(
            label = getString(R.string.point_smartapp_demo_app_lab_refund_payment_id),
            value = paymentId,
            onValueChange = { paymentId = it },
            keyboardType = KeyboardType.Number,
        )
        DemoField(
            label = getString(R.string.point_smartapp_demo_app_lab_access_token),
            value = accessToken,
            onValueChange = { accessToken = it },
            password = true,
        )
        DemoPrimaryButton(
            text = getString(R.string.point_smartapp_demo_app_lab_send_refund_action),
            onClick = {
                val parsedPaymentId = paymentId.toLongOrNull()?.takeIf { it > 0L }
                when {
                    parsedPaymentId == null -> feedback = DemoFeedback(
                        getString(R.string.point_smartapp_demo_app_refund_invalid_payment_id), true,
                    )
                    accessToken.isBlank() -> feedback = DemoFeedback(
                        getString(R.string.point_smartapp_demo_app_refund_access_token_required), true,
                    )
                    else -> {
                        feedback = null
                        viewModel.performRefund(parsedPaymentId, accessToken.trim())
                    }
                }
            },
        )
        feedback?.let { result ->
            DemoMessage(result.message, result.isError, onDismiss = { feedback = null })
        }
    }

    @androidx.compose.runtime.Composable
    private fun SdkRefundTab() {
        Text(text = getString(R.string.point_smartapp_demo_app_refund_sdk_description))
        DemoField(
            label = getString(R.string.point_smartapp_demo_app_lab_refund_payment_id),
            value = sdkPaymentId,
            onValueChange = { sdkPaymentId = it },
            keyboardType = KeyboardType.Number,
            enabled = !sdkLoading,
        )
        DemoField(
            label = getString(R.string.point_smartapp_demo_app_refund_sdk_amount),
            value = sdkAmount,
            onValueChange = { sdkAmount = it },
            keyboardType = KeyboardType.Decimal,
            enabled = !sdkLoading,
        )
        DemoField(
            label = getString(R.string.point_smartapp_demo_app_refund_sdk_key),
            value = sdkIdempotencyKey,
            onValueChange = { sdkIdempotencyKey = it },
            enabled = !sdkLoading,
        )
        DemoPrimaryButton(
            text = getString(R.string.point_smartapp_demo_app_refund_sdk_send),
            loading = sdkLoading,
            onClick = ::submitSdkRefund,
        )
        sdkFeedback?.let { result ->
            DemoMessage(result.message, result.isError, onDismiss = { sdkFeedback = null })
        }
    }

    private fun submitSdkRefund() {
        if (sdkLoading) return
        val input = parseSdkRefundInput(sdkPaymentId, sdkAmount, sdkIdempotencyKey)
        when (input) {
            SdkRefundInput.InvalidPaymentId -> {
                sdkFeedback = DemoFeedback(getString(R.string.point_smartapp_demo_app_refund_invalid_payment_id), true)
            }
            SdkRefundInput.InvalidAmount -> {
                sdkFeedback = DemoFeedback(getString(R.string.point_smartapp_demo_app_refund_sdk_invalid_amount), true)
            }
            SdkRefundInput.InvalidIdempotencyKey -> {
                sdkFeedback = DemoFeedback(getString(R.string.point_smartapp_demo_app_refund_sdk_invalid_key), true)
            }
            is SdkRefundInput.Valid -> {
                hideKeyboard()
                sdkFeedback = null
                sdkLoading = true
                viewModel.performSdkRefund(input.request)
            }
        }
    }

    private fun renderSdkEvent(event: SdkRefundEvent) {
        if (isFinishing || isDestroyed) return
        when (event) {
            is SdkRefundEvent.Progress -> {
                sdkFeedback = DemoFeedback(getString(R.string.point_smartapp_demo_app_refund_sdk_processing))
            }
            is SdkRefundEvent.Success -> renderSdkResponse(event.response, isError = false)
            is SdkRefundEvent.Error -> {
                val refundResponse = (event.error as? RefundException)?.response
                if (refundResponse != null) {
                    renderSdkResponse(refundResponse, isError = true)
                    if (event.error.code == RefundErrorCode.RESULT_TIMEOUT) {
                        sdkFeedback = DemoFeedback(
                            sdkFeedback?.message.orEmpty() + "\n" +
                                getString(R.string.point_smartapp_demo_app_refund_sdk_result_timeout),
                            isError = true,
                        )
                    }
                } else {
                    val message = when (event.error.code) {
                        GenericErrorCode.NOT_SUPPORTED -> getString(R.string.point_smartapp_demo_app_refund_sdk_not_supported)
                        RefundErrorCode.RESULT_TIMEOUT -> getString(R.string.point_smartapp_demo_app_refund_sdk_result_timeout)
                        else -> getString(R.string.point_smartapp_demo_app_refund_sdk_transport_error)
                    }
                    sdkFeedback = DemoFeedback(message, isError = true)
                }
            }
        }
    }

    private fun renderSdkResponse(result: RefundResponse, isError: Boolean) {
        sdkFeedback = DemoFeedback(
            getString(
                R.string.point_smartapp_demo_app_refund_sdk_result,
                result.status.name,
                result.refundId ?: getString(R.string.point_smartapp_demo_app_payment_status_not_available),
                result.amount?.toPlainString() ?: getString(R.string.point_smartapp_demo_app_payment_status_not_available),
                result.idempotencyKey ?: getString(R.string.point_smartapp_demo_app_payment_status_not_available),
                result.errorCode ?: getString(R.string.point_smartapp_demo_app_payment_status_not_available),
            ),
            isError = isError,
        )
    }

    private fun setupObservers() {
        viewModel.sdkLoading.observe(this) { sdkLoading = it }
        viewModel.sdkResult.observe(this) { response -> response?.let(::renderSdkEvent) }
        viewModel.successResult.observe(this) { result ->
            feedback = DemoFeedback(getString(R.string.point_smartapp_demo_app_lab_refund_result_text, result))
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
