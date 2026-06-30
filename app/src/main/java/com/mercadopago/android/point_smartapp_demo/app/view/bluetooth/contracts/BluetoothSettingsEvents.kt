package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts

import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.contracts.states.BluetoothBondState
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.entities.BluetoothDeviceModel

internal sealed class BluetoothSettingsEvents {
    object Init : BluetoothSettingsEvents()

    class IgnitorCurrentState(val state: Boolean) : BluetoothSettingsEvents()
    class IgnitorLaunchResult(val result: Boolean) : BluetoothSettingsEvents()

    class DiscoveryPairDevicesResult(val devicesList: List<BluetoothDeviceModel>) : BluetoothSettingsEvents()

    object DiscoveryStarted : BluetoothSettingsEvents()
    object DiscoveryEnded : BluetoothSettingsEvents()
    class DiscoveryDevicesFound(val devices: BluetoothDeviceModel) : BluetoothSettingsEvents()
    class DiscoveryDevicesUpdate(val devices: BluetoothDeviceModel) : BluetoothSettingsEvents()
    class PairingDevicesStatus(val pair: Pair<BluetoothBondState, BluetoothDeviceModel>) : BluetoothSettingsEvents()
    class ConnectDevicesResult(val connectDevices: BluetoothDeviceModel) : BluetoothSettingsEvents()

    class Error(val error: Exception) : BluetoothSettingsEvents()
}
