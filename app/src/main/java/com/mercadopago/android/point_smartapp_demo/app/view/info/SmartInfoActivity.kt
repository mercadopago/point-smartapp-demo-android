package com.mercadopago.android.point_smartapp_demo.app.view.info

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoMessage
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoInfoRow
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedback
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent

class SmartInfoActivity : AppCompatActivity() {

    private var smartInfo by mutableStateOf<List<Pair<String, String>>>(emptyList())
    private var feedback by mutableStateOf<DemoFeedback?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = getString(R.string.point_smartapp_demo_app_smart_info_title)
        setDemoContent {
            DemoScreen(title = title) {
                smartInfo.forEach { (label, value) -> DemoInfoRow(label = label, value = value) }
                feedback?.let { result ->
                    DemoMessage(message = result.message, isError = result.isError)
                }
            }
        }
        configSmartInfo()
    }

    private fun configSmartInfo() {
        MPManager.smartInformationTools.getInformation { response ->
            response
                .doIfSuccess { smartInformation ->
                    val device = smartInformation.smartDevice
                    val integration = smartInformation.integration
                    smartInfo = listOf(
                        getString(R.string.point_smartapp_demo_app_serial_number_text)
                            .substringBefore(":") to device.serialNumber,
                        getString(R.string.point_smartapp_demo_app_brand_name_text)
                            .substringBefore(":") to device.brandName,
                        getString(R.string.point_smartapp_demo_app_model_name_text)
                            .substringBefore(":") to device.modelName,
                        getString(R.string.point_smartapp_demo_app_payment_module_version_text)
                            .substringBefore(":") to device.paymentModuleVersion,
                        getString(R.string.point_smartapp_demo_app_sdk_version_text)
                            .substringBefore(":") to integration.nativeSdkVersion,
                    )
                }
                .doIfError {
                    feedback = DemoFeedback(
                        getString(R.string.point_smartapp_demo_app_smart_info_load_error),
                        isError = true,
                    )
                }
        }
    }
}
