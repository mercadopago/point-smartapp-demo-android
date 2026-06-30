package com.mercadopago.android.point_smartapp_demo.app.actions.contract

import android.content.Context
import com.mercadopago.android.point_smartapp_demo.app.actions.model.ActionModel

interface ActionsProvider {
    fun getActions(context: Context): List<ActionModel>
}
