package com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mercadopago.android.point_integration_sdk.nativesdk.MPManager
import com.mercadopago.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadopago.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_integration_sdk.nativesdk.payment.data.PaymentFlowRequestData
import com.mercadopago.android.point_integration_sdk.nativesdk.payment.data.PaymentMethod
import com.mercadopago.android.point_integration_sdk.nativesdk.utils.ifLet
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointMainappDemoAppActivityPaymentFlowInstallmetsBinding
import com.mercadopago.android.point_smartapp_demo.app.util.gone
import com.mercadopago.android.point_smartapp_demo.app.util.visible
import com.mercadopago.android.point_smartapp_demo.app.view.payment.adapter.PaymentInstallmentAdapter
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.PayerConditionString
import com.mercadopago.android.point_smartapp_demo.app.view.payment.models.toTaxes

class PaymentFlowInstallmentsActivity : AppCompatActivity() {

    private val binding: PointMainappDemoAppActivityPaymentFlowInstallmetsBinding by lazy {
        PointMainappDemoAppActivityPaymentFlowInstallmetsBinding.inflate(layoutInflater)
    }

    private val paymentFlow = MPManager.paymentFlow

    private val amount by lazy { intent.getStringExtra(AMOUNT) }
    private val paymentMethod by lazy { intent.getStringExtra(PAYMENT_METHOD) }
    private val description by lazy { intent.getStringExtra(DESCRIPTION) }
    private val printOnTerminal by lazy { intent.getBooleanExtra(PRINT_ON_TERMINAL, false) }
    private val payerCondition: PayerConditionString? by lazy { intent.getStringExtra(PAYER_CONDITION) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setView()
        val adapter = PaymentInstallmentAdapter { installmentAmount ->
            ifLet(amount, paymentMethod) { (amountValue, paymentMethodValue) ->
                val paymentFlowRequestData = buildBasePaymentFlowRequestData(
                    amountValue,
                    description,
                    paymentMethodValue,
                ).apply {
                    installmentAmount.installment?.let {
                        setInstallmentsForCreditCard(it)
                    }
                }
                launchPaymentInstallment(paymentFlowRequestData)
            }
        }
        getRecyclerView(adapter)
    }

    private fun setView() {
        binding.apply {
            textAmount.text = amount.orEmpty()
            textAmountTotal.text = TOTAL_AMOUNT
        }
    }

    private fun getRecyclerView(adapter: PaymentInstallmentAdapter) {
        ifLet(amount, paymentMethod) { (amountValue, paymentMethodValue) ->
            MPManager.paymentInstallmentTools.getInstallmentsAmount({ mpResponse ->
                mpResponse.doIfSuccess { installments ->
                    adapter.submitList(installments)
                    setRecyclerView(adapter)
                }.doIfError {
                    val paymentFlowRequestData = buildBasePaymentFlowRequestData(
                        amount = amountValue,
                        description = description,
                        paymentMethodValue = paymentMethodValue
                    )
                    launchPaymentInstallment(paymentFlowRequestData)
                }
            }, amountValue)
        }
    }

    private fun setRecyclerView(adapter: PaymentInstallmentAdapter) {
        binding.rvListInstallments.apply {
            layoutManager = LinearLayoutManager(
                this@PaymentFlowInstallmentsActivity,
                RecyclerView.VERTICAL,
                false
            )
            this.adapter = adapter
        }
    }

    private fun buildBasePaymentFlowRequestData(
        amount: String,
        description: String? = null,
        paymentMethodValue: String? = null
    ) = PaymentFlowRequestData(
        amount = amount.toDouble(),
        description = description,
        paymentMethod = paymentMethodValue?.run { PaymentMethod.valueOf(this) },
        printOnTerminal = printOnTerminal,
        taxes = payerCondition?.toTaxes()
    )

    private fun launchPaymentInstallment(paymentFlowRequestData: PaymentFlowRequestData) {
        paymentFlow.launchPaymentFlow(paymentFlowRequestData) { response ->
            response.doIfError {
                setOnError(it.message)
            }
        }
    }

    private fun setOnError(message: String?) {
        binding.apply {
            textAmount.gone()
            textAmountTotal.gone()
            rvListInstallments.gone()
            textException.visible()
            imageErrorInstallment.visible()
            btnGoBack.visible()
            message.let { textException.text = it }
            btnGoBack.setOnClickListener { finish() }
        }
    }

    companion object {
        internal const val AMOUNT = "amount"
        internal const val DESCRIPTION = "description"
        internal const val PAYMENT_METHOD = "payment_method"
        internal const val TOTAL_AMOUNT = "Total Amount"
        internal const val PRINT_ON_TERMINAL = "print_on_terminal"
        internal const val PAYER_CONDITION = "payer_condition"
    }
}
