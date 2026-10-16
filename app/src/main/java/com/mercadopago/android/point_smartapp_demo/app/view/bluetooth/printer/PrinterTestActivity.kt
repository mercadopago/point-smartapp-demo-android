package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.printer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.contracts.states.BluetoothPrinterResult
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.util.hideKeyboard
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedback
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedbackBanner
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoCheckbox
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoField
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoPrimaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoProgress
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts.PrinterEvents
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.viewmodels.BluetoothPrinterViewModel

class PrinterTestActivity : AppCompatActivity(), PrinterSelectorCallback {

    private val viewModel by viewModels<BluetoothPrinterViewModel>()
    private var dataToPrint by mutableStateOf("")
    private var isLoading by mutableStateOf(false)
    private var feedback by mutableStateOf<DemoFeedback?>(null)
    private var isLongSampleEnabled by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = getString(R.string.point_smartapp_demo_app_home_print_label)
        val longSampleLabel = getString(R.string.point_smartapp_demo_app_printer_use_long_sample)
        val inputLabel = getString(R.string.point_smartapp_demo_app_to_print_label)
        val printLabel = getString(R.string.point_smartapp_demo_app_make_print_label)
        val progressLabel = printLabel

        setDemoContent {
            DemoScreen(title = title) {
                if (isLoading) DemoProgress(label = progressLabel)
                DemoField(
                    label = inputLabel,
                    value = dataToPrint,
                    onValueChange = {
                        dataToPrint = it
                        isLongSampleEnabled = false
                    },
                    singleLine = false,
                    textFontSize = 12.sp,
                )
                DemoCheckbox(
                    label = longSampleLabel,
                    checked = isLongSampleEnabled,
                    onCheckedChange = { enabled ->
                        isLongSampleEnabled = enabled
                        dataToPrint = if (enabled) LARGE_STRING else ""
                    },
                )
                DemoPrimaryButton(
                    text = printLabel,
                    onClick = {
                        viewModel.makePrint(dataToPrint)
                        hideKeyboard()
                    },
                )
                DemoFeedbackBanner(feedback) { feedback = null }
            }
        }
        setObserver()
    }

    private fun setObserver() {
        viewModel.printerEventLiveDataLiveData.observe(this) { event ->
            when (event) {
                is PrinterEvents.IsLoading -> isLoading = event.isVisible
                is PrinterEvents.LaunchPrinterSelector -> PrinterSelectorDialog.newInstance(event.printerList)
                    .show(supportFragmentManager, PrinterSelectorDialog::class.simpleName)
                is PrinterEvents.OutputResult -> showPrinterResult(event.result)
                is PrinterEvents.DataEmpty -> showFeedback(
                    getString(R.string.point_smartapp_demo_app_error_msg_data_empty),
                    isError = true,
                )
                is PrinterEvents.Error -> event.error.message?.let { message -> showFeedback(message, isError = true) }
            }
        }
    }

    private fun showFeedback(message: String, isError: Boolean = false) {
        isLoading = false
        feedback = DemoFeedback(message, isError)
    }

    private fun showPrinterResult(result: BluetoothPrinterResult) {
        when (result) {
            BluetoothPrinterResult.SUCCESS -> showFeedback(
                getString(R.string.point_smartapp_demo_app_printer_result_success),
            )
            BluetoothPrinterResult.ERROR_PRINTER_NOT_FOUND -> showFeedback(
                getString(R.string.point_smartapp_demo_app_printer_result_not_found),
                isError = true,
            )
            BluetoothPrinterResult.CONNECTION_FAILED -> showFeedback(
                getString(R.string.point_smartapp_demo_app_printer_result_connection_failed),
                isError = true,
            )
            BluetoothPrinterResult.ERROR_DATA_TO_PRINT_NULL -> showFeedback(
                getString(R.string.point_smartapp_demo_app_error_msg_data_empty),
                isError = true,
            )
            BluetoothPrinterResult.ERROR_UNDEFINED -> showFeedback(
                getString(R.string.point_smartapp_demo_app_printer_result_unknown_error),
                isError = true,
            )
            BluetoothPrinterResult.NEED_SELECTION_DEVICE -> showFeedback(
                getString(R.string.point_smartapp_demo_app_printer_result_select_device),
                isError = true,
            )
        }
    }

    override fun onListenerOptionPrinter(address: String) {
        viewModel.makePrint(dataToPrint, address)
    }

    companion object {
        const val LARGE_STRING = "Lorem ipsum dolor sit amet consectetur adipiscing elit ac, vel posuere lobortis nunc praesent" +
                " penatibus consequat, nam per cursus ultrices sem luctus sollicitudin. Auctor non sagittis lectus ultricies cubilia" +
                " praesent, bibendum convallis torquent condimentum nullam consequat sem, augue ullamcorper eu purus habitasse. " +
                "Vestibulum pretium cras leo magna duis nullam porta dui urna, velit laoreet ridiculus nostra risus venenatis sapien" +
                " potenti, senectus donec mi varius felis erat curabitur penatibus. Placerat natoque aenean velit nullam risus dis ac" +
                " platea praesent pulvinar, ligula molestie pellentesque parturient leo eros nisi varius."
    }
}
