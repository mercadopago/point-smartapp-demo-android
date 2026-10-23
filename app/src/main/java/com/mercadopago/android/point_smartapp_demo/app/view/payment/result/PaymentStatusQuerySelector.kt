package com.mercadopago.android.point_smartapp_demo.app.view.payment.result

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mercadopago.android.point_smartapp_demo.app.R

@Composable
internal fun PaymentStatusQuerySelector(
    online: Boolean,
    enabled: Boolean,
    onModeChange: (Boolean) -> Unit,
) {
    TabRow(selectedTabIndex = if (online) 1 else 0) {
        Tab(
            selected = !online,
            enabled = enabled,
            onClick = { onModeChange(false) },
            text = { Text(stringResource(R.string.point_smartapp_demo_app_payment_status_local)) },
        )
        Tab(
            selected = online,
            enabled = enabled,
            onClick = { onModeChange(true) },
            text = { Text(stringResource(R.string.point_smartapp_demo_app_payment_status_online)) },
        )
    }
}
