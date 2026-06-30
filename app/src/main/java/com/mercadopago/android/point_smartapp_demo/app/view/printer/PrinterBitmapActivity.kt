package com.mercadopago.android.point_smartapp_demo.app.view.printer

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointSmartappDemoAppActivityPrinterBitmapBinding
import com.mercadopago.android.point_smartapp_demo.app.util.gone
import com.mercadopago.android.point_smartapp_demo.app.util.toast
import com.mercadopago.android.point_smartapp_demo.app.util.visible

class PrinterBitmapActivity : AppCompatActivity() {

    private var binding: PointSmartappDemoAppActivityPrinterBitmapBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PointSmartappDemoAppActivityPrinterBitmapBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        onPrintImageBitmap()
    }

    private fun onPrintImageBitmap() {
        binding?.apply {

            printImageBitmap.setOnClickListener {

                onActionButton()
            }
        }
    }

    private fun onActionButton() {
        binding?.apply {
            if (printImageBitmap.text.toString() == getString(R.string.point_smartapp_demo_app_text_button_printer_bitmap)) {
                progressCircular.visible()
                printImageBitmap()
            } else {
                finish()
            }
        }
    }

    private fun printImageBitmap() {
        val inputStream = resources.openRawResource(R.raw.point_smartapp_demo_app_ic_datafono)
        BitmapFactory.decodeStream(inputStream)?.run {
            MPManager.bitmapPrinter.print(this) { response ->
                response
                    .doIfSuccess { result ->
                        onResultSuccess(result)
                    }
                    .doIfError { error ->
                        onResultFailure(error.message.orEmpty())
                        toast(error.message.orEmpty())
                    }
            }
        }
    }

    private fun onResultSuccess(result: String) {

        binding?.apply {
            progressCircular.gone()
            iconDescription.setImageResource(R.drawable.point_smartapp_demo_app_ic_done)
            descriptionPrinterBitmap.text = getString(R.string.point_smartapp_demo_app_home_result_successful_description_printer_bitmap, result)
            printImageBitmap.text = TEXT_BUTTON_ACTION
        }
    }

    private fun onResultFailure(message: String) {

        binding?.apply {
            progressCircular.gone()
            iconDescription.setImageResource(R.drawable.point_smartapp_demo_app_ic_error)
            descriptionPrinterBitmap.text =
                getString(R.string.point_smartapp_demo_app_home_result_failure_description_printer_bitmap, message)
            printImageBitmap.text = TEXT_BUTTON_ACTION
        }
    }

    companion object {
        private const val TEXT_BUTTON_ACTION = "Go to start"
    }
}
