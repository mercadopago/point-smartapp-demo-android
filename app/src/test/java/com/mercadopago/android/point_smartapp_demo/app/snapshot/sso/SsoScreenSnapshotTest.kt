package com.mercadopago.android.point_smartapp_demo.app.snapshot.sso

import app.cash.paparazzi.Paparazzi
import androidx.compose.ui.res.stringResource
import com.mercadolibre.android.point_integration_sdk.nativesdk.exception.GenericErrorCode
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.snapshot.SnapshotConfiguration
import com.mercadopago.android.point_smartapp_demo.app.view.sso.SingleSignOnScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoTheme
import org.junit.Rule
import org.junit.Test

class SsoScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = SnapshotConfiguration.N950,
        theme = SnapshotConfiguration.THEME,
        showSystemUi = false,
    )

    @Test
    fun initial() = snapshot()

    @Test
    fun processing() = snapshot(loading = true, message = R.string.point_smartapp_demo_app_sso_processing)

    @Test
    fun waitingForAuthorization() = snapshot(loading = true, message = R.string.point_smartapp_demo_app_sso_waiting)

    @Test
    fun jwtReturned() = snapshot(message = R.string.point_smartapp_demo_app_sso_success, token = SYNTHETIC_JWT)

    @Test
    fun networkError() = snapshot(networkError = true)

    private fun snapshot(
        loading: Boolean = false,
        message: Int? = null,
        token: String? = null,
        networkError: Boolean = false,
    ) {
        paparazzi.snapshot {
            DemoTheme {
                val status = if (networkError) {
                    stringResource(
                        R.string.point_smartapp_demo_app_sso_error,
                        GenericErrorCode.NETWORK_UNAVAILABLE,
                        NETWORK_ERROR_DETAIL,
                    )
                } else {
                    message?.let { stringResource(it) }
                }
                SingleSignOnScreen(
                    loading = loading,
                    statusMessage = status,
                    statusIsError = networkError,
                    identityToken = token,
                    onRequestIdentityToken = {},
                )
            }
        }
    }

    private companion object {
        const val NETWORK_ERROR_DETAIL = "Network unavailable."

        private const val JWT_HEADER =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9"

        private const val JWT_PAYLOAD =
            "eyJpc3MiOiJodHRwczovL2V4YW1wbGUuaW52YWxpZCIsImF1ZCI6ImRlbW8tYXBwIiwic3ViIjoiZGVt" +
            "by11c2VyIiwibm9uY2UiOiJkZW1vLXByZXZpZXciLCJpYXQiOjE3OTI3NzEyMDAsImV4cCI6MTc5Mjc3" +
            "MTUwMH0"

        private const val INVALID_SIGNATURE =
            "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7" +
            "PD0-P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3" +
            "eHl6e3x9fn-AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmqq6ytrq-wsbKz" +
            "tLW2t7i5uru8vb6_wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t_g4eLj5OXm5-jp6uvs7e7v" +
            "8PHy8_T19vf4-fr7_P3-_w"
        const val SYNTHETIC_JWT = "$JWT_HEADER.$JWT_PAYLOAD.$INVALID_SIGNATURE"
    }
}
