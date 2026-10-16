package com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PayerCondition
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PaymentFlowRequestData
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PaymentMethod
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.util.hideKeyboard
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoCheckbox
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoDropdown
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedback
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedbackBanner
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoField
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoPrimaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoProgress
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoRadioOption
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSecondaryButton
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoSectionTitle
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentFlowInstallmentsActivity.Companion.AMOUNT
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentFlowInstallmentsActivity.Companion.EXTRA_INSTALLMENTS_RESULT
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.PaymentMethodModel
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.toTaxes

class PaymentLauncherLegacyActivity : AppCompatActivity() {

    private val paymentFlow = MPManager.paymentFlow
    private val paymentTool = MPManager.paymentMethodsTools
    private var lastPaymentMethodSelected by mutableStateOf<PaymentMethod?>(null)
    private var clearPaymentMethodList by mutableStateOf(true)
    private var isPrintOnTerminal by mutableStateOf(true)
    private var amount by mutableStateOf("")
    private var description by mutableStateOf("")
    private var payerCondition by mutableStateOf(NO_TAX)
    private var paymentMethods by mutableStateOf<List<PaymentMethodModel>>(emptyList())
    private var paymentMethodActionLabel by mutableStateOf("")
    private var amountError by mutableStateOf<String?>(null)
    private var isLoading by mutableStateOf(false)
    private var feedback by mutableStateOf<DemoFeedback?>(null)
    private var pendingPaymentAmount: String? = null
    private var pendingPaymentDescription: String? = null

    private val installmentsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result -> handleInstallmentsResult(result.resultCode, result.data) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = getString(R.string.point_smartapp_demo_app_go_to_payment_legacy)
        val amountLabel = getString(R.string.point_smartapp_demo_app_lab_payment_amount)
        val descriptionLabel = getString(R.string.point_smartapp_demo_app_lab_payment_description)
        val taxLabel = getString(R.string.point_smartapp_demo_app_lab_tax)
        val automaticPrintingLabel = getString(R.string.point_smartapp_demo_app_description_is_automatic_printing)
        val sendPaymentLabel = getString(R.string.point_smartapp_demo_app_lab_send_payment_action)
        val loadPaymentMethodsLabel = getString(R.string.point_smartapp_demo_app_lab_get_payment_method_action)
        val paymentMethodTitle = "Payment method"
        val progressLabel = "Processing payment"
        paymentMethodActionLabel = loadPaymentMethodsLabel
        val payerConditions = listOf(NO_TAX) + PayerCondition.values().map { it.name }

