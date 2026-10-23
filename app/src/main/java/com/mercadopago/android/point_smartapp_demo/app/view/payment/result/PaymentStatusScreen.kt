package com.mercadopago.android.point_smartapp_demo.app.view.payment.result

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoField
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoMessage
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoPrimaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSectionTitle

@Composable
internal fun PaymentStatusScreen(
    reference: String,
    online: Boolean,
    loading: Boolean,
    result: PaymentStatusResult?,
    errorMessage: String?,
    onReferenceChange: (String) -> Unit,
    onModeChange: (Boolean) -> Unit,
    onQuery: () -> Unit,
) {
    DemoScreen(title = stringResource(R.string.point_smartapp_demo_app_payment_status_title)) {
        Text(
            text = stringResource(R.string.point_smartapp_demo_app_payment_status_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        DemoSectionTitle(stringResource(R.string.point_smartapp_demo_app_payment_status_query_type))
        PaymentStatusQuerySelector(online = online, enabled = !loading, onModeChange = onModeChange)
        DemoField(
            label = stringResource(R.string.point_smartapp_demo_app_lab_payment_external_reference),
            value = reference,
            onValueChange = onReferenceChange,
            enabled = !loading,
            imeAction = ImeAction.Search,
            keyboardActions = KeyboardActions(onSearch = { onQuery() }),
        )
        Text(
            text = stringResource(R.string.point_smartapp_demo_app_payment_status_reference_helper),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        DemoPrimaryButton(
            text = stringResource(
                if (loading) R.string.point_smartapp_demo_app_payment_status_loading
                else R.string.point_smartapp_demo_app_payment_status_search,
            ),
            loading = loading,
            enabled = reference.isNotBlank(),
            onClick = onQuery,
        )
        errorMessage?.let { DemoMessage(it, isError = true) }
        result?.let { PaymentStatusResultCard(it) }
    }
}
