package com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.mercadopago.android.point_integration_sdk.nativesdk.MPManager
import com.mercadopago.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadopago.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_integration_sdk.nativesdk.payment.data.PayerCondition
import com.mercadopago.android.point_integration_sdk.nativesdk.payment.data.PaymentFlowRequestData
import com.mercadopago.android.point_integration_sdk.nativesdk.payment.data.PaymentMethod
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointMainappDemoAppActivityPaymentLauncherBinding
import com.mercadopago.android.point_smartapp_demo.app.util.gone
import com.mercadopago.android.point_smartapp_demo.app.util.hideKeyboard
import com.mercadopago.android.point_smartapp_demo.app.util.launchActivity
import com.mercadopago.android.point_smartapp_demo.app.util.toast
import com.mercadopago.android.point_smartapp_demo.app.util.visible
import com.mercadopago.android.point_smartapp_demo.app.view.payment.adapter.PaymentMethodAdapter
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentFlowInstallmentsActivity.Companion.AMOUNT
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentFlowInstallmentsActivity.Companion.DESCRIPTION
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentFlowInstallmentsActivity.Companion.PAYER_CONDITION
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentFlowInstallmentsActivity.Companion.PAYMENT_METHOD
import com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher.PaymentFlowInstallmentsActivity.Companion.PRINT_ON_TERMINAL
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.PayerConditionString
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.PaymentMethodModel
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.toTaxes

/** Main activity class */
class PaymentLauncherActivity : AppCompatActivity() {

    lateinit var binding: PointMainappDemoAppActivityPaymentLauncherBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PointMainappDemoAppActivityPaymentLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerviewPaymentMethod.apply {
            layoutManager = LinearLayoutManager(
                this@PaymentLauncherActivity, LinearLayoutManager.VERTICAL, false
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

        isCreditCard() -> launchActivity(
            PaymentFlowInstallmentsActivity::class.java, bundleOf(
                PAYMENT_METHOD to lastPaymentMethodSelected?.name,
                AMOUNT to amount,
                DESCRIPTION to description,
                PRINT_ON_TERMINAL to isPrintOnTerminal,
                PAYER_CONDITION to binding.payerCondition.getSelectedValue()
            )
        )

        else -> launchPaymentFlowIntent(
            amount = amount, description = description
        )
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
        amount: String, description: String?
    ) {
        binding.paymentProgressBar.visible()
        paymentFlow.launchPaymentFlow(
            PaymentFlowRequestData(
                amount = amount.toDouble(),
                description = description,
                paymentMethod = lastPaymentMethodSelected,
                printOnTerminal = isPrintOnTerminal,
                taxes = binding.payerCondition.getSelectedValue()?.toTaxes()
            )
        ) { response ->
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
