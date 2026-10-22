package com.mercadopago.android.point_smartapp_demo.app.view.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.mercadopago.android.point_smartapp_demo.app.R
import kotlinx.coroutines.delay

@Composable
internal fun DemoMessage(
    message: String,
    isError: Boolean = false,
    onDismiss: (() -> Unit)? = null,
) {
    var announcement by remember(message) { mutableStateOf("") }
    LaunchedEffect(message) {
        delay(50)
        announcement = message
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                liveRegion = if (isError) LiveRegionMode.Assertive else LiveRegionMode.Polite
                contentDescription = announcement
            },
        color = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
            )
            if (onDismiss != null) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.point_smartapp_demo_app_accessibility_dismiss_message))
                }
            }
        }
    }
}
