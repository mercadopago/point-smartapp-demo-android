package com.mercadopago.android.point_smartapp_demo.app.view.bluetooth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.viewmodels.BluetoothSettingsViewModel
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent

class BluetoothTestActivity : AppCompatActivity() {
    private val viewModel by viewModels<BluetoothSettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDemoContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            BluetoothSettingsScreen(
                state = state,
                onPowerChange = viewModel::changePower,
                onSearch = viewModel::search,
                onPair = viewModel::pair,
                onForget = viewModel::requestForget,
                onConfirmForget = viewModel::confirmForget,
                onCancelForget = viewModel::cancelForget,
                onDismissMessage = viewModel::dismissMessage,
            )
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.start()
    }

    override fun onStop() {
        viewModel.stop()
        super.onStop()
    }
}
