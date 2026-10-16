package com.mercadopago.android.point_smartapp_demo.app.view.home

import com.mercadopago.android.point_smartapp_demo.app.actions.contract.HomeActions

internal data class HomeTile(
    val title: String,
    val iconRes: Int,
    val action: HomeActions,
)
