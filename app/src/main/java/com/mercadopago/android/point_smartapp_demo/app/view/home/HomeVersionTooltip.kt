package com.mercadopago.android.point_smartapp_demo.app.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.mercadopago.android.point_smartapp_demo.app.R

@Composable
internal fun HomeVersionTooltip(versionText: String) {
    var isVisible by remember { mutableStateOf(false) }
    val tooltipOffsetY = with(LocalDensity.current) { 44.dp.roundToPx() }
    val versionAccessibilityDescription = stringResource(
        R.string.point_smartapp_demo_app_home_version_accessibility,
    )

    Box {
        IconButton(
            onClick = { isVisible = !isVisible },
            modifier = Modifier.semantics {
                contentDescription = versionAccessibilityDescription
            },
        )
        {
            Text(
                text = "i",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
        }
        if (isVisible) {
            Popup(
                alignment = Alignment.TopEnd,
                offset = IntOffset(x = 0, y = tooltipOffsetY),
                onDismissRequest = { isVisible = false },
                properties = PopupProperties(focusable = true),
            ) {
                val tooltipShape = RoundedCornerShape(8.dp)
                Text(
                    modifier = Modifier
                        .shadow(4.dp, tooltipShape)
                        .clip(tooltipShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, Color(0xFFD9DDE8), tooltipShape)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    text = versionText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}
