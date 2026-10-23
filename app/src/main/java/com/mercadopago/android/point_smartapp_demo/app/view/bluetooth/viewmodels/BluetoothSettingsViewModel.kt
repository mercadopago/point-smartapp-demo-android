package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.viewmodels

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.contracts.states.BluetoothBondState
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.contracts.states.BluetoothConnectionState
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.contracts.states.BluetoothDiscoveryState
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.entities.BluetoothDeviceModel
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.MPResponse
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts.BluetoothDeviceAction
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts.BluetoothSettingsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class BluetoothSettingsViewModel : ViewModel() {
    private val bluetooth = MPManager.bluetooth
    private val _state = MutableStateFlow(BluetoothSettingsState())
    val state = _state.asStateFlow()
    private var started = false
    private var operationTimeout: Job? = null

    fun start() {
        if (!started) {
            started = true
            bluetooth.connectObserver.registerObserver { response ->
                viewModelScope.launch {
                    response.doIfSuccess { (device, connection) ->
                        if (connection != BluetoothConnectionState.UNDEFINED) {
                            _state.update { it.connectionChanged(device, connection == BluetoothConnectionState.CONNECTED) }
                        }
                    }
                }
            }
        }
        if (!_state.value.busy) {
            _state.update { it.endDiscovery() }
            readPower()
        }
    }

    fun stop() {
        _state.update { it.endDiscovery() }
    }

    fun changePower(enabled: Boolean) {
        if (_state.value.busy || enabled == _state.value.enabled) return
        readPower(enabled)
    }

    private fun readPower(requested: Boolean? = null) {
        _state.update { it.copy(changingPower = true, discovering = false, message = null, isError = false) }
        val callback: (MPResponse<Boolean>) -> Unit = { response ->
            viewModelScope.launch {
                response.doIfSuccess { enabled ->
                    _state.update { it.powerChanged(enabled) }
                    if (enabled) refreshPairedDevices()
                }.doIfError { _state.update { it.failed() } }
            }
        }
        when (requested) {
            true -> bluetooth.ignitor.turnOn(callback)
            false -> bluetooth.ignitor.turnOff(callback)
            null -> bluetooth.ignitor.getCurrentState(callback)
        }
    }

    fun search() {
        if (!_state.value.enabled || _state.value.busy || _state.value.discovering) return
        _state.update { it.copy(
            discovering = true,
            hasSearched = true,
            devices = it.devices.filterValues { device -> device.boundState == BluetoothDevice.BOND_BONDED },
            message = null,
            isError = false,
        ) }
        refreshPairedDevices {
            if (!_state.value.discovering || _state.value.busy) return@refreshPairedDevices
            bluetooth.discover.startDiscovery { response ->
                viewModelScope.launch {
                    if (_state.value.enabled && _state.value.discovering) {
                        response.doIfSuccess { discovery ->
                            when (discovery.type) {
                                BluetoothDiscoveryState.Type.STARTED -> Unit
                                BluetoothDiscoveryState.Type.ENDED -> _state.update { it.endDiscovery() }
                                BluetoothDiscoveryState.Type.DEVICE_FOUND,
                                BluetoothDiscoveryState.Type.DEVICE_CHANGE -> discovery.device?.let { device ->
                                    _state.update { it.discovered(device) }
                                }
                            }
                        }.doIfError { _state.update { it.failed() } }
                    }
                }
            }
        }
    }

    fun pair(address: String) = performDeviceAction(address, BluetoothDeviceAction.PAIR)

    fun requestForget(address: String) {
        if (_state.value.busy || _state.value.pairedDevices.none { it.address == address }) return
        _state.update { it.copy(forgetConfirmationAddress = address) }
    }

    fun cancelForget() {
        _state.update { it.copy(forgetConfirmationAddress = null) }
    }

    fun confirmForget() {
        _state.value.forgetConfirmationAddress?.let { performDeviceAction(it, BluetoothDeviceAction.FORGET) }
    }

    private fun performDeviceAction(address: String, action: BluetoothDeviceAction) {
        if (_state.value.busy) return
        _state.update { it.beginOperation(address, action) }
        val operation = _state.value.pendingOperation ?: return
        operationTimeout = viewModelScope.launch {
            delay(DEVICE_STATUS_CHECK_DELAY_MS)
            refreshPairedDevices()
            delay(DEVICE_OPERATION_TIMEOUT_MS - DEVICE_STATUS_CHECK_DELAY_MS)
            if (_state.value.pendingOperation === operation) {
                _state.update { it.failed(R.string.point_smartapp_demo_app_bluetooth_operation_timeout) }
                refreshPairedDevices()
            }
        }
        val callback: (MPResponse<Pair<BluetoothBondState, BluetoothDeviceModel>>) -> Unit = { response ->
            viewModelScope.launch {
                if (_state.value.pendingOperation === operation) {
                    response.doIfSuccess { (bond, device) ->
                        _state.update { it.bondChanged(bond, device) }
                    }.doIfError { _state.update { it.failed() } }
                    if (_state.value.pendingOperation == null) {
                        operationTimeout?.cancel()
                        refreshPairedDevices()
                    }
                }
            }
        }
        if (action == BluetoothDeviceAction.PAIR) bluetooth.paring.pairDevice(address, callback)
        else bluetooth.paring.unPairDevice(address, callback)
    }

    private fun refreshPairedDevices(onSuccess: () -> Unit = {}) {
        bluetooth.discover.getPairedDevices { response ->
            viewModelScope.launch {
                if (_state.value.enabled && !_state.value.changingPower) {
                    response.doIfSuccess { devices ->
                        _state.update { it.pairedDevicesChanged(devices) }
                        if (_state.value.pendingOperation == null) operationTimeout?.cancel()
                        onSuccess()
                    }.doIfError { _state.update { it.failed() } }
                }
            }
        }
    }

    fun dismissMessage() {
        _state.update { it.copy(message = null, isError = false) }
    }

    private companion object {
        const val DEVICE_STATUS_CHECK_DELAY_MS = 5_000L
        const val DEVICE_OPERATION_TIMEOUT_MS = 30_000L
    }
}
