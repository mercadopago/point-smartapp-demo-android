package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.printer

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointMainappDemoAppActivityPrinterTestBinding
import com.mercadopago.android.point_smartapp_demo.app.util.hideKeyboard
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts.PrinterEvents
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.viewmodels.BluetoothPrinterViewModel

class PrinterTestActivity : AppCompatActivity(), PrinterSelectorCallback {

    private val binding: PointMainappDemoAppActivityPrinterTestBinding by lazy {
        PointMainappDemoAppActivityPrinterTestBinding.inflate(
            layoutInflater
        )
    }

    private val viewModel by viewModels<BluetoothPrinterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setObserver()
        onClicksListener()
    }

    private fun onClicksListener() {
        binding.apply {
            buttonLoremImsup.setOnClickListener {
                viewModel.isPasteLargeText = viewModel.isPasteLargeText.not()
                if (viewModel.isPasteLargeText) {
                    buttonLoremImsup.text = getString(R.string.point_smartapp_demo_app_clear_label)
                    textFieldDataToPrint.setText(LARGE_STRING)
                } else {
                    buttonLoremImsup.text = getString(R.string.point_smartapp_demo_app_make_print_label_large_text)
                    textFieldDataToPrint.setText("")
                }
            }

            buttonMakePrint.setOnClickListener {
                viewModel.makePrint(textFieldDataToPrint.text.toString())
                hideKeyboard()
            }
        }
    }

    private fun setObserver() {
        viewModel.printerEventLiveDataLiveData.observe(this) { event ->
            when (event) {
                is PrinterEvents.IsLoading -> binding.progressIndicator.isVisible = event.isVisible
                is PrinterEvents.LaunchPrinterSelector -> PrinterSelectorDialog.newInstance(event.printerList)
                    .show(supportFragmentManager, PrinterSelectorDialog::class.simpleName)

                is PrinterEvents.OutputResult -> makeSnackBar(event.resultMessage)
                is PrinterEvents.DataEmpty -> makeSnackBar(getString(R.string.point_smartapp_demo_app_error_msg_data_empty))
                is PrinterEvents.Error -> event.error.message?.let { message -> makeSnackBar(message) }
            }
        }
    }

    private fun makeSnackBar(message: String) {
        binding.progressIndicator.visibility = View.GONE
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.primaryColor))
            .setTextColor(getColor(R.color.secondaryTextColor))
            .show()
    }

    override fun onListenerOptionPrinter(address: String) {
        viewModel.makePrint(binding.textFieldDataToPrint.text.toString(), address)
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
