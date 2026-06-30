package com.mercadopago.android.point_smartapp_demo.app.view.payment.result

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.mercadopago.android.point_integration_sdk.nativesdk.MPManager
import com.mercadopago.android.point_integration_sdk.nativesdk.message.utils.doIfError
import com.mercadopago.android.point_integration_sdk.nativesdk.message.utils.doIfSuccess
import com.mercadopago.android.point_integration_sdk.nativesdk.payment.data.PaymentResponse
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointMainappDemoAppActivityPaymentStatusApprovedBinding
import com.mercadopago.android.point_smartapp_demo.app.util.visible

class PaymentStatusApprovedActivity : AppCompatActivity() {

    private val binding: PointMainappDemoAppActivityPaymentStatusApprovedBinding by lazy {
        PointMainappDemoAppActivityPaymentStatusApprovedBinding.inflate(layoutInflater)
    }

    private val paymentStatus = MPManager.paymentStatus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        searchPayment()
        goBack()
    }

    private fun searchPayment() {
        binding.apply {
            searchInputPaymentStatusApproved.setEndIconOnClickListener {
                val paymentId = searchInputPaymentStatusApproved.editText?.text.toString()
                getPaymentStatus(paymentId)
            }
        }
    }

    private fun getPaymentStatus(paymentId: String) {
        paymentStatus.getPaymentStatus(paymentId) { response ->
            response.doIfSuccess {
                paymentStatusSuccessResult(it)
            }
            response.doIfError {
                paymentStatusErrorResult(it.message)
            }
        }
    }

    private fun paymentStatusSuccessResult(paymentResponse: PaymentResponse) {
        binding.apply {
            cardPaymentStatusApproved.visible()
            textPaymentAmountResult.text = paymentResponse.paymentAmount.toString()
            textPaymentInstallmentResult.text = paymentResponse.paymentInstallments
            textPaymentStatusPaymentMethodResult.text = paymentResponse.paymentMethod.toString()
            textPaymentStatusPaymentReferenceResult.text = paymentResponse.paymentReference
            textPaymentStatusBrandNameResult.text = paymentResponse.paymentBrandName
            textPaymentStatusCreationDateResult.text = paymentResponse.paymentCreationDate
            textPaymentStatusLastFourDigitsResult.text = paymentResponse.paymentLastFourDigits
            textPaymentStatusTipAmount.text = paymentResponse.tipAmount
        }
    }

    private fun paymentStatusErrorResult(message: String?) {
        Snackbar.make(binding.root, message ?: "Error", Snackbar.ANIMATION_MODE_SLIDE)
            .setBackgroundTint(getColor(android.R.color.holo_red_light)).show()
    }

    private fun goBack() {
        binding.apply {
            buttonPaymentStatusApproved.setOnClickListener {
                finish()
            }
        }
    }
}
