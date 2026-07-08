package com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PayerCondition
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PaymentFlowRequestData
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PaymentMethod
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointSmartappDemoAppActivityPaymentLauncherLegacyBinding
import com.mercadopago.android.point_smartapp_demo.app.util.gone
import com.mercadopago.android.point_smartapp_demo.app.util.hideKeyboard
import com.mercadopago.android.point_smartapp_demo.app.util.toast
import com.mercadopago.android.point_smartapp_demo.app.util.visible
import com.mercadopago.android.point_smartapp_demo.app.view.payment.adapter.PaymentMethodAdapter
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentFlowInstallmentsActivity.Companion.AMOUNT
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentFlowInstallmentsActivity.Companion.EXTRA_INSTALLMENTS_RESULT
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.PayerConditionString
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.PaymentMethodModel
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.toTaxes

class PaymentLauncherLegacyActivity : AppCompatActivity() {

    lateinit var binding: PointSmartappDemoAppActivityPaymentLauncherLegacyBinding
    private val paymentFlow = MPManager.paymentFlow
    private val paymentTool = MPManager.paymentMethodsTools
    private var lastPaymentMethodSelected: PaymentMethod? = null
    private var clearPaymentMethodList: Boolean = true
    private var isPrintOnTerminal: Boolean = true
    private val paymentMethodAdapter by lazy {
        PaymentMethodAdapter {
            lastPaymentMethodSelected = PaymentMethod.valueOf(it)
        }
    }
    private var pendingPaymentAmount: String? = null
    private var pendingPaymentDescription: String? = null
    private val installmentsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleInstallmentsResult(result.resultCode, result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PointSmartappDemoAppActivityPaymentLauncherLegacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerviewPaymentMethod.apply {
            layoutManager = LinearLayoutManager(
                this@PaymentLauncherLegacyActivity, LinearLayoutManager.VERTICAL, false
            )
            adapter = paymentMethodAdapter
        }

        configPaymentButton()
        configPayerConditionDropDown()
        isAutomaticPrintOnTerminal()
    }

    private fun configPayerConditionDropDown() {
        binding.payerCondition.setText(NO_TAX)
        val finalList = listOf(NO_TAX) + PayerCondition.values().map { it.name }
        binding.payerCondition.setSimpleItems(finalList.toTypedArray())
    }

    private fun isAutomaticPrintOnTerminal() =
        binding.checkboxIsAutomaticPrinting.setOnCheckedChangeListener { _, isChecked ->
            isPrintOnTerminal = isChecked
        }

    private fun configPaymentButton() {

        binding.apply {
            getPaymentMethodActionButton.setOnClickListener {
                hideKeyboard()
                clearPaymentMethodList = clearPaymentMethodList.not()
                if (clearPaymentMethodList) {
                    getPaymentMethodActionButton.text =
                        getString(R.string.point_smartapp_demo_app_lab_get_payment_method_action)
                    lastPaymentMethodSelected = null
                    paymentMethodAdapter.clear()
                } else {
                    getPaymentMethodActionButton.text =
                        getString(R.string.point_smartapp_demo_app_clear_label)
                    configPaymentMethodList()
                }
            }
            sendPaymentActionButton.setOnClickListener {
                val amount = amountEditText.text?.toString()
                val description = binding.descriptionEditText.text?.toString()
                launchPaymentFlow(amount, description)
            }
        }
    }

    private fun launchPaymentFlow(amount: String?, description: String?) = when {
        amount.isNullOrEmpty() -> ERROR_INVALID_AMOUNT.setLayoutError()

        isCreditCard() -> checkInstallmentsAndProceed(amount, description)

        else -> launchPaymentFlowIntent(
            amount = amount,
            description = description,
            installments = null
        )
    }

    private fun checkInstallmentsAndProceed(amount: String, description: String?) {
        binding.paymentProgressBar.visible()
        MPManager.paymentInstallmentTools.getInstallmentsAmount({ mpResponse ->
            binding.paymentProgressBar.gone()
            mpResponse.doIfSuccess { installments ->
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
                val paymentMethodList = result.map { PaymentMethodModel(name = it.name) }
                paymentMethodAdapter.submitList(paymentMethodList)
            }.doIfError { error ->
                toast(error.message.orEmpty())
            }
        }
    }

    private fun launchPaymentFlowIntent(
        amount: String,
        description: String?,
        installments: Int? = null
    ) {
        binding.paymentProgressBar.visible()
        val paymentRequestData = PaymentFlowRequestData(
            amount = amount.toDouble(),
            description = description,
            paymentMethod = lastPaymentMethodSelected,
            printOnTerminal = isPrintOnTerminal,
            taxes = binding.payerCondition.getSelectedValue()?.toTaxes()
        ).apply {
            installments?.let { setInstallmentsForCreditCard(it) }
        }

        paymentFlow.launchPaymentFlow(paymentRequestData) { response ->
            binding.paymentProgressBar.gone()
            response.doIfSuccess {
                showSnackBar(MESSAGE_PAYMENT_SUCCESS.format(it.paymentReference))
            }.doIfError {
                it.message?.let { message ->
                    showSnackBar(MESSAGE_PAYMENT_CANCELED.format(message), true)
                }
            }
        }
    }

    private fun handleInstallmentsResult(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val selectedInstallments = data?.getIntExtra(EXTRA_INSTALLMENTS_RESULT, 0)
            pendingPaymentAmount?.let { amount ->
                launchPaymentFlowIntent(
                    amount = amount,
                    description = pendingPaymentDescription,
                    installments = selectedInstallments
                )
            }
        }
        pendingPaymentAmount = null
        pendingPaymentDescription = null
    }

    private fun String?.setLayoutError() {

        binding.amountInputLayout.apply {
            isCounterEnabled = true
            error = this@setLayoutError
        }

        listenerIconError()
    }

    private fun listenerIconError() {

        binding.amountInputLayout.apply {
            setErrorIconOnClickListener {
                isErrorEnabled = false
            }
        }
    }

    private fun showSnackBar(message: String, isCanceled: Boolean = false) {
        Snackbar.make(
            binding.root, message, Snackbar.LENGTH_LONG
        ).setBackgroundTint(getBackgroundColorSnackBar(isCanceled)).show()
    }

    private fun getBackgroundColorSnackBar(canceled: Boolean): Int = if (canceled) {
        getColor(R.color.design_default_color_error)
    } else {
        getColor(R.color.doneColor)
    }

    private fun MaterialAutoCompleteTextView.getSelectedValue(): PayerConditionString? =
        text.toString().takeIf { it != NO_TAX }

    companion object {
        private const val ERROR_INVALID_AMOUNT = "Amount is null or empty"
        private const val MESSAGE_PAYMENT_CANCELED = "Your payment was %s"
        private const val MESSAGE_PAYMENT_SUCCESS = "Your payment reference is: %s"
        private const val NO_TAX = "NO TAX"
    }
}
