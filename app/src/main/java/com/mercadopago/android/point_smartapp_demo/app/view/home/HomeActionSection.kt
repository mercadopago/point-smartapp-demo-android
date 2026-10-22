package com.mercadopago.android.point_smartapp_demo.app.view.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mercadopago.android.point_smartapp_demo.app.actions.contract.HomeActions
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSectionTitle

@Composable
internal fun HomeActionSection(
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
