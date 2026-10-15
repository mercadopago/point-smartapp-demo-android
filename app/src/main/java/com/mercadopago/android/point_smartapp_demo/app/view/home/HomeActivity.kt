package com.mercadopago.android.point_smartapp_demo.app.view.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.mercadopago.android.point_smartapp_demo.app.ActionsProviderImpl
import com.mercadopago.android.point_smartapp_demo.app.BuildConfig
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.actions.contract.HomeActions
import com.mercadopago.android.point_smartapp_demo.app.actions.model.ActionModel
import com.mercadopago.android.point_smartapp_demo.app.util.launchActivity
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSectionTitle
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSecondaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.BluetoothTestActivity
import com.mercadopago.android.point_smartapp_demo.app.view.bluetooth.printer.PrinterTestActivity
import com.mercadopago.android.point_smartapp_demo.app.view.camera.LaunchScannerActivity
import com.mercadopago.android.point_smartapp_demo.app.view.info.SmartInfoActivity
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentLauncherActivity
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentLauncherLegacyActivity
import com.mercadopago.android.point_smartapp_demo.app.view.payment.result.PaymentStatusApprovedActivity
import com.mercadopago.android.point_smartapp_demo.app.view.printer.PrinterBitmapActivity
import com.mercadopago.android.point_smartapp_demo.app.view.printer.PrinterCustomTagActivity
import com.mercadopago.android.point_smartapp_demo.app.view.refunds.RefundsActivity

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
                        actionsByActivity[PaymentStatusApprovedActivity::class.java],
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

private data class HomeSection(
    val title: String,
    val tiles: List<HomeTile>,
)

private data class HomeTile(
    val title: String,
    val iconRes: Int,
    val action: HomeActions,
)

@Composable
private fun HomeActionSection(
    section: HomeSection,
    onActionClick: (HomeActions) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        DemoSectionTitle(text = section.title)
        section.tiles.chunked(2).forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowTiles.forEach { tile ->
                    HomeActionTile(
                        tile = tile,
                        modifier = Modifier.weight(1f),
                        onActionClick = onActionClick,
                    )
                }
                if (rowTiles.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HomeGroupDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .height(1.dp)
            .background(Color(0xFFD9DDE8)),
    )
}

@Composable
private fun HomeActionTile(
    tile: HomeTile,
    modifier: Modifier,
    onActionClick: (HomeActions) -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Color(0xFFD9DDE8), shape)
            .clickable(role = Role.Button) { onActionClick(tile.action) }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(tile.iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = tile.title,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun HomeVersionTooltip(versionText: String) {
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
