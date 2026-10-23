package com.mercadopago.android.point_smartapp_demo.app.snapshot.bluetooth

import android.bluetooth.BluetoothDevice
import app.cash.paparazzi.Paparazzi
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.entities.BluetoothDeviceModel
import com.mercadopago.android.point_smartapp_demo.app.snapshot.SnapshotConfiguration
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.BluetoothSettingsScreen
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts.BluetoothDeviceAction
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts.BluetoothSettingsState
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoTheme
import org.junit.Rule
import org.junit.Test

class BluetoothSettingsScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = SnapshotConfiguration.N950,
        theme = SnapshotConfiguration.THEME,
        showSystemUi = false,
    )

    @Test
    fun disabled() = snapshot(BluetoothSettingsState())

    @Test
    fun devices() = snapshot(devicesState())

    @Test
    fun pairing() = snapshot(devicesState().beginOperation(AVAILABLE_ADDRESS, BluetoothDeviceAction.PAIR))

    @Test
    fun error() = snapshot(devicesState().failed())

    private fun snapshot(state: BluetoothSettingsState) {
        paparazzi.snapshot {
            DemoTheme {
                BluetoothSettingsScreen(
                    state = state,
                    onPowerChange = {},
                    onSearch = {},
                    onPair = {},
                    onForget = {},
                    onConfirmForget = {},
                    onCancelForget = {},
                    onDismissMessage = {},
                )
            }
        }
    }

    private fun devicesState() = BluetoothSettingsState(enabled = true, discovering = true, hasSearched = true).discovered(
        BluetoothDeviceModel(
            id = AVAILABLE_ADDRESS,
            address = AVAILABLE_ADDRESS,
            name = KEYBOARD_NAME,
            boundState = BluetoothDevice.BOND_NONE,
            isConnected = false,
        ),
    ).pairedDevicesChanged(listOf(
        BluetoothDeviceModel(
            id = PAIRED_ADDRESS,
            address = PAIRED_ADDRESS,
            name = PRINTER_NAME,
            boundState = BluetoothDevice.BOND_BONDED,
            isConnected = true,
        ),
    ))

    private companion object {
        const val AVAILABLE_ADDRESS = "00:00:00:00:00:01"
        const val PAIRED_ADDRESS = "00:00:00:00:00:02"
        const val KEYBOARD_NAME = "Demo keyboard"
        const val PRINTER_NAME = "Demo printer"
    }
}
