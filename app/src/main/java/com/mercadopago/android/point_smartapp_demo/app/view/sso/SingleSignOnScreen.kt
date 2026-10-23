package com.mercadopago.android.point_smartapp_demo.app.view.sso

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoInfoRow
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoMessage
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoPrimaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen

@Composable
internal fun SingleSignOnScreen(
    loading: Boolean,
    statusMessage: String?,
    statusIsError: Boolean,
    identityToken: String?,
    onRequestIdentityToken: () -> Unit,
) {
    DemoScreen(title = stringResource(R.string.point_smartapp_demo_app_sso_title)) {
        DemoPrimaryButton(
            text = stringResource(R.string.point_smartapp_demo_app_sso_request),
            onClick = onRequestIdentityToken,
            loading = loading,
        )
        statusMessage?.let { DemoMessage(message = it, isError = statusIsError) }
        identityToken?.let { token ->
            SelectionContainer {
                DemoInfoRow(
                    label = stringResource(R.string.point_smartapp_demo_app_sso_identity_token),
                    value = token,
                )
            }
        }
    }
}
