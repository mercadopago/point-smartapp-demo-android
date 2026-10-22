package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.printer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.entities.BluetoothDeviceModel
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoTheme
import androidx.compose.ui.res.painterResource

class PrinterSelectorDialog : BottomSheetDialogFragment() {

    private var listener: PrinterSelectorCallback? = null
    private var printerList: List<BluetoothDeviceModel>? = null

    override fun onAttach(context: Context) {
        try {
            listener = context as PrinterSelectorCallback
        } catch (e: ClassCastException) {
            throw IllegalStateException("Should implement PrinterSelectorCallback in dialog host")
        }
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val connectedLabel = getString(R.string.point_smartapp_demo_app_bluetooth_devices_connect)
        val dialogTitle = getString(R.string.point_smartapp_demo_app_printer_selector_title)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DemoTheme {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .verticalScroll(rememberScrollState())
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp,
                            )
                            .semantics { paneTitle = dialogTitle },
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        printerList.orEmpty().forEach { printer ->
                            val bonded = printer.boundState == android.bluetooth.BluetoothDevice.BOND_BONDED
                            val iconRes = if (bonded && printer.isConnected) {
                                R.drawable.point_smartapp_demo_app_ic_bluetooth_connected
                            } else {
                                R.drawable.point_smartapp_demo_app_ic_bluetooth_available
                            }
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    listener?.onListenerOptionPrinter(printer.address)
                                    dismiss()
                                },
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Icon(
                                        painter = painterResource(iconRes),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp),
                                    )
                                    Column {
                                        Text(text = printer.name.ifEmpty { printer.address })
                                        if (bonded && printer.isConnected) {
                                            Text(
                                                text = connectedLabel,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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
