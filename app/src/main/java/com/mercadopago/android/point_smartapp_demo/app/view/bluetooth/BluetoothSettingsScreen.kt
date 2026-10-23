package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mercadolibre.android.point_integration_sdk.nativesdk.bluetoothclient.provider.entities.BluetoothDeviceModel
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts.BluetoothDeviceAction
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.contracts.BluetoothSettingsState
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoMessage
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoProgress
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSecondaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSectionTitle
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSwitch

@Composable
internal fun BluetoothSettingsScreen(
    state: BluetoothSettingsState,
    onPowerChange: (Boolean) -> Unit,
    onSearch: () -> Unit,
    onPair: (String) -> Unit,
    onForget: (String) -> Unit,
    onConfirmForget: () -> Unit,
    onCancelForget: () -> Unit,
    onDismissMessage: () -> Unit,
) {
    DemoScreen(title = stringResource(R.string.point_smartapp_demo_app_bluetooth_title)) {
        DemoSwitch(
            label = stringResource(R.string.point_smartapp_demo_app_bluetooth),
            selected = state.enabled,
            onSelectedChange = onPowerChange,
            enabled = !state.busy,
        )
        if (state.changingPower) DemoProgress(stringResource(R.string.point_smartapp_demo_app_bluetooth_changing_power))
        state.message?.let { DemoMessage(stringResource(it), state.isError, onDismissMessage) }
        if (state.enabled) {
            DemoSecondaryButton(
                text = stringResource(R.string.point_smartapp_demo_app_bluetooth_search),
                onClick = onSearch,
                enabled = !state.busy && !state.discovering,
            )
            if (state.discovering) DemoProgress(stringResource(R.string.point_smartapp_demo_app_bluetooth_searching))
            DemoSectionTitle(stringResource(R.string.point_smartapp_demo_app_bluetooth_pair_devices_label))
            if (state.pairedDevices.isEmpty()) Text(stringResource(R.string.point_smartapp_demo_app_bluetooth_no_paired))
            state.pairedDevices.forEach { device ->
                key(device.address) { BluetoothDeviceRow(device, state, paired = true, onAction = onForget) }
            }
            DemoSectionTitle(stringResource(R.string.point_smartapp_demo_app_bluetooth_discovery_devices_label))
            if (state.availableDevices.isEmpty() && state.hasSearched && !state.discovering) {
                Text(stringResource(R.string.point_smartapp_demo_app_bluetooth_no_available))
            }
            state.availableDevices.forEach { device ->
                key(device.address) { BluetoothDeviceRow(device, state, paired = false, onAction = onPair) }
            }
        } else if (!state.changingPower) {
            Text(stringResource(R.string.point_smartapp_demo_app_bluetooth_disabled))
        }
    }
    state.forgetConfirmationAddress?.let { address ->
        state.devices[address]?.let { device ->
            AlertDialog(
                onDismissRequest = onCancelForget,
                title = { Text(stringResource(R.string.point_smartapp_demo_app_bluetooth_forget_title)) },
                text = { Text(stringResource(R.string.point_smartapp_demo_app_bluetooth_forget_description, device.name.ifBlank { device.address })) },
                confirmButton = {
                    TextButton(onClick = onConfirmForget) { Text(stringResource(R.string.point_smartapp_demo_app_bluetooth_forget)) }
                },
                dismissButton = {
                    TextButton(onClick = onCancelForget) { Text(stringResource(R.string.point_smartapp_demo_app_bluetooth_cancel)) }
                },
            )
        }
    }
}

@Composable
private fun BluetoothDeviceRow(
    device: BluetoothDeviceModel,
    state: BluetoothSettingsState,
    paired: Boolean,
    onAction: (String) -> Unit,
) {
    val pending = state.pendingOperation?.takeIf { it.address == device.address }
    val status = when {
        pending?.action == BluetoothDeviceAction.PAIR -> R.string.point_smartapp_demo_app_bluetooth_pairing
        pending?.action == BluetoothDeviceAction.FORGET -> R.string.point_smartapp_demo_app_bluetooth_forgetting
        device.isConnected -> R.string.point_smartapp_demo_app_bluetooth_devices_connect
        paired -> R.string.point_smartapp_demo_app_bluetooth_paired_label
        else -> R.string.point_smartapp_demo_app_bluetooth_available_label
    }
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(
                    painter = painterResource(if (device.isConnected) R.drawable.point_smartapp_demo_app_ic_bluetooth_connected else R.drawable.point_smartapp_demo_app_ic_bluetooth_available),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(device.name.ifBlank { device.address }, style = MaterialTheme.typography.titleMedium)
                    Text(device.address, style = MaterialTheme.typography.bodySmall)
                    Text(stringResource(status), style = MaterialTheme.typography.bodySmall)
                }
                if (pending != null) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            }
            TextButton(
                onClick = { onAction(device.address) },
                enabled = !state.busy,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(stringResource(if (paired) R.string.point_smartapp_demo_app_bluetooth_forget else R.string.point_smartapp_demo_app_bluetooth_pair))
            }
        }
    }
}
