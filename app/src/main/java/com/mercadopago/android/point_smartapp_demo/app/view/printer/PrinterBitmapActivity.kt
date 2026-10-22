package com.mercadopago.android.point_smartapp_demo.app.view.printer

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoMessage
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoProgress
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoPrimaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent

class PrinterBitmapActivity : AppCompatActivity() {

    private var isPrinting by mutableStateOf(false)
    private var description by mutableStateOf("")
    private var resultIsError by mutableStateOf(false)
    private var resultShown by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = getString(R.string.point_smartapp_demo_app_home_printer_bitmap)
        val initialButtonLabel = getString(R.string.point_smartapp_demo_app_text_button_printer_bitmap)
        val progressLabel = initialButtonLabel
        description = getString(R.string.point_smartapp_demo_app_home_descption_printer_bitmap)

        setDemoContent {
            DemoScreen(title = title) {
                if (isPrinting) DemoProgress(label = progressLabel)
                if (resultShown) {
                    DemoMessage(message = description, isError = resultIsError)
                } else {
                    Text(text = description)
                }
                if (!resultShown) {
                    DemoPrimaryButton(
                        text = initialButtonLabel,
                        enabled = !isPrinting,
                        onClick = {
                            isPrinting = true
                            printImageBitmap()
                        },
                    )
                }
            }
        }
    }

    private fun printImageBitmap() {
        val inputStream = resources.openRawResource(R.raw.point_smartapp_demo_app_ic_datafono)
        BitmapFactory.decodeStream(inputStream)?.run {
            MPManager.bitmapPrinter.print(this) { response ->
                response
                    .doIfSuccess { result -> onResultSuccess(result) }
                    .doIfError { error ->
                        onResultFailure(error.message.orEmpty())
                    }
            }
        }
    }

    private fun onResultSuccess(result: String) {
        isPrinting = false
        resultIsError = false
        resultShown = true
        description = getString(
            R.string.point_smartapp_demo_app_home_result_successful_description_printer_bitmap,
            result,
        )
    }

    private fun onResultFailure(message: String) {
        isPrinting = false
        resultIsError = true
        resultShown = true
        description = getString(
            R.string.point_smartapp_demo_app_home_result_failure_description_printer_bitmap,
            message,
        )
    }
}
