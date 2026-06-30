package com.mercadopago.android.point_smartapp_demo.app.util

import android.content.Context
import android.content.Intent
import android.os.Bundle

fun Context.launchActivity(destination: Class<*>, bundle: Bundle? = null) {
    Intent(this, destination).run {
        bundle?.let {
            putExtras(bundle)
        }
        startActivity(this)
    }
}
