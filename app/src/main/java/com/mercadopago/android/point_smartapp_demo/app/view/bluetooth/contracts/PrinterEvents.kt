package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts

import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.entities.BluetoothDeviceModel

sealed class PrinterEvents {
    class IsLoading(val isVisible: Boolean) : PrinterEvents()
    class OutputResult(val resultMessage: String) : PrinterEvents()
    class LaunchPrinterSelector(val printerList: List<BluetoothDeviceModel>) : PrinterEvents()
    object DataEmpty : PrinterEvents()

    class Error(val error: Exception) : PrinterEvents()
}
