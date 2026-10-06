package com.mercadopago.android.point_smartapp_demo.app.view.sso

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.exception.SDKException
import com.mercadolibre.android.point_integration_sdk.nativesdk.sso.provider.IdentityTokenResponseData
import com.mercadolibre.android.point_integration_sdk.nativesdk.sso.provider.SingleSignOnCallback
import com.mercadolibre.android.point_integration_sdk.nativesdk.sso.provider.SingleSignOnStatus
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointSmartappDemoAppActivitySsoBinding
import com.mercadopago.android.point_smartapp_demo.app.util.gone
import com.mercadopago.android.point_smartapp_demo.app.util.visible
import java.util.UUID

class SingleSignOnActivity : AppCompatActivity() {
    private val binding: PointSmartappDemoAppActivitySsoBinding by lazy {
        PointSmartappDemoAppActivitySsoBinding.inflate(layoutInflater)
    }
    private val singleSignOnTools = MPManager.singleSignOnTools

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewCompat.setAccessibilityHeading(binding.ssoTitle, true)
        binding.requestSsoButton.setOnClickListener { requestIdentityToken() }
        binding.backToHomeButton.setOnClickListener { finish() }
    }

    private fun requestIdentityToken() {
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
                    // Delivery only: do not display, log or store the token in this demo.
                    // A real integrator sends it to its backend to validate signature, claims and nonce.
                    showStatus(getString(R.string.point_smartapp_demo_app_sso_success), isLoading = false)
                }

                override fun onError(error: SDKException) {
                    // Classify errors by code (GenericErrorCode / SsoErrorCode), never by message.
                    showStatus(
                        getString(R.string.point_smartapp_demo_app_sso_error, error.code, error.message.orEmpty()),
                        isLoading = false
                    )
                }
            }
        )
    }

    private fun showStatus(message: String, isLoading: Boolean) {
        if (isFinishing || isDestroyed) return
        binding.apply {
            if (isLoading) ssoProgressBar.visible() else ssoProgressBar.gone()
            ssoStatus.visible()
            ssoStatus.text = message
            requestSsoButton.isEnabled = !isLoading
        }
    }
}
