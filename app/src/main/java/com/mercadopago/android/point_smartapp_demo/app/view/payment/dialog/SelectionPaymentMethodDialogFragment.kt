package com.mercadopago.android.point_smartapp_demo.app.view.payment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoMessage
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoPrimaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoRadioOption
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoTheme
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.PaymentMethodModel

class SelectionPaymentMethodDialogFragment : BottomSheetDialogFragment() {

    private var paymentMethods by mutableStateOf<List<PaymentMethodModel>>(emptyList())
    private var lastPaymentMethodSelected by mutableStateOf<String?>(null)
    private var loadError by mutableStateOf<String?>(null)

    var onListenerPaymentMethod: (String) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val doneText = getString(R.string.point_smartapp_demo_app_lab_done_copy)
        val dialogTitle = getString(R.string.point_smartapp_demo_app_payment_method_dialog_title)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DemoTheme {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .verticalScroll(rememberScrollState())
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp,
                            )
                            .semantics { paneTitle = dialogTitle },
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        loadError?.let { message ->
                            DemoMessage(message = message, isError = true)
                        }
                        paymentMethods.forEach { method ->
                            DemoRadioOption(
                                text = method.name,
                                selected = lastPaymentMethodSelected == method.name,
                                onClick = { lastPaymentMethodSelected = method.name },
                            )
                        }
                        DemoPrimaryButton(
                            text = doneText,
                            enabled = lastPaymentMethodSelected != null,
                            onClick = {
                                lastPaymentMethodSelected?.let { paymentMethod ->
                                    onListenerPaymentMethod(paymentMethod)
                                    dismiss()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configPaymentMethodList()
    }

    private fun configPaymentMethodList() {
        MPManager.paymentMethodsTools.getPaymentMethods { response ->
            response.doIfSuccess { result ->
                loadError = null
                paymentMethods = result.map { PaymentMethodModel(name = it.name) }
            }.doIfError {
                loadError = getString(R.string.point_smartapp_demo_app_payment_methods_load_error)
            }
        }
    }

    companion object {
        fun newInstance(): SelectionPaymentMethodDialogFragment = SelectionPaymentMethodDialogFragment()
    }
}
