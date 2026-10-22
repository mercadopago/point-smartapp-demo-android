package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.contracts.states.BluetoothBondState
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.entities.BluetoothDeviceModel
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoMessage
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedback
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoProgress
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSwitch
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts.BluetoothSettingsEvents.*
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.viewmodels.BluetoothSettingsViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/** test activity bluetooth. */
class BluetoothTestActivity : AppCompatActivity() {

    private val viewModel by viewModels<BluetoothSettingsViewModel>()
    private var bluetoothEnabled by mutableStateOf(false)
    private var isDiscoveryLoading by mutableStateOf(false)
    private var isDiscoverySectionVisible by mutableStateOf(false)
    private var pairedDevices by mutableStateOf<List<BluetoothDeviceModel>>(emptyList())
    private var availableDevices by mutableStateOf<List<BluetoothDeviceModel>>(emptyList())
    private var feedback by mutableStateOf<DemoFeedback?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = getString(R.string.point_smartapp_demo_app_bluetooth_title)
        val pairedDevicesLabel = getString(R.string.point_smartapp_demo_app_bluetooth_pair_devices_label)
        val foundDevicesLabel = getString(R.string.point_smartapp_demo_app_bluetooth_discovery_devices_label)
        val connectedLabel = getString(R.string.point_smartapp_demo_app_bluetooth_devices_connect)
        val ignitionLabel = getString(R.string.point_smartapp_demo_app_bluetooth)
        val readerMessage = getString(R.string.point_smartapp_demo_app_bluetooth_connect_reader_message)

        setDemoContent {
            DemoScreen(title = title) {
                Text(text = readerMessage)
                DemoSwitch(
                    label = ignitionLabel,
                    selected = bluetoothEnabled,
                    onSelectedChange = ::onBluetoothSwitchChanged,
                )
                if (isDiscoveryLoading) DemoProgress(label = foundDevicesLabel)
                if (pairedDevices.isNotEmpty()) {
                    Text(text = pairedDevicesLabel, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        pairedDevices.forEach { device ->
                            BluetoothDeviceRow(
                                device = device,
                                connectedLabel = connectedLabel,
                                onClick = { onDeviceSelected(device.address, needPair = false) },
                            )
                        }
                    }
                }
                if (isDiscoverySectionVisible) {
                    Text(text = foundDevicesLabel, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        availableDevices.forEach { device ->
                            BluetoothDeviceRow(
                                device = device,
                                connectedLabel = connectedLabel,
                                onClick = { onDeviceSelected(device.address, needPair = true) },
                            )
                        }
                    }
                }
                feedback?.let { result ->
                    DemoMessage(message = result.message, isError = result.isError)
                }
            }
        }

        viewModel.getCurrentStateBluetooth()
        setObserver()
    }

    private fun onBluetoothSwitchChanged(isChecked: Boolean) {
        bluetoothEnabled = isChecked
        if (isChecked) {
            viewModel.registerConnectObserver()
        } else {
            availableDevices = emptyList()
        }
        viewModel.ignitorBluetooth(isChecked)
    }

    private fun onDeviceSelected(address: String, needPair: Boolean) {
        isDiscoveryLoading = true
        viewModel.pairingDevices(address = address, needPair = needPair)
    }

    private fun setObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bluetoothSettingLiveData.collect { events ->
                    when (events) {
                        DiscoveryEnded -> isDiscoveryLoading = false
                        is DiscoveryDevicesFound -> {
                            isDiscoverySectionVisible = true
                            availableDevices = availableDevices + events.devices
                        }
                        is DiscoveryDevicesUpdate -> availableDevices = availableDevices.update(events.devices)
                        DiscoveryStarted -> isDiscoveryLoading = true
                        is DiscoveryPairDevicesResult -> pairedDevices = events.devicesList
                        is IgnitorCurrentState -> updateIgnitorBluetooth(events.state)
                        is IgnitorLaunchResult -> canLaunchGetPairDevices(events.result)
                        is PairingDevicesStatus -> handlePairingResult(events.pair)
                        Init -> Unit
                        is ConnectDevicesResult -> pairedDevices = pairedDevices.update(events.connectDevices)
                        is Error -> showErrorFeedback()
                    }
                }
            }
        }
    }

    private fun handlePairingResult(pair: Pair<BluetoothBondState, BluetoothDeviceModel>) {
        val bondState = pair.first
        val device = pair.second
        val deviceInAvailableList = availableDevices.find { it.address == device.address }
        viewModel.getPairDevices()

        when (bondState) {
                BluetoothBondState.NONE -> {
                availableDevices = if (deviceInAvailableList != null) {
                    availableDevices.update(deviceInAvailableList)
                } else {
                    availableDevices + device
                }
                Toast.makeText(this, "devices forget", Toast.LENGTH_SHORT).show()
            }
            BluetoothBondState.BONDING -> {
                if (deviceInAvailableList != null) availableDevices = availableDevices.update(deviceInAvailableList)
            }
            BluetoothBondState.BONDED -> {
                if (deviceInAvailableList != null) {
                    availableDevices = availableDevices.filterNot { it.address == device.address }
                    Toast.makeText(this, "pair devices", Toast.LENGTH_SHORT).show()
                }
            }
        }
        isDiscoverySectionVisible = availableDevices.isNotEmpty()
        isDiscoveryLoading = false
    }

    private fun updateIgnitorBluetooth(state: Boolean) {
        bluetoothEnabled = state
        canLaunchGetPairDevices(state)
    }

    private fun canLaunchGetPairDevices(launch: Boolean) {
        if (launch) {
            viewModel.getPairDevices()
            viewModel.startBluetoothDiscovery()
        } else {
            isDiscoverySectionVisible = false
            availableDevices = emptyList()
            pairedDevices = emptyList()
        }
    }

    private fun showErrorFeedback() {
        feedback = DemoFeedback(
            getString(R.string.point_smartapp_demo_app_bluetooth_operation_failed),
            isError = true,
        )
    }

    private fun List<BluetoothDeviceModel>.update(device: BluetoothDeviceModel): List<BluetoothDeviceModel> {
        val index = indexOf(device)
        if (index < 0) return this
        return toMutableList().also { it[index] = device }
    }

    @androidx.compose.runtime.Composable
    private fun BluetoothDeviceRow(
        device: BluetoothDeviceModel,
        connectedLabel: String,
        onClick: () -> Unit,
    ) {
        val isBonded = device.boundState == BluetoothDevice.BOND_BONDED
        val iconRes = if (isBonded && device.isConnected) {
            R.drawable.point_smartapp_demo_app_ic_bluetooth_connected
        } else {
            R.drawable.point_smartapp_demo_app_ic_bluetooth_available
        }
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
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
                    Text(text = device.name.ifEmpty { device.address })
                    if (isBonded && device.isConnected) {
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
