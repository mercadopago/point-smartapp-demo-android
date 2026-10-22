package com.mercadopago.android.point_smartapp_demo.app.view.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy

internal fun ComponentActivity.setDemoContent(content: @Composable () -> Unit) {
    val composeView = ComposeView(this).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            DemoTheme(content = content)
        }
    }
    setContentView(composeView)
}
