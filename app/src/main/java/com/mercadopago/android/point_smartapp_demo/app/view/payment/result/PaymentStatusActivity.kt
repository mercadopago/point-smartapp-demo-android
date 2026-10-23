package com.mercadopago.android.point_smartapp_demo.app.view.payment.result

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.exception.SDKException
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.model.OnlinePaymentData
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.provider.OnlinePaymentStatusCallback
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.provider.OnlinePaymentStatusErrorCode
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIf
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.util.hideKeyboard
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent

class PaymentStatusActivity : AppCompatActivity() {
    private val paymentStatus = MPManager.paymentStatus
    private var loading by mutableStateOf(false)
    private var result by mutableStateOf<PaymentStatusResult?>(null)
    private var errorMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDemoContent {
            var reference by rememberSaveable { mutableStateOf("") }
            var online by rememberSaveable { mutableStateOf(false) }
            PaymentStatusScreen(
                reference = reference,
                online = online,
                loading = loading,
                result = result,
                errorMessage = errorMessage,
                onReferenceChange = { reference = it.take(128) },
                onModeChange = { selectedOnline ->
                    if (online != selectedOnline) {
                        online = selectedOnline
                        clearResult()
                    }
                },
                onQuery = { queryPaymentStatus(reference, online) },
            )
        }
    }

    private fun queryPaymentStatus(reference: String, online: Boolean) {
        if (loading) return
        if (reference.isBlank()) {
            errorMessage = getString(R.string.point_smartapp_demo_app_payment_status_required)
            return
        }
        hideKeyboard()
        clearResult()
        loading = true
        if (online) queryOnlinePayment(reference) else queryLocalPayment(reference)
    }

    private fun queryLocalPayment(reference: String) {
        paymentStatus.getPaymentStatus(reference) { response ->
            response.doIf(
                successCallback = { result ->
                    showOnUiThread {
                        renderResult(
                            result.toPaymentStatusResult(
                                getString(R.string.point_smartapp_demo_app_payment_status_local_found),
                            ),
                        )
                    }
                },
                errorCallback = { error -> showOnUiThread { renderError(error.message) } },
            )
        }
    }

    private fun queryOnlinePayment(externalReference: String) {
        paymentStatus.getOnlinePaymentStatus(
            externalReference,
            object : OnlinePaymentStatusCallback {
                override fun onSuccess(data: OnlinePaymentData) {
                    showOnUiThread { renderResult(data.toPaymentStatusResult()) }
                }

                override fun onError(error: SDKException) {
                    showOnUiThread { renderOnlineError(error.code) }
                }
            },
        )
    }

    private fun renderOnlineError(code: Int) {
        loading = false
        result = null
        errorMessage = when (code) {
            OnlinePaymentStatusErrorCode.LOCAL_PAYMENT_NOT_FOUND,
            OnlinePaymentStatusErrorCode.PAYMENT_NOT_FOUND ->
                getString(R.string.point_smartapp_demo_app_payment_status_not_found_warning)
            else -> getString(R.string.point_smartapp_demo_app_payment_status_error_code, code.toString())
        }
    }

    private fun renderResult(payment: PaymentStatusResult) {
        loading = false
        errorMessage = null
        result = payment
    }

    private fun renderError(rawCode: String?) {
        loading = false
        result = null
        val code = rawCode?.takeIf(KNOWN_ERROR_CODES::contains)
        errorMessage = when (code) {
            ERROR_LOCAL_PAYMENT_NOT_FOUND, ERROR_PAYMENT_NOT_FOUND ->
                getString(R.string.point_smartapp_demo_app_payment_status_not_found_warning)
            null -> getString(R.string.point_smartapp_demo_app_payment_status_error)
            else -> getString(R.string.point_smartapp_demo_app_payment_status_error_code, code)
        }
    }

    private fun clearResult() {
        result = null
        errorMessage = null
    }

    private fun showOnUiThread(action: () -> Unit) {
        runOnUiThread {
            if (!isFinishing && !isDestroyed) action()
        }
    }

    private companion object {
        const val ERROR_INVALID_EXTERNAL_REFERENCE = "invalid_external_reference"
        const val ERROR_LOCAL_PAYMENT_NOT_FOUND = "local_payment_not_found"
        const val ERROR_INVALID_LOCAL_PAYMENT_ID = "invalid_local_payment_id"
        const val ERROR_UNAUTHORIZED = "unauthorized"
        const val ERROR_PAYMENT_NOT_FOUND = "payment_not_found"
        const val ERROR_NO_INTERNET = "no_internet"
        const val ERROR_UNSUPPORTED_PAYMENT_STATUS = "unsupported_payment_status"
        const val ERROR_REQUEST_IN_PROGRESS = "request_in_progress"
        const val ERROR_UNEXPECTED = "unexpected_error"
        val KNOWN_ERROR_CODES = setOf(
            ERROR_INVALID_EXTERNAL_REFERENCE,
            ERROR_LOCAL_PAYMENT_NOT_FOUND,
            ERROR_INVALID_LOCAL_PAYMENT_ID,
            ERROR_UNAUTHORIZED,
            ERROR_PAYMENT_NOT_FOUND,
            ERROR_NO_INTERNET,
            ERROR_UNSUPPORTED_PAYMENT_STATUS,
            ERROR_REQUEST_IN_PROGRESS,
            ERROR_UNEXPECTED,
        )
    }
}
