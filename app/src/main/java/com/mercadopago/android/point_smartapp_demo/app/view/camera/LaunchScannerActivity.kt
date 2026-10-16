package com.mercadopago.android.point_smartapp_demo.app.view.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mercadolibre.android.point_integration_sdk.nativesdk.camera.provider.entities.CameraScannerStatus
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.camera.domain.ScanType
import com.mercadolibre.android.point_integration_sdk.nativesdk.camera.provider.data.ScannerFlowRequestData
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoDropdown
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedback
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedbackBanner
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoPrimaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSwitch
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent

class LaunchScannerActivity : AppCompatActivity() {

    private var scannerRequestBuilder = ScannerFlowRequestData(
        initialOrientation = ORIENTATION_PORTRAIT,
        isLanternOn = false,
    )
    private var flashlightOn by mutableStateOf(false)
    private var selectedOrientation by mutableStateOf(ORIENTATION_NONE)
    private var feedback by mutableStateOf<DemoFeedback?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = getString(R.string.point_smartapp_demo_app_cammera_scanner_main_title)
        val orientations = resources.getStringArray(
            R.array.point_smartapp_demo_app_camera_scanner_orientation,
        ).toList()
        val flashlightLabel = "Do you want to turn on the flashlight?"
        val orientationLabel = "Select the screen orientation you want"
        val qrLabel = getString(R.string.point_smartapp_demo_app_cammera_scanner_new_start_qr_callback_title)
        val barcodeLabel = getString(R.string.point_smartapp_demo_app_cammera_scanner_new_start_barcode_callback_title)

        setDemoContent {
            DemoScreen(title = title) {
                DemoSwitch(
                    label = flashlightLabel,
                    selected = flashlightOn,
                    onSelectedChange = { isOn ->
                        flashlightOn = isOn
                        scannerRequestBuilder = scannerRequestBuilder.copy(isLanternOn = isOn)
                    },
                )
                DemoDropdown(
                    label = orientationLabel,
                    selected = selectedOrientation,
                    options = orientations,
                    onSelected = { orientation ->
                        selectedOrientation = orientation
                        val position = orientations.indexOf(orientation)
                        val selectedValue = if (position == 1) {
                            ORIENTATION_LANDSCAPE
                        } else {
                            ORIENTATION_PORTRAIT
                        }
                        scannerRequestBuilder = scannerRequestBuilder.copy(
                            initialOrientation = selectedValue,
                        )
                    },
                )
                DemoPrimaryButton(
                    text = qrLabel,
                    onClick = { launchScanner(ScanType.CAMERA_SCANNER_QR) },
                )
                DemoPrimaryButton(
                    text = barcodeLabel,
                    onClick = { launchScanner(ScanType.CAMERA_SCANNER_BARCODE) },
                )
                DemoFeedbackBanner(feedback) { feedback = null }
            }
        }
    }

    private fun launchScanner(scanType: ScanType) {
        try {
            MPManager.cameraScanner.launchScanner(scanType, scannerRequestBuilder) { response ->
                response
                    .doIfSuccess { result ->
                        val isError = result.status != CameraScannerStatus.Ok
                        val message = result.message
                            ?.takeIf { it.isNotBlank() }
                            ?: getString(R.string.point_smartapp_demo_app_scanner_result_error)
                        showFeedback(message, isError)
                    }
                    .doIfError { error -> showFeedback(error.message.toString(), isError = true) }
            }
        } catch (e: Exception) {
            showFeedback("Error initial scanner: ${e.message}", isError = true)
        }
    }

    private fun showFeedback(message: String, isError: Boolean = false) {
        feedback = DemoFeedback(message, isError)
    }

    companion object {
        private const val ORIENTATION_NONE = "None"
        private const val ORIENTATION_PORTRAIT = 1
        private const val ORIENTATION_LANDSCAPE = 0
    }
}
