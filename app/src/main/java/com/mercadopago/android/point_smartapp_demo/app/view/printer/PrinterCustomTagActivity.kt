package com.mercadopago.android.point_smartapp_demo.app.view.printer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoMessage
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoCheckbox
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoField
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoProgress
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoPrimaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSecondaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent
import com.mercadopago.android.point_smartapp_demo.app.view.payment.dialog.SelectionPaymentMethodDialogFragment

class PrinterCustomTagActivity : AppCompatActivity() {

    private var lastPaymentMethodSelected: String? = null
    private var clearPaymentMethodList = true
    private var inputText by mutableStateOf("")
    private var printPdf417 by mutableStateOf(false)
    private var isPrinting by mutableStateOf(false)
    private var showForm by mutableStateOf(true)
    private var paymentMethodActionLabel by mutableStateOf("")
    private var description by mutableStateOf("")
    private var selectedPaymentMethodLabel by mutableStateOf<String?>(null)
    private var resultIsError by mutableStateOf(false)
    private var resultShown by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val printActionLabel = getString(R.string.point_smartapp_demo_app_text_button_printer_custom_tag)
        paymentMethodActionLabel = getString(R.string.point_smartapp_demo_app_lab_get_payment_method_action)
        description = getString(R.string.point_smartapp_demo_app_home_descption_printer_custom_tag)
        inputText = getString(R.string.point_smartapp_demo_app_custom_tag_sample_text)
        val title = getString(R.string.point_smartapp_demo_app_home_printer_custom_tag)
        val textFieldLabel = getString(R.string.point_smartapp_demo_app_to_print_label)
        val checkboxLabel = getString(R.string.point_smartapp_demo_app_home_print_pdf417_in_boleta_custom_tag)

        setDemoContent {
            DemoScreen(title = title) {
                if (showForm) {
                    Text(text = description)
                    DemoField(
                        label = textFieldLabel,
                        value = inputText,
                        onValueChange = { inputText = it },
                        keyboardType = KeyboardType.Text,
                        singleLine = false,
                        textFontSize = 12.sp,
                    )
                    DemoCheckbox(
                        label = checkboxLabel,
                        checked = printPdf417,
                        onCheckedChange = { printPdf417 = it },
                    )
                    selectedPaymentMethodLabel?.let { Text(text = it) }
                    DemoSecondaryButton(
                        text = paymentMethodActionLabel,
                        onClick = ::getPaymentMethodAction,
                    )
                } else {
                    if (isPrinting) DemoProgress(label = printActionLabel)
                    if (resultShown) {
                        DemoMessage(message = description, isError = resultIsError)
                    } else {
                        Text(text = description)
                    }
                }
                if (!resultShown) {
                    DemoPrimaryButton(
                        text = printActionLabel,
                        enabled = !isPrinting,
                        onClick = ::onClickPrintAction,
                    )
                }
            }
        }
    }

    private fun getPaymentMethodAction() {
        clearPaymentMethodList = clearPaymentMethodList.not()
        if (clearPaymentMethodList) {
            paymentMethodActionLabel = getString(R.string.point_smartapp_demo_app_lab_get_payment_method_action)
            lastPaymentMethodSelected = null
            selectedPaymentMethodLabel = null
        } else {
            paymentMethodActionLabel = getString(R.string.point_smartapp_demo_app_clear_label)
            launchPaymentMethodDialog()
        }
    }

    private fun launchPaymentMethodDialog() {
        val dialog = SelectionPaymentMethodDialogFragment.newInstance()
        dialog.onListenerPaymentMethod = { paymentMethod ->
            lastPaymentMethodSelected = paymentMethod
            selectedPaymentMethodLabel = String.format(
                getString(R.string.point_smartapp_demo_app_home_print_payment_method_selected_custom_tag),
                paymentMethod,
            )
        }
        dialog.show(supportFragmentManager, "SelectionPaymentMethodDialogFragment")
    }

    private fun onClickPrintAction() {
        isPrinting = true
        showForm = false
        resultShown = false
        printImageCustomTag()
    }

    private fun printImageCustomTag() {
        MPManager.bitmapPrinter.print(
            inputText,
            lastPaymentMethodSelected,
            printPdf417,
        ) { response ->
            response
                .doIfSuccess { result -> onResultSuccess(result) }
                .doIfError { error -> onResultFailure(error.message.orEmpty()) }
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
