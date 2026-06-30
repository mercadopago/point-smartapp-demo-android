package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.printer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mercadopago.android.point_integration_sdk.nativesdk.bluetoothclient.provider.entities.BluetoothDeviceModel
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointMainappDemoAppDialogPrinterSelectorBinding
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.adapter.BluetoothDevicesAdapter

class PrinterSelectorDialog : BottomSheetDialogFragment() {

    private lateinit var binding: PointMainappDemoAppDialogPrinterSelectorBinding

    private var listener: PrinterSelectorCallback? = null

    private var printerList: List<BluetoothDeviceModel>? = null

    private val adapterPrinterOptions by lazy {
        BluetoothDevicesAdapter { printerSelect ->
            listener?.onListenerOptionPrinter(printerSelect)
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        try {
            listener = context as PrinterSelectorCallback
        } catch (e: ClassCastException) {
            throw IllegalStateException("Should implement PrinterSelectorCallback in dialog host")
        }
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PointMainappDemoAppDialogPrinterSelectorBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvPrinterOptions.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterPrinterOptions
        }
        adapterPrinterOptions.submitList(printerList)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        printerList = null
        super.onDestroy()
    }

    companion object {
        internal fun newInstance(printerList: List<BluetoothDeviceModel>) = PrinterSelectorDialog().apply {
            this.printerList = printerList
        }
    }
}

internal interface PrinterSelectorCallback {
    fun onListenerOptionPrinter(address: String)
}
