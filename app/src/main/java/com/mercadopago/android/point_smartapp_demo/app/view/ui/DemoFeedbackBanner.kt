package com.mercadopago.android.point_smartapp_demo.app.view.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
internal fun DemoFeedbackBanner(
    feedback: DemoFeedback?,
    onDismiss: () -> Unit,
) {
    if (feedback != null) {
        DemoMessage(
            message = feedback.message,
            isError = feedback.isError,
            onDismiss = onDismiss,
        )
        LaunchedEffect(feedback) {
            delay(2750)
            onDismiss()
        }
    }
}
