package com.mercadopago.android.point_smartapp_demo.app.view.info

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointSmartappDemoAppActivitySmartInfoBinding
import com.mercadopago.android.point_smartapp_demo.app.util.toast

class SmartInfoActivity : AppCompatActivity() {

    private var binding: PointSmartappDemoAppActivitySmartInfoBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = PointSmartappDemoAppActivitySmartInfoBinding.inflate(layoutInflater)
        binding?.run {
            setContentView(root)
            configSmartInfo()
        }
    }

    private fun configSmartInfo() {
        MPManager.smartInformationTools.getInformation { response ->
            response
                .doIfSuccess { smartInformation ->
                    with(smartInformation.smartDevice) {
                        val serialNumber = getString(R.string.point_smartapp_demo_app_serial_number_text).format(serialNumber)
                        val brandName = getString(R.string.point_smartapp_demo_app_brand_name_text).format(brandName)
                        val modelName = getString(R.string.point_smartapp_demo_app_model_name_text).format(modelName)
                        val paymentModuleVersion = getString(R.string.point_smartapp_demo_app_payment_module_version_text)
                            .format(paymentModuleVersion)

                        binding?.run {
                            smartappDemoAppSerialNumberText.text = serialNumber
                            smartappDemoAppBrandNameText.text = brandName
                            smartappDemoAppModelNameText.text = modelName
                            smartappDemoAppPaymentModuleVersionText.text = paymentModuleVersion
                        }
                    }

                    with(smartInformation.integration) {
                        val sdkVersion = getString(R.string.point_smartapp_demo_app_sdk_version_text).format(nativeSdkVersion)
                        binding?.smartappDemoAppSdkVersionText?.text = sdkVersion

                    }
                }
                .doIfError { error ->
                    toast(error.message.orEmpty())
                }
        }
    }
}
