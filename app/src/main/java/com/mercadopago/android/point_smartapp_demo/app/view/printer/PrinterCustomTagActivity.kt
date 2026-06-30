package com.mercadopago.android.point_smartapp_demo.app.view.printer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointSmartappDemoAppActivityPrinterCustomTagBinding
import com.mercadopago.android.point_smartapp_demo.app.util.gone
import com.mercadopago.android.point_smartapp_demo.app.util.hideKeyboard
import com.mercadopago.android.point_smartapp_demo.app.util.toast
import com.mercadopago.android.point_smartapp_demo.app.util.visible
import com.mercadopago.android.point_smartapp_demo.app.view.payment.adapter.PaymentMethodAdapter
import com.mercadopago.android.point_smartapp_demo.app.view.payment.dialog.SelectionPaymentMethodDialogFragment
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.PaymentMethodModel

class PrinterCustomTagActivity : AppCompatActivity() {
    private var lastPaymentMethodSelected: String? = null
    private var clearPaymentMethodList: Boolean = true

    val binding: PointSmartappDemoAppActivityPrinterCustomTagBinding by lazy {
        PointSmartappDemoAppActivityPrinterCustomTagBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        onPrintCustomTag()
        onGetPaymentMethod()
        setupPaymentMethodSelectedTextView()
        hideKeyboard()
    }

    private fun onPrintCustomTag() {
        binding.apply {
            printImageCustomTag.setOnClickListener {
                onClickPrintAction()
            }
        }
    }

    private fun onGetPaymentMethod() {
        binding.apply {
            getPaymentMethodCustomTag.setOnClickListener {
                onClickGetPaymentMethodAction()
            }
        }
    }

    private fun onClickPrintAction() {
        binding.apply {
            if (printImageCustomTag.text.toString() == getString(R.string.point_smartapp_demo_app_text_button_printer_custom_tag)) {
                progressCircular.visible()
                groupPrintImageCustomTag.gone()
                printImageCustomTag()
            } else {
                onRestartPrintAction()
            }
        }
    }

    private fun setupPaymentMethodSelectedTextView() {
        binding.textViewPaymentMethodSelected.apply {
            lastPaymentMethodSelected?.let { lastPaymentMethodSelected ->
                visible()
                text = String.format(
                    getString(R.string.point_smartapp_demo_app_home_print_payment_method_selected_custom_tag),
                    lastPaymentMethodSelected
                )
            } ?: gone()
        }
    }

    private fun PointSmartappDemoAppActivityPrinterCustomTagBinding.onClickGetPaymentMethodAction() {
        getPaymentMethodAction()
    }

    private fun PointSmartappDemoAppActivityPrinterCustomTagBinding.getPaymentMethodAction() {
        clearPaymentMethodList = clearPaymentMethodList.not()
        if (clearPaymentMethodList) {
            clearPaymentMethodAction()
        } else {
            getPaymentMethodCustomTag.text = getString(R.string.point_smartapp_demo_app_clear_label)
            launchPaymentMethodDialog()
        }
    }

    private fun PointSmartappDemoAppActivityPrinterCustomTagBinding.clearPaymentMethodAction() {
        getPaymentMethodCustomTag.text =
            getString(R.string.point_smartapp_demo_app_lab_get_payment_method_action)
        lastPaymentMethodSelected = null
        setupPaymentMethodSelectedTextView()
    }

    private fun launchPaymentMethodDialog() {
        val dialog = SelectionPaymentMethodDialogFragment.newInstance()
        dialog.onListenerPaymentMethod = { paymentMethod ->
            lastPaymentMethodSelected = paymentMethod
            setupPaymentMethodSelectedTextView()
        }
        dialog.show(supportFragmentManager, "SelectionPaymentMethodDialogFragment")
    }


    private fun printImageCustomTag() {
        val content = binding.inputText.text.toString()
        val printPdf417InBoleta = binding.checkboxPrintPdf417Boleta.isChecked
        MPManager.bitmapPrinter.print(
            content,
            lastPaymentMethodSelected,
            printPdf417InBoleta,
        ) { response ->
            response
                .doIfSuccess { result ->
                    onResultSuccess(result)
                }
                .doIfError { error ->
                    onResultFailure(error.message.orEmpty())
                }
        }
    }

    private fun onRestartPrintAction() {
        binding.apply {
            inputText.text?.clear()
            iconDescription.setImageResource(R.drawable.point_smartapp_demo_app_black_ic_print)
            descriptionPrinterBitmap.text =
                getString(R.string.point_smartapp_demo_app_home_descption_printer_custom_tag)
            progressCircular.gone()
            groupPrintImageCustomTag.visible()
            printImageCustomTag.text =
                getString(R.string.point_smartapp_demo_app_text_button_printer_custom_tag)
        }
    }

    private fun onResultSuccess(result: String) {
        binding.apply {
            progressCircular.gone()
            iconDescription.setImageResource(R.drawable.point_smartapp_demo_app_ic_done)
            descriptionPrinterBitmap.text = getString(
                R.string.point_smartapp_demo_app_home_result_successful_description_printer_bitmap,
                result
            )
            printImageCustomTag.text = TEXT_BUTTON_ACTION
        }
    }

    private fun onResultFailure(message: String) {

        binding.apply {
            progressCircular.gone()
            iconDescription.setImageResource(R.drawable.point_smartapp_demo_app_ic_error)
            descriptionPrinterBitmap.text =
                getString(
                    R.string.point_smartapp_demo_app_home_result_failure_description_printer_bitmap,
                    message
                )
            printImageCustomTag.text = TEXT_BUTTON_ACTION
        }
    }

    companion object {
        private const val TEXT_BUTTON_ACTION = "Replay"
    }
}
