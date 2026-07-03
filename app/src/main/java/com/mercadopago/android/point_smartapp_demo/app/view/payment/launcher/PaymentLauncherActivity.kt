package com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.exception.SDKException
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PayerCondition
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PaymentRequestData
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.PaymentTransactionMetadata
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.domain.model.PaymentResponseData
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.provider.PaymentFlowCallback
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointSmartappDemoAppActivityPaymentLauncherBinding
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

/** Main activity class */
class PaymentLauncherActivity : AppCompatActivity() {

    lateinit var binding: PointSmartappDemoAppActivityPaymentLauncherBinding
    private val paymentFlow = MPManager.paymentFlow
    private val paymentTool = MPManager.paymentMethodsTools
    private var lastPaymentMethodSelected: String? = null
    private var clearPaymentMethodList: Boolean = true
    private var isPrintOnTerminal: Boolean = true
    private val paymentMethodAdapter by lazy {
        PaymentMethodAdapter {
            lastPaymentMethodSelected = it
        }
    }
    private var pendingPaymentAmount: String? = null
    private var pendingPaymentDescription: String? = null
    private var pendingExternalReference: String? = null
    private val installmentsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleInstallmentsResult(result.resultCode, result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PointSmartappDemoAppActivityPaymentLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerviewPaymentMethod.apply {
            layoutManager = LinearLayoutManager(
                this@PaymentLauncherActivity, LinearLayoutManager.VERTICAL, false
            )
            adapter = paymentMethodAdapter
        }
        binding.externalReferenceInputLayout.visibility = View.GONE

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
                val externalReference = binding.externalReferenceEditText.text?.toString()
                launchPaymentFlow(amount, description, externalReference)
            }
        }
    }

    private fun launchPaymentFlow(amount: String?, description: String?, externalReference: String?) = when {
        amount.isNullOrEmpty() -> ERROR_INVALID_AMOUNT.setLayoutError()

        isCreditCard() -> checkInstallmentsAndProceed(amount, description, externalReference)

        else -> launchPaymentFlowIntent(
            amount = amount,
            description = description,
            installments = null,
            externalReference = externalReference
        )
    }

    private fun checkInstallmentsAndProceed(amount: String, description: String?, externalReference: String?) {
        binding.paymentProgressBar.visible()
        MPManager.paymentInstallmentTools.getInstallmentsAmount({ mpResponse ->
            binding.paymentProgressBar.gone()
            mpResponse.doIfSuccess { installments ->
                if (installments.isNotEmpty()) {
                    launchInstallmentsSelection(amount, description, externalReference)
                } else {
                    launchPaymentFlowIntent(amount, description, installments = null, externalReference = externalReference)
                }
            }.doIfError {
                launchPaymentFlowIntent(amount, description, installments = null, externalReference = externalReference)
            }
        }, amount)
    }

    private fun launchInstallmentsSelection(amount: String, description: String?, externalReference: String?) {
        pendingPaymentAmount = amount
        pendingPaymentDescription = description
        pendingExternalReference = externalReference
        val intent = Intent(this, PaymentFlowInstallmentsActivity::class.java).apply {
            putExtra(AMOUNT, amount)
        }
        installmentsLauncher.launch(intent)
    }

    private fun isCreditCard() = lastPaymentMethodSelected == "credit_card"

    private fun configPaymentMethodList() {
        paymentTool.getPaymentMethodsList { response ->
            response.doIfSuccess { result ->
                val paymentMethodList = result.map { PaymentMethodModel(name = it) }
                paymentMethodAdapter.submitList(paymentMethodList)
            }.doIfError { error ->
                toast(error.message.orEmpty())
            }
        }
    }

    private fun launchPaymentFlowIntent(
        amount: String,
        description: String?,
        installments: Int? = null,
        externalReference: String? = null
    ) {
        val paymentRequestData = PaymentRequestData.builder(amount.toBigDecimal())
            .setDescription(description)
            .setPaymentMethod(lastPaymentMethodSelected)
            .setInstallments(installments)
            .setPrintOnTerminal(isPrintOnTerminal)
            .setTaxes(binding.payerCondition.getSelectedValue()?.toTaxes())
            .setPaymentTransactionMetadata(
                externalReference?.takeIf { it.isNotEmpty() }?.let {
                    PaymentTransactionMetadata(externalReference = it)
                }
            )
            .build()

        paymentFlow.launchPaymentFlow(paymentRequestData, object : PaymentFlowCallback {
            override fun onProgress() {
                binding.paymentProgressBar.visible()
            }

            override fun onSuccess(data: PaymentResponseData) {
                binding.paymentProgressBar.gone()
                showSnackBar(MESSAGE_PAYMENT_SUCCESS.format(data.paymentReference))
            }

            override fun onError(error: SDKException) {
                binding.paymentProgressBar.gone()
                error.message?.let { message ->
                    showSnackBar(MESSAGE_PAYMENT_CANCELED.format(message), true)
                }
            }
        })
    }

    private fun handleInstallmentsResult(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val selectedInstallments = data?.getIntExtra(EXTRA_INSTALLMENTS_RESULT, 0)
            pendingPaymentAmount?.let { amount ->
                launchPaymentFlowIntent(
                    amount = amount,
                    description = pendingPaymentDescription,
                    installments = selectedInstallments,
                    externalReference = pendingExternalReference
                )
            }
        }
        pendingPaymentAmount = null
        pendingPaymentDescription = null
        pendingExternalReference = null
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
            binding.root, message, Snackbar.ANIMATION_MODE_SLIDE
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
