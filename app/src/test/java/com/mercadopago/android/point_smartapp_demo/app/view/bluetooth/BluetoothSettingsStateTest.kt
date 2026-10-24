package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth

import android.bluetooth.BluetoothDevice
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.contracts.states.BluetoothBondState
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.entities.BluetoothDeviceModel
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts.BluetoothDeviceAction
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts.BluetoothSettingsState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BluetoothSettingsStateTest {
    @Test
    fun repeatedDiscoveryUpdatesTheSameAddress() {
        val device = device()
        val renamed = device.copy(name = UPDATED_NAME)
        val state = enabled().discovered(device).discovered(renamed)

        assertEquals(listOf(renamed), state.availableDevices)
    }

    @Test
    fun devicesWithTheSameNameAndDifferentAddressesRemainSeparate() {
        val state = enabled().discovered(device()).discovered(device(OTHER_ADDRESS))

        assertEquals(2, state.availableDevices.size)
    }

    @Test
    fun pairedDevicesAreNotRepeatedInAvailableDevices() {
        val available = device()
        val paired = available.copy(boundState = BluetoothDevice.BOND_BONDED)
        val state = enabled().discovered(available).pairedDevicesChanged(listOf(paired)).discovered(available)

        assertEquals(listOf(paired), state.pairedDevices)
        assertTrue(state.availableDevices.isEmpty())
    }

    @Test
    fun connectionChangesUseTheAddressInsteadOfModelEquality() {
        val original = device().copy(boundState = BluetoothDevice.BOND_BONDED)
        val state = enabled().pairedDevicesChanged(listOf(original))
            .connectionChanged(original.copy(name = UPDATED_NAME), connected = true)

        assertEquals(1, state.devices.size)
        assertTrue(state.pairedDevices.single().isConnected)
    }

    @Test
    fun pairingKeepsProgressUntilBondedAndUsesTheLatestDevice() {
        val device = device()
        val bonding = enabled().discovered(device).beginOperation(ADDRESS, BluetoothDeviceAction.PAIR)
            .bondChanged(BluetoothBondState.BONDING, device)
        val bonded = bonding.bondChanged(BluetoothBondState.BONDED, device.copy(name = UPDATED_NAME))

        assertNotNull(bonding.pendingOperation)
        assertNull(bonded.pendingOperation)
        assertEquals(UPDATED_NAME, bonded.pairedDevices.single().name)
        assertTrue(bonded.availableDevices.isEmpty())
        assertFalse(bonded.isError)
    }

    @Test
    fun canceledPairingAndSuccessfulForgettingHaveDifferentFeedback() {
        val device = device()
        val canceled = enabled().discovered(device).beginOperation(ADDRESS, BluetoothDeviceAction.PAIR)
            .bondChanged(BluetoothBondState.NONE, device)
        val forgotten = enabled().pairedDevicesChanged(listOf(device.copy(boundState = BluetoothDevice.BOND_BONDED)))
            .beginOperation(ADDRESS, BluetoothDeviceAction.FORGET).bondChanged(BluetoothBondState.NONE, device)

        assertTrue(canceled.isError)
        assertEquals(R.string.point_smartapp_demo_app_bluetooth_pairing_failed, canceled.message)
        assertFalse(forgotten.isError)
        assertEquals(R.string.point_smartapp_demo_app_bluetooth_forgotten, forgotten.message)
        assertTrue(forgotten.devices.isEmpty())
    }

    @Test
    fun unrelatedDeviceEventsDoNotFinishThePendingOperation() {
        val state = enabled().discovered(device()).beginOperation(ADDRESS, BluetoothDeviceAction.PAIR)
            .bondChanged(BluetoothBondState.BONDED, device(OTHER_ADDRESS))

        assertEquals(ADDRESS, state.pendingOperation?.address)
        assertEquals(state, state.beginOperation(OTHER_ADDRESS, BluetoothDeviceAction.FORGET))
    }

    @Test
    fun errorsAndPowerOffClearProgress() {
        val state = enabled().discovered(device()).beginOperation(ADDRESS, BluetoothDeviceAction.PAIR)
        val failed = state.failed()

        assertFalse(failed.busy)
        assertFalse(failed.discovering)
        assertTrue(failed.isError)
        assertEquals(BluetoothSettingsState(), state.powerChanged(false))
        assertEquals(BluetoothSettingsState(), BluetoothSettingsState().discovered(device()))
    }

    @Test
    fun pairedSnapshotFinishesPairingWhenTheFinalEventIsMissing() {
        val device = device()
        val state = enabled().discovered(device).beginOperation(ADDRESS, BluetoothDeviceAction.PAIR)
            .pairedDevicesChanged(listOf(device.copy(boundState = BluetoothDevice.BOND_BONDED)))

        assertNull(state.pendingOperation)
        assertFalse(state.isError)
        assertTrue(state.availableDevices.isEmpty())
        assertEquals(ADDRESS, state.pairedDevices.single().address)
        assertEquals(R.string.point_smartapp_demo_app_bluetooth_paired, state.message)
    }

    @Test
    fun pairedSnapshotFinishesForgettingWhenTheFinalEventIsMissing() {
        val paired = device().copy(boundState = BluetoothDevice.BOND_BONDED)
        val state = enabled().pairedDevicesChanged(listOf(paired)).beginOperation(ADDRESS, BluetoothDeviceAction.FORGET)
            .pairedDevicesChanged(emptyList())

        assertNull(state.pendingOperation)
        assertTrue(state.devices.isEmpty())
        assertEquals(R.string.point_smartapp_demo_app_bluetooth_forgotten, state.message)
    }

    @Test
    fun pairedSnapshotKeepsWaitingWhenPairingIsNotConfirmed() {
        val pending = enabled().discovered(device()).beginOperation(ADDRESS, BluetoothDeviceAction.PAIR)

        assertEquals(pending.pendingOperation, pending.pairedDevicesChanged(emptyList()).pendingOperation)
    }

    @Test
    fun endingDiscoveryRemovesFoundDevicesAndKeepsPairedDevices() {
        val available = device()
        val paired = device(OTHER_ADDRESS).copy(boundState = BluetoothDevice.BOND_BONDED)
        val state = enabled().discovered(available).pairedDevicesChanged(listOf(paired)).endDiscovery()

        assertFalse(state.discovering)
        assertTrue(state.availableDevices.isEmpty())
        assertEquals(listOf(paired), state.pairedDevices)
        assertEquals(state, state.beginOperation(ADDRESS, BluetoothDeviceAction.PAIR))
        assertEquals(state, state.discovered(available))
    }

    @Test
    fun unpairedConnectionEventsDoNotRepopulateFoundDevices() {
        val state = BluetoothSettingsState(enabled = true)

        assertEquals(state, state.connectionChanged(device(), connected = false))
    }

    @Test
    fun endingDiscoveryKeepsTheDeviceBeingPaired() {
        val state = enabled().discovered(device()).discovered(device(OTHER_ADDRESS))
            .beginOperation(ADDRESS, BluetoothDeviceAction.PAIR).endDiscovery()

        assertEquals(setOf(ADDRESS), state.devices.keys)
        assertEquals(ADDRESS, state.pendingOperation?.address)
    }

    @Test
    fun failingAnOperationRemovesTheStaleDevice() {
        val state = enabled().discovered(device()).beginOperation(ADDRESS, BluetoothDeviceAction.PAIR).failed()

        assertTrue(state.availableDevices.isEmpty())
        assertFalse(state.busy)
    }

    private fun enabled() = BluetoothSettingsState(enabled = true, discovering = true)

    private fun device(address: String = ADDRESS) = BluetoothDeviceModel(
        id = address,
        address = address,
        name = DEVICE_NAME,
        boundState = BluetoothDevice.BOND_NONE,
        isConnected = false,
    )

    private companion object {
        const val ADDRESS = "00:00:00:00:00:01"
        const val OTHER_ADDRESS = "00:00:00:00:00:02"
        const val DEVICE_NAME = "Demo keyboard"
        const val UPDATED_NAME = "Updated keyboard"
    }
}
