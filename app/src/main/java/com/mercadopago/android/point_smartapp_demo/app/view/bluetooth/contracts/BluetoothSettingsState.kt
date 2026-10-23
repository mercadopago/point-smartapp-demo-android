package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts

import android.bluetooth.BluetoothDevice
import androidx.annotation.StringRes
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.contracts.states.BluetoothBondState
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.entities.BluetoothDeviceModel
import com.mercadopago.android.point_smartapp_demo.app.R

internal enum class BluetoothDeviceAction { PAIR, FORGET }

internal data class BluetoothDeviceOperation(val address: String, val action: BluetoothDeviceAction)

internal data class BluetoothSettingsState(
    val enabled: Boolean = false,
    val changingPower: Boolean = false,
    val discovering: Boolean = false,
    val hasSearched: Boolean = false,
    val devices: Map<String, BluetoothDeviceModel> = emptyMap(),
    val pendingOperation: BluetoothDeviceOperation? = null,
    val forgetConfirmationAddress: String? = null,
    @StringRes val message: Int? = null,
    val isError: Boolean = false,
) {
    val busy: Boolean get() = changingPower || pendingOperation != null
    val pairedDevices get() = devices.values.filter { it.boundState == BluetoothDevice.BOND_BONDED }.sortedBy { it.name }
    val availableDevices get() = devices.values.filter { it.boundState != BluetoothDevice.BOND_BONDED }.sortedBy { it.name }

    fun powerChanged(enabled: Boolean): BluetoothSettingsState =
        if (enabled) copy(enabled = true, changingPower = false) else BluetoothSettingsState()

    fun discovered(device: BluetoothDeviceModel): BluetoothSettingsState {
        if (!enabled || device.address.isBlank()) return this
        if (devices[device.address]?.boundState == BluetoothDevice.BOND_BONDED) return this
        return copy(devices = devices + (device.address to device))
    }

    fun pairedDevicesChanged(paired: List<BluetoothDeviceModel>): BluetoothSettingsState {
        if (!enabled) return this
        val pairedByAddress = paired.filter { it.address.isNotBlank() && it.boundState == BluetoothDevice.BOND_BONDED }.associateBy { it.address }
        val updated = copy(
            devices = devices.filterValues { it.boundState != BluetoothDevice.BOND_BONDED } + pairedByAddress,
            forgetConfirmationAddress = forgetConfirmationAddress?.takeIf { it in pairedByAddress },
        )
        val operation = pendingOperation ?: return updated
        val pairedNow = operation.address in pairedByAddress
        return when {
            operation.action == BluetoothDeviceAction.PAIR && pairedNow -> updated.operationFinished(true)
            operation.action == BluetoothDeviceAction.FORGET && !pairedNow ->
                updated.copy(devices = updated.devices - operation.address).operationFinished(true)
            else -> updated
        }
    }

    fun connectionChanged(device: BluetoothDeviceModel, connected: Boolean): BluetoothSettingsState {
        if (!enabled || device.address.isBlank()) return this
        val current = devices[device.address] ?: device
        return copy(devices = devices + (device.address to current.copy(isConnected = connected)))
    }

    fun beginOperation(address: String, action: BluetoothDeviceAction): BluetoothSettingsState {
        if (!enabled || busy) return this
        val device = devices[address] ?: return this
        if ((device.boundState == BluetoothDevice.BOND_BONDED) != (action == BluetoothDeviceAction.FORGET)) return this
        return copy(
            discovering = false,
            pendingOperation = BluetoothDeviceOperation(address, action),
            forgetConfirmationAddress = null,
            message = null,
            isError = false,
        )
    }

    fun bondChanged(bond: BluetoothBondState, device: BluetoothDeviceModel): BluetoothSettingsState {
        if (!enabled || device.address.isBlank()) return this
        val boundState = when (bond) {
            BluetoothBondState.NONE -> BluetoothDevice.BOND_NONE
            BluetoothBondState.BONDING -> BluetoothDevice.BOND_BONDING
            BluetoothBondState.BONDED -> BluetoothDevice.BOND_BONDED
        }
        val updated = copy(devices = if (bond == BluetoothBondState.NONE) {
            devices - device.address
        } else {
            devices + (device.address to device.copy(boundState = boundState))
        })
        val operation = pendingOperation?.takeIf { it.address == device.address } ?: return updated
        return when {
            bond == BluetoothBondState.NONE -> updated.operationFinished(operation.action == BluetoothDeviceAction.FORGET)
            bond == BluetoothBondState.BONDED && operation.action == BluetoothDeviceAction.PAIR -> updated.operationFinished(true)
            else -> updated
        }
    }

    private fun operationFinished(success: Boolean): BluetoothSettingsState {
        val operation = pendingOperation ?: return this
        return copy(
            pendingOperation = null,
            message = if (!success) R.string.point_smartapp_demo_app_bluetooth_pairing_failed else when (operation.action) {
                BluetoothDeviceAction.PAIR -> R.string.point_smartapp_demo_app_bluetooth_paired
                BluetoothDeviceAction.FORGET -> R.string.point_smartapp_demo_app_bluetooth_forgotten
            },
            isError = !success,
        )
    }

    fun failed(@StringRes message: Int = R.string.point_smartapp_demo_app_bluetooth_operation_failed) = copy(
        changingPower = false,
        discovering = false,
        pendingOperation = null,
        forgetConfirmationAddress = null,
        message = message,
        isError = true,
    )
}
