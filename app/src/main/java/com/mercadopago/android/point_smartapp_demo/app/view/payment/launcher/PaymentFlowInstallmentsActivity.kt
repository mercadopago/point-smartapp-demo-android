package com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointSmartappDemoAppActivityPaymentFlowInstallmetsBinding
import com.mercadopago.android.point_smartapp_demo.app.util.gone
import com.mercadopago.android.point_smartapp_demo.app.util.visible
import com.mercadopago.android.point_smartapp_demo.app.view.payment.adapter.PaymentInstallmentAdapter

class PaymentFlowInstallmentsActivity : AppCompatActivity() {

    private val binding: PointSmartappDemoAppActivityPaymentFlowInstallmetsBinding by lazy {
        PointSmartappDemoAppActivityPaymentFlowInstallmetsBinding.inflate(layoutInflater)
    }

    private val amount by lazy { intent.getStringExtra(AMOUNT) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setView()
        val adapter = PaymentInstallmentAdapter { installmentAmount ->
            installmentAmount.installment?.let { installments ->
                returnInstallmentsResult(installments)
            } ?: run {
                setResult(RESULT_CANCELED)
                finish()
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
        amount?.let { amountValue ->
            binding.progressBar.visible()
            MPManager.paymentInstallmentTools.getInstallmentsAmount({ mpResponse ->
                binding.progressBar.gone()
                mpResponse.doIfSuccess { installments ->
                    if (installments.isNotEmpty()) {
                        adapter.submitList(installments)
                        setRecyclerView(adapter)
                    } else {
                        showErrorAndReturnCancelled(ERROR_NO_INSTALLMENTS)
                    }
                }.doIfError { error ->
                    showErrorAndReturnCancelled(error.message ?: ERROR_NO_INSTALLMENTS)
                }
            }, amountValue)
        } ?: run {
            showErrorAndReturnCancelled(ERROR_INVALID_AMOUNT)
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

    private fun returnInstallmentsResult(installments: Int) {
        val resultIntent = Intent().apply {
            putExtra(EXTRA_INSTALLMENTS_RESULT, installments)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun showErrorAndReturnCancelled(errorMessage: String) {
        Snackbar.make(
            binding.root,
            errorMessage,
            Snackbar.LENGTH_LONG
        ).setBackgroundTint(getColor(R.color.design_default_color_error))
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    setResult(RESULT_CANCELED)
                    finish()
                }
            })
            .show()
    }

    companion object {
        internal const val AMOUNT = "amount"
        internal const val EXTRA_INSTALLMENTS_RESULT = "extra_installments_result"
        private const val TOTAL_AMOUNT = "Total Amount"
        private const val ERROR_NO_INSTALLMENTS = "No installments available"
        private const val ERROR_INVALID_AMOUNT = "Invalid amount"
    }
}
