package com.mercadopago.android.point_smartapp_demo.app.view.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.mercadopago.android.point_smartapp_demo.app.ActionsProviderImpl
import com.mercadopago.android.point_smartapp_demo.app.BuildConfig
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.actions.contract.HomeActions
import com.mercadopago.android.point_smartapp_demo.app.actions.model.ActionModel
import com.mercadopago.android.point_smartapp_demo.app.util.launchActivity
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.BluetoothTestActivity
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.printer.PrinterTestActivity
import com.mercadopago.android.point_smartapp_demo.app.view.camera.LaunchScannerActivity
import com.mercadopago.android.point_smartapp_demo.app.view.info.SmartInfoActivity
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentLauncherActivity
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentLauncherLegacyActivity
import com.mercadopago.android.point_smartapp_demo.app.view.payment.result.PaymentStatusActivity
import com.mercadopago.android.point_smartapp_demo.app.view.printer.PrinterBitmapActivity
import com.mercadopago.android.point_smartapp_demo.app.view.printer.PrinterCustomTagActivity
import com.mercadopago.android.point_smartapp_demo.app.view.refunds.RefundsActivity
import com.mercadopago.android.point_smartapp_demo.app.view.sso.SingleSignOnActivity
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSecondaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent

class HomeActivity : AppCompatActivity() {

    private var showPrinterOptions by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actions = ActionsProviderImpl.getActions(this)
        val screenTitle = getString(R.string.point_smartapp_demo_app_title)
        val versionText = getString(
            R.string.point_smartapp_demo_app_version_name,
            BuildConfig.VERSION_NAME,
        )
        val actionsByActivity = actions.mapNotNull { actionModel ->
            (actionModel.action as? HomeActions.LaunchActivity)?.let { it.activity to actionModel }
        }.toMap()
        val bluetoothSettingsAction = actions.firstOrNull {
            it.action is HomeActions.LaunchBtUi
        }
        val printerOptionsAction = actions.firstOrNull {
            it.action is HomeActions.OpenPointPrinterOptions
        }
        val printerOptionsTitle = getString(R.string.point_smartapp_demo_app_home_tile_point_printer)
        val printerOptions = listOf(
            HomeTile(
                title = getString(R.string.point_smartapp_demo_app_printer_option_image),
                iconRes = R.drawable.point_smartapp_demo_app_black_ic_print,
                action = HomeActions.LaunchActivity(PrinterBitmapActivity::class.java),
            ),
            HomeTile(
                title = getString(R.string.point_smartapp_demo_app_printer_option_custom_tags),
                iconRes = R.drawable.point_smartapp_demo_app_black_ic_print,
                action = HomeActions.LaunchActivity(PrinterCustomTagActivity::class.java),
            ),
        )

        fun tile(titleRes: Int, actionModel: ActionModel?): HomeTile? = actionModel?.let {
            HomeTile(
                title = getString(titleRes),
                iconRes = it.iconRes,
                action = it.action,
            )
        }

        val sections = listOf(
            HomeSection(
                title = getString(R.string.point_smartapp_demo_app_home_group_payments),
                tiles = listOfNotNull(
                    tile(
                        R.string.point_smartapp_demo_app_home_tile_legacy,
                        actionsByActivity[PaymentLauncherLegacyActivity::class.java],
                    ),
                    tile(
                        R.string.point_smartapp_demo_app_home_tile_payment,
                        actionsByActivity[PaymentLauncherActivity::class.java],
                    ),
                    tile(
                        R.string.point_smartapp_demo_app_home_tile_status,
                        actionsByActivity[PaymentStatusActivity::class.java],
                    ),
                    tile(
                        R.string.point_smartapp_demo_app_home_tile_refunds,
                        actionsByActivity[RefundsActivity::class.java],
                    ),
                ),
            ),
            HomeSection(
                title = getString(R.string.point_smartapp_demo_app_home_group_printing),
                tiles = listOfNotNull(
                    tile(
                        R.string.point_smartapp_demo_app_home_tile_bluetooth_printer,
                        actionsByActivity[PrinterTestActivity::class.java],
                    ),
                    tile(
                        R.string.point_smartapp_demo_app_home_tile_point_printer,
                        printerOptionsAction,
                    ),
                ),
            ),
            HomeSection(
                title = getString(R.string.point_smartapp_demo_app_home_group_bluetooth),
                tiles = listOfNotNull(
                    tile(
                        R.string.point_smartapp_demo_app_home_tile_bluetooth_tools,
                        actionsByActivity[BluetoothTestActivity::class.java],
                    ),
                    tile(
                        R.string.point_smartapp_demo_app_home_tile_bluetooth_settings,
                        bluetoothSettingsAction,
                    ),
                ),
            ),
        ).filter { it.tiles.isNotEmpty() }

        val standaloneTiles = listOfNotNull(
            tile(
                R.string.point_smartapp_demo_app_sso_title,
                actionsByActivity[SingleSignOnActivity::class.java],
            ),
            tile(
                R.string.point_smartapp_demo_app_home_tile_camera_scanner,
                actionsByActivity[LaunchScannerActivity::class.java],
            ),
            tile(
                R.string.point_smartapp_demo_app_home_tile_smart_info,
                actionsByActivity[SmartInfoActivity::class.java],
            ),
        )

        setDemoContent {
            if (showPrinterOptions) {
                DemoScreen(
                    title = printerOptionsTitle,
                    onBack = { showPrinterOptions = false },
                ) {
                    Text(text = getString(R.string.point_smartapp_demo_app_printer_options_description))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        printerOptions.forEach { option ->
                            DemoSecondaryButton(
                                text = option.title,
                                onClick = { handlerActionItem(option.action) },
                            )
                        }
                    }
                }
            } else {
                DemoScreen(
                    title = screenTitle,
                    showBack = false,
                    titleLeadingContent = {
                        Image(
                            painter = painterResource(R.drawable.point_smartapp_demo_app_point_home),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                .padding(3.dp),
                        )
                    },
                    titleTrailingContent = { HomeVersionTooltip(versionText) },
                ) {
                    sections.forEachIndexed { index, section ->
                        HomeActionSection(
                            section = section,
                            onActionClick = ::handlerActionItem,
                        )
                        if (index < sections.lastIndex || standaloneTiles.isNotEmpty()) {
                            HomeGroupDivider()
                        }
                    }
                    standaloneTiles.forEach { tile ->
                        HomeActionTile(
                            tile = tile,
                            modifier = Modifier.fillMaxWidth(),
                            onActionClick = ::handlerActionItem,
                        )
                    }
                }
            }
        }
    }

    private fun handlerActionItem(action: HomeActions) {
        when (action) {
            is HomeActions.LaunchActivity -> launchActivity(action.activity)
            is HomeActions.LaunchBtUi -> action.actionManager.bluetoothUiSettings.launch(this@HomeActivity)
            HomeActions.OpenPointPrinterOptions -> showPrinterOptions = true
        }
    }
}