        setDemoContent {
            DemoScreen(title = title) {
                DemoField(
                    label = amountLabel,
                    value = amount,
                    onValueChange = { amount = it },
                    keyboardType = KeyboardType.Decimal,
                    errorMessage = amountError,
                )
                DemoField(
                    label = descriptionLabel,
                    value = description,
                    onValueChange = { description = it },
                )
                DemoDropdown(
                    label = taxLabel,
                    selected = payerCondition,
                    options = payerConditions,
                    onSelected = { payerCondition = it },
                )
                DemoCheckbox(
                    label = automaticPrintingLabel,
                    checked = isPrintOnTerminal,
                    onCheckedChange = { isPrintOnTerminal = it },
                )
                DemoSectionTitle(text = paymentMethodTitle)
                paymentMethods.forEach { method ->
                    DemoRadioOption(
                        text = method.name,
                        selected = lastPaymentMethodSelected?.name == method.name,
                        onClick = { lastPaymentMethodSelected = PaymentMethod.valueOf(method.name) },
                    )
                }
                DemoSecondaryButton(
                    text = paymentMethodActionLabel,
                    onClick = ::togglePaymentMethodList,
                )
                if (isLoading) DemoProgress(label = progressLabel)
                DemoFeedbackBanner(feedback) { feedback = null }
                DemoPrimaryButton(
                    text = sendPaymentLabel,
                    onClick = {
                        hideKeyboard()
                        launchPaymentFlow(amount, description)
                    },
                )
            }
        }
    }

    private fun togglePaymentMethodList() {
        hideKeyboard()
        clearPaymentMethodList = clearPaymentMethodList.not()
        if (clearPaymentMethodList) {
            paymentMethodActionLabel = getString(R.string.point_smartapp_demo_app_lab_get_payment_method_action)
            lastPaymentMethodSelected = null
            paymentMethods = emptyList()
        } else {
            paymentMethodActionLabel = getString(R.string.point_smartapp_demo_app_clear_label)
            configPaymentMethodList()
        }
    }

    private fun launchPaymentFlow(amount: String?, description: String?) {
        when {
            amount.isNullOrEmpty() -> amountError = ERROR_INVALID_AMOUNT
            isCreditCard() -> checkInstallmentsAndProceed(amount, description)
            else -> {
                amountError = null
                launchPaymentFlowIntent(amount, description, installments = null)
            }
        }
    }

    private fun checkInstallmentsAndProceed(amount: String, description: String?) {
        isLoading = true
        MPManager.paymentInstallmentTools.getInstallmentsAmount({ response ->
            isLoading = false
            response.doIfSuccess { installments ->
                if (installments.isNotEmpty()) {
                    launchInstallmentsSelection(amount, description)
                } else {
                    launchPaymentFlowIntent(amount, description, installments = null)
                }
            }.doIfError {
                launchPaymentFlowIntent(amount, description, installments = null)
            }
        }, amount)
    }

    private fun launchInstallmentsSelection(amount: String, description: String?) {
        pendingPaymentAmount = amount
        pendingPaymentDescription = description
        val intent = Intent(this, PaymentFlowInstallmentsActivity::class.java).apply {
            putExtra(AMOUNT, amount)
        }
        installmentsLauncher.launch(intent)
    }

    private fun isCreditCard() = lastPaymentMethodSelected == PaymentMethod.CREDIT_CARD

    private fun configPaymentMethodList() {
        paymentTool.getPaymentMethods { response ->
            response.doIfSuccess { result ->
                paymentMethods = result.map { PaymentMethodModel(name = it.name) }
            }.doIfError {
                showFeedback(getString(R.string.point_smartapp_demo_app_payment_methods_load_error), isError = true)
            }
        }
    }

    private fun launchPaymentFlowIntent(
        amount: String,
        description: String?,
        installments: Int? = null,
    ) {
        isLoading = true
        val paymentRequestData = PaymentFlowRequestData(
            amount = amount.toDouble(),
            description = description,
            paymentMethod = lastPaymentMethodSelected,
            printOnTerminal = isPrintOnTerminal,
            taxes = payerCondition.takeIf { it != NO_TAX }?.toTaxes(),
        ).apply {
            installments?.let { setInstallmentsForCreditCard(it) }
        }

        paymentFlow.launchPaymentFlow(paymentRequestData) { response ->
            isLoading = false
            response.doIfSuccess {
                showFeedback(MESSAGE_PAYMENT_SUCCESS.format(it.paymentReference))
            }.doIfError {
                it.message?.let { message -> showFeedback(MESSAGE_PAYMENT_CANCELED.format(message), true) }
            }
        }
    }

    private fun handleInstallmentsResult(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val selectedInstallments = data?.getIntExtra(EXTRA_INSTALLMENTS_RESULT, 0)
            pendingPaymentAmount?.let { amountValue ->
                launchPaymentFlowIntent(
                    amount = amountValue,
                    description = pendingPaymentDescription,
                    installments = selectedInstallments,
                )
            }
        }
        pendingPaymentAmount = null
        pendingPaymentDescription = null
    }

    private fun showFeedback(message: String, isError: Boolean = false) {
        feedback = DemoFeedback(message, isError)
    }

    companion object {
        private const val ERROR_INVALID_AMOUNT = "Amount is null or empty"
        private const val MESSAGE_PAYMENT_CANCELED = "Your payment was %s"
        private const val MESSAGE_PAYMENT_SUCCESS = "Your payment reference is: %s"
        private const val NO_TAX = "NO TAX"
    }
}
