package com.mercadopago.android.point_smartapp_demo.app.snapshot

import app.cash.paparazzi.DeviceConfig

internal object SnapshotConfiguration {
    const val THEME = "android:Theme.Material.Light.NoActionBar"
    private const val N950_WIDTH_PX = 720
    private const val N950_HEIGHT_PX = 1440
    private const val N950_DENSITY_DPI = 320

    val N950 = DeviceConfig(
        screenWidth = N950_WIDTH_PX,
        screenHeight = N950_HEIGHT_PX,
        xdpi = N950_DENSITY_DPI,
        ydpi = N950_DENSITY_DPI,
    )
}
