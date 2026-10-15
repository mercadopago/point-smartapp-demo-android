package com.mercadopago.android.point_smartapp_demo.app.view.payment.launcher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.InstallmentAmount
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedback
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoFeedbackBanner
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoProgress
import com.mercadopago.android.point_smartapp_demo.app.view.ui.DemoScreen
import com.mercadopago.android.point_smartapp_demo.app.view.ui.setDemoContent

class PaymentFlowInstallmentsActivity : AppCompatActivity() {

    private val amount by lazy { intent.getStringExtra(AMOUNT) }
    private var installments by mutableStateOf<List<InstallmentAmount>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var feedback by mutableStateOf<DemoFeedback?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screenTitle = "Installments"
        val totalAmountLabel = TOTAL_AMOUNT
        val progressLabel = "Loading installments"

        setDemoContent {
            DemoScreen(title = screenTitle) {
                Text(text = amount.orEmpty())
                Text(text = totalAmountLabel)
                if (isLoading) DemoProgress(label = progressLabel)
                installments.forEach { installment ->
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            installment.installment?.let(::returnInstallmentsResult) ?: run {
                                setResult(RESULT_CANCELED)
                                finish()
                            }
                        },
                    ) {
                        Text(
                            text = "${installment.installment}X ${installment.amount}",
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
                DemoFeedbackBanner(feedback) { cancelAfterError() }
            }
        }
        getInstallments()
    }

    private fun getInstallments() {
        amount?.let { amountValue ->
            isLoading = true
            MPManager.paymentInstallmentTools.getInstallmentsAmount({ response ->
                isLoading = false
                response.doIfSuccess { result ->
                    if (result.isNotEmpty()) {
                        installments = result
                    } else {
                        showErrorAndReturnCancelled(ERROR_NO_INSTALLMENTS)
                    }
                }.doIfError { error ->
                    showErrorAndReturnCancelled(error.message ?: ERROR_NO_INSTALLMENTS)
                }
            }, amountValue)
        } ?: showErrorAndReturnCancelled(ERROR_INVALID_AMOUNT)
    }

    private fun returnInstallmentsResult(installments: Int) {
        val resultIntent = Intent().apply {
            putExtra(EXTRA_INSTALLMENTS_RESULT, installments)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun showErrorAndReturnCancelled(errorMessage: String) {
        feedback = DemoFeedback(errorMessage, isError = true)
    }

    private fun cancelAfterError() {
        setResult(RESULT_CANCELED)
        finish()
    }

    companion object {
        internal const val AMOUNT = "amount"
        internal const val EXTRA_INSTALLMENTS_RESULT = "extra_installments_result"
        private const val TOTAL_AMOUNT = "Total Amount"
        private const val ERROR_NO_INSTALLMENTS = "No installments available"
        private const val ERROR_INVALID_AMOUNT = "Invalid amount"
    }
}
