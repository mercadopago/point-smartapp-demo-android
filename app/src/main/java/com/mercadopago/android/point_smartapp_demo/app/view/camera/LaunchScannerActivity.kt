package com.mercadopago.android.point_smartapp_demo.app.view.camera

import android.os.Bundle
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.mercadopago.android.point_integration_sdk.nativesdk.MPManager
import com.mercadopago.android.point_integration_sdk.nativesdk.camera.domain.ScanType
import com.mercadopago.android.point_integration_sdk.nativesdk.camera.provider.data.ScannerFlowRequestData
import com.mercadopago.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadopago.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointMainappDemoAppActivityLaunchScannerBinding

class LaunchScannerActivity : AppCompatActivity() {

    private val binding: PointMainappDemoAppActivityLaunchScannerBinding by lazy {
        PointMainappDemoAppActivityLaunchScannerBinding.inflate(layoutInflater)
    }

    private var scannerRequestBuilder = ScannerFlowRequestData(
        initialOrientation = ORIENTATION_PORTRAIT,
        isLanternOn = false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        setupScanButtons()
        setupLanternSwitch()
        setupOrientationSpinner()
        initializeViewsWithCurrentState()
    }

    private fun initializeViewsWithCurrentState() {
        binding.apply {
            switchTurnOn.isChecked = false
            valueInitialOrientation.setText(ORIENTATION_NONE)
            valueInitialOrientation.setSimpleItems(
                resources.getStringArray(R.array.point_smartapp_demo_app_camera_scanner_orientation)
            )
        }
    }

    private fun setupScanButtons() {
        binding.apply {
            pointMainappDemoAppCameraQrScannerNewInitBtn.setOnClickListener {
                launchScanner(ScanType.CAMERA_SCANNER_QR)
            }
            pointMainappDemoAppNewCameraBarcodeScannerInitBtn.setOnClickListener {
                launchScanner(ScanType.CAMERA_SCANNER_BARCODE)
            }
        }
    }

    private fun setupLanternSwitch() {
        binding.switchTurnOn.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    thumbTintList = getColorStateList(R.color.primaryColor)
                    trackTintList = getColorStateList(R.color.primaryColor)
                } else {
                    thumbTintList = getColorStateList(R.color.grayDark)
                    trackTintList = getColorStateList(R.color.grayDark)
                }
                scannerRequestBuilder = scannerRequestBuilder.copy(
                    isLanternOn = isChecked
                )
            }
        }
    }

    private fun setupOrientationSpinner() {
        binding.valueInitialOrientation.apply {
            onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->
                val selectedOrientation = when (position) {
                    1 -> ORIENTATION_LANDSCAPE
                    else -> ORIENTATION_PORTRAIT
                }
                scannerRequestBuilder = scannerRequestBuilder.copy(
                    initialOrientation = selectedOrientation
                )
            }
        }
    }


    private fun launchScanner(scanType: ScanType) {
        try {
            val requestData = scannerRequestBuilder
            MPManager.cameraScanner.launchScanner(scanType, requestData) { response ->
                response
                    .doIfSuccess { result -> showFeedback(result.message.toString()) }
                    .doIfError { error -> showFeedback(error.message.toString(), isError = true) }
            }
        } catch (e: Exception) {
            showFeedback("Error initial scanner: ${e.message}", isError = true)
        }
    }

    private fun showFeedback(message: String, isError: Boolean = false) {
        val backgroundColor = if (isError) {
            getColor(R.color.design_default_color_error)
        } else {
            getColor(R.color.doneColor)
        }

        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(backgroundColor)
            .show()
    }

    companion object {
        private const val ORIENTATION_NONE = "None"
        private const val ORIENTATION_PORTRAIT = 1
        private const val ORIENTATION_LANDSCAPE = 0
    }
}
