package com.mercadopago.android.point_smartapp_demo.app.actions.contract

import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadopago.android.point_smartapp_demo.app.view.home.HomeActivity

sealed class HomeActions {
   class LaunchActivity(val activity: Class<*>) : HomeActions()
   class LaunchBtUi(val actionManager:MPManager) : HomeActions()
}
