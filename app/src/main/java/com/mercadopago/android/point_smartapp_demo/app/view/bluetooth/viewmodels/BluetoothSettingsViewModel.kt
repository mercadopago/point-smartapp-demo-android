package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import com.mercadopago.android.point_integration_sdk.nativesdk.MPManager
import com.mercadopago.android.point_integration_sdk.nativesdk.bluetoothclient.provider.contracts.states.BluetoothDiscoveryState
import com.mercadopago.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadopago.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts.BluetoothSettingsEvents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class BluetoothSettingsViewModel : ViewModel() {

    private val _bluetoothSettingLiveData =
        MutableStateFlow<BluetoothSettingsEvents>(BluetoothSettingsEvents.Init)
    val bluetoothSettingLiveData = _bluetoothSettingLiveData.asStateFlow()

    fun registerConnectObserver() {
        MPManager.bluetooth.connectObserver.registerObserver { result ->
            result
                .doIfSuccess { pair ->
                    val bluetoothDeviceModel = pair.first
                    _bluetoothSettingLiveData.value =
                        BluetoothSettingsEvents.ConnectDevicesResult(bluetoothDeviceModel)
                }.doIfError { exception ->
                    _bluetoothSettingLiveData.value = BluetoothSettingsEvents.Error(exception)
                }
        }
    }

    fun getCurrentStateBluetooth() {
        MPManager.bluetooth.ignitor.getCurrentState { response ->
            response
                .doIfSuccess { result ->
                    _bluetoothSettingLiveData.value =
                        BluetoothSettingsEvents.IgnitorCurrentState(result)
                }.doIfError { exception ->
                    _bluetoothSettingLiveData.value = BluetoothSettingsEvents.Error(exception)
                }
        }
    }

    fun ignitorBluetooth(ignitor: Boolean) {
        MPManager.bluetooth.ignitor.run {
            if (ignitor) {
                turnOn { response ->
                    response
                        .doIfSuccess { result ->
                            _bluetoothSettingLiveData.value =
                                BluetoothSettingsEvents.IgnitorLaunchResult(result)
                        }
                        .doIfError { exception ->
                            _bluetoothSettingLiveData.value =
                                BluetoothSettingsEvents.Error(exception)
                        }
                }
            } else {
                turnOff { response ->
                    response
                        .doIfSuccess { result ->
                            _bluetoothSettingLiveData.value =
                                BluetoothSettingsEvents.IgnitorLaunchResult(result)
                        }
                        .doIfError { exception ->
                            _bluetoothSettingLiveData.value =
                                BluetoothSettingsEvents.Error(exception)
                        }
                }
            }
        }
    }

    fun getPairDevices() {

        MPManager.bluetooth.discover.getPairedDevices { response ->
            response
                .doIfSuccess { result ->
                    _bluetoothSettingLiveData.value =
                        BluetoothSettingsEvents.DiscoveryPairDevicesResult(result)
                }
                .doIfError { exception ->
                    _bluetoothSettingLiveData.value = BluetoothSettingsEvents.Error(exception)
                }
        }
    }

    fun startBluetoothDiscovery() {
        MPManager.bluetooth.discover.startDiscovery { mpResponse ->
            mpResponse.doIfSuccess { discoveryState ->
                when (discoveryState.type) {
                    BluetoothDiscoveryState.Type.STARTED -> onBluetoothType(BluetoothSettingsEvents.DiscoveryStarted)
                    BluetoothDiscoveryState.Type.ENDED -> onBluetoothType(BluetoothSettingsEvents.DiscoveryEnded)
                    BluetoothDiscoveryState.Type.DEVICE_FOUND -> discoveryState.device?.let { device ->
                        onBluetoothType(BluetoothSettingsEvents.DiscoveryDevicesFound(device))
                    }

                    BluetoothDiscoveryState.Type.DEVICE_CHANGE -> discoveryState.device?.let { device ->
                        onBluetoothType(BluetoothSettingsEvents.DiscoveryDevicesUpdate(device))
                    }
                }

            }.doIfError { error ->
                onBluetoothType(BluetoothSettingsEvents.Error(error))
            }
        }
    }

    private fun onBluetoothType(discoveryStarted: BluetoothSettingsEvents) {
        _bluetoothSettingLiveData.value = discoveryStarted
    }

    @SuppressLint("LogConditional")
    fun pairingDevices(address: String, needPair: Boolean) {
        Log.i(TAG, "pairingDevices: devices address --> $address, needPair --> $needPair")
        if (needPair) {
            MPManager.bluetooth.paring.pairDevice(address) { response ->
                response
                    .doIfSuccess { resultPair ->
                        Log.i(TAG, "pairDevices: callback response ${resultPair.first}")
                        _bluetoothSettingLiveData.value =
                            BluetoothSettingsEvents.PairingDevicesStatus(resultPair)
                    }
                    .doIfError { exception ->
                        _bluetoothSettingLiveData.value =
                            BluetoothSettingsEvents.Error(exception)
                    }
            }
        } else {
            MPManager.bluetooth.paring.unPairDevice(address) { response ->
                response
                    .doIfSuccess { resultPair ->
                        Log.i(TAG, "unPairDevices: callback response ${resultPair.first}")
                        _bluetoothSettingLiveData.value =
                            BluetoothSettingsEvents.PairingDevicesStatus(resultPair)
                    }.doIfError { exception ->
                        _bluetoothSettingLiveData.value =
                            BluetoothSettingsEvents.Error(exception)
                    }
            }
        }
    }

    companion object {
        const val TAG = "pairingDevicesWay"
    }
}
