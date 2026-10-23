package com.mercadopago.android.point_smartapp_demo.app.view.sso

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.exception.SDKException
import com.mercadolibre.android.point_integration_sdk.nativesdk.sso.provider.IdentityTokenResponseData
import com.mercadolibre.android.point_integration_sdk.nativesdk.sso.provider.SingleSignOnCallback
import com.mercadolibre.android.point_integration_sdk.nativesdk.sso.provider.SingleSignOnStatus
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoInfoRow
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoMessage
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoPrimaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent
import java.util.UUID

class SingleSignOnActivity : AppCompatActivity() {
    private var loading by mutableStateOf(false)
    private var statusMessage by mutableStateOf<String?>(null)
    private var statusIsError by mutableStateOf(false)
    private var identityToken by mutableStateOf<String?>(null)
    private val singleSignOnTools = MPManager.singleSignOnTools

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDemoContent {
            DemoScreen(title = getString(R.string.point_smartapp_demo_app_sso_title)) {
                DemoPrimaryButton(
                    text = getString(R.string.point_smartapp_demo_app_sso_request),
                    onClick = ::requestIdentityToken,
                    loading = loading,
                )
                statusMessage?.let { DemoMessage(message = it, isError = statusIsError) }
                identityToken?.let { token ->
                    SelectionContainer {
                        DemoInfoRow(
                            label = getString(R.string.point_smartapp_demo_app_sso_identity_token),
                            value = token,
                        )
                    }
                }
            }
        }
    }

    private fun requestIdentityToken() {
        if (loading) return
        showStatus(getString(R.string.point_smartapp_demo_app_sso_processing), isLoading = true)
        singleSignOnTools.requestIdentityToken(
            nonce = UUID.randomUUID().toString(),
            callback = object : SingleSignOnCallback {
                override fun onProgress(status: SingleSignOnStatus) {
                    val message = when (status) {
                        SingleSignOnStatus.PROCESSING -> R.string.point_smartapp_demo_app_sso_processing
                        SingleSignOnStatus.WAITING_FOR_USER_AUTHORIZATION -> R.string.point_smartapp_demo_app_sso_waiting
                    }
                    showStatus(getString(message), isLoading = true)
                }

                override fun onSuccess(response: IdentityTokenResponseData) {
                    showStatus(
                        getString(R.string.point_smartapp_demo_app_sso_success),
                        isLoading = false,
                        identityToken = response.identityToken,
                    )
                }

                override fun onError(error: SDKException) {
                    // Classify errors by code (GenericErrorCode / SsoErrorCode), never by message.
                    showStatus(
                        getString(R.string.point_smartapp_demo_app_sso_error, error.code, error.message.orEmpty()),
                        isLoading = false,
                        isError = true,
                    )
                }
            }
        )
    }

    private fun showStatus(
        message: String,
        isLoading: Boolean,
        isError: Boolean = false,
        identityToken: String? = null,
    ) {
        runOnUiThread {
            if (!isFinishing && !isDestroyed) {
                statusMessage = message
                statusIsError = isError
                loading = isLoading
                this.identityToken = identityToken
            }
        }
    }
}
