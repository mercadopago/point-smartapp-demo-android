package com.mercadopago.android.point_smartapp_demo.app.view.payment.result

import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.doAfterTextChanged
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.doIf
import com.mercadopago.android.point_smartapp_demo.app.R
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointSmartappDemoAppActivityPaymentStatusBinding
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointSmartappDemoAppItemPaymentStatusFieldBinding
import com.mercadopago.android.point_smartapp_demo.app.databinding.PointSmartappDemoAppItemPaymentStatusSectionBinding
import com.mercadopago.android.point_smartapp_demo.app.util.gone
import com.mercadopago.android.point_smartapp_demo.app.util.hideKeyboard
import com.mercadopago.android.point_smartapp_demo.app.util.visible

class PaymentStatusActivity : AppCompatActivity() {

    private val binding: PointSmartappDemoAppActivityPaymentStatusBinding by lazy {
        PointSmartappDemoAppActivityPaymentStatusBinding.inflate(layoutInflater)
    }

    private val paymentStatus = MPManager.paymentStatus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        ViewCompat.setAccessibilityHeading(binding.textPaymentStatusTitle, true)
        ViewCompat.setAccessibilityHeading(binding.textPaymentStatusResultTitle, true)
        bindQueryType()
        bindReferenceInput()
        bindActions()
    }

    private fun bindQueryType() = with(binding) {
        paymentStatusQueryType.addOnButtonCheckedListener { _, _, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            clearResult()
        }
        paymentStatusQueryType.check(buttonLocalPaymentStatus.id)
    }

    private fun bindReferenceInput() = with(binding) {
        searchInputPaymentStatusEditText.doAfterTextChanged {
            searchInputPaymentStatus.error = null
            buttonSearchPaymentStatus.isEnabled = !it.isNullOrBlank() && !paymentStatusProgress.isShown
        }
        searchInputPaymentStatusEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                queryPaymentStatus()
                true
            } else {
                false
            }
        }
    }

    private fun bindActions() = with(binding) {
        buttonSearchPaymentStatus.setOnClickListener { queryPaymentStatus() }
        buttonBackToHome.setOnClickListener { finish() }
    }

    private fun queryPaymentStatus() {
        val reference = binding.searchInputPaymentStatusEditText.text?.toString().orEmpty()
        if (reference.isBlank()) {
            binding.searchInputPaymentStatus.error =
                getString(R.string.point_smartapp_demo_app_payment_status_required)
            return
        }

        hideKeyboard()
        setLoading(true)
        if (binding.buttonOnlinePaymentStatus.isChecked) {
            queryOnlinePayment(reference)
        } else {
            queryLocalPayment(reference)
        }
    }

    private fun queryLocalPayment(reference: String) {
        paymentStatus.getPaymentStatus(reference) { response ->
            response.doIf(
                successCallback = { result ->
                    showOnUiThread {
                        renderResult(
                            result.toPaymentStatusResult(
                                getString(R.string.point_smartapp_demo_app_payment_status_local_found),
                            ),
                        )
                    }
                },
                errorCallback = { error -> showOnUiThread { renderError(error.message) } },
            )
        }
    }

    private fun queryOnlinePayment(externalReference: String) {
        paymentStatus.getOnlinePaymentStatus(externalReference) { response ->
            response.doIf(
                successCallback = { result ->
                    showOnUiThread { renderResult(result.toPaymentStatusResult()) }
                },
                errorCallback = { error -> showOnUiThread { renderError(error.message) } },
            )
        }
    }

    private fun renderResult(result: PaymentStatusResult) = with(binding) {
        setLoading(false)
        textPaymentStatusError.gone()
        textPaymentStatusResultTitle.text = result.title
        textPaymentStatusResultSubtitle.setText(result.subtitle)
        paymentStatusResultSections.removeAllViews()
        result.sections.forEach(::addSection)
        cardPaymentStatus.visible()
        paymentStatusScroll.post { paymentStatusScroll.smoothScrollTo(0, cardPaymentStatus.top) }
    }

    private fun addSection(section: PaymentStatusSection) {
        val sectionBinding = PointSmartappDemoAppItemPaymentStatusSectionBinding.inflate(
            layoutInflater,
            binding.paymentStatusResultSections,
            false,
        )
        sectionBinding.textPaymentStatusSectionTitle.setText(section.title)
        ViewCompat.setAccessibilityHeading(sectionBinding.textPaymentStatusSectionTitle, true)
        section.fields.forEach { field ->
            val fieldBinding = PointSmartappDemoAppItemPaymentStatusFieldBinding.inflate(
                layoutInflater,
                sectionBinding.paymentStatusSectionFields,
                false,
            )
            fieldBinding.textPaymentStatusFieldLabel.setText(field.label)
            fieldBinding.textPaymentStatusFieldValue.text = field.value.displayValue()
            sectionBinding.paymentStatusSectionFields.addView(fieldBinding.root)
        }
        binding.paymentStatusResultSections.addView(sectionBinding.root)
    }

    private fun renderError(rawCode: String?) = with(binding) {
        setLoading(false)
        cardPaymentStatus.gone()
        val code = rawCode?.takeIf(KNOWN_ERROR_CODES::contains)
        textPaymentStatusError.text = when (code) {
            ERROR_LOCAL_PAYMENT_NOT_FOUND, ERROR_PAYMENT_NOT_FOUND ->
                getString(R.string.point_smartapp_demo_app_payment_status_not_found_warning)
            null -> getString(R.string.point_smartapp_demo_app_payment_status_error)
            else -> getString(R.string.point_smartapp_demo_app_payment_status_error_code, code)
        }
        textPaymentStatusError.visible()
    }

    private fun setLoading(isLoading: Boolean) = with(binding) {
        paymentStatusProgress.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        paymentStatusQueryType.isEnabled = !isLoading
        buttonLocalPaymentStatus.isEnabled = !isLoading
        buttonOnlinePaymentStatus.isEnabled = !isLoading
        searchInputPaymentStatus.isEnabled = !isLoading
        buttonSearchPaymentStatus.isEnabled = !isLoading &&
            !searchInputPaymentStatusEditText.text.isNullOrBlank()
        if (isLoading) {
            cardPaymentStatus.gone()
            textPaymentStatusError.gone()
        }
    }

    private fun clearResult() = with(binding) {
        cardPaymentStatus.gone()
        textPaymentStatusError.gone()
        paymentStatusResultSections.removeAllViews()
    }

    private fun showOnUiThread(action: () -> Unit) {
        runOnUiThread {
            if (!isFinishing && !isDestroyed) action()
        }
    }

    private fun String?.displayValue(): String =
        this?.takeIf(String::isNotBlank)
            ?: getString(R.string.point_smartapp_demo_app_payment_status_not_available)

    private companion object {
        const val ERROR_INVALID_EXTERNAL_REFERENCE = "invalid_external_reference"
        const val ERROR_LOCAL_PAYMENT_NOT_FOUND = "local_payment_not_found"
        const val ERROR_INVALID_LOCAL_PAYMENT_ID = "invalid_local_payment_id"
        const val ERROR_UNAUTHORIZED = "unauthorized"
        const val ERROR_PAYMENT_NOT_FOUND = "payment_not_found"
        const val ERROR_NO_INTERNET = "no_internet"
        const val ERROR_UNSUPPORTED_PAYMENT_STATUS = "unsupported_payment_status"
        const val ERROR_REQUEST_IN_PROGRESS = "request_in_progress"
        const val ERROR_UNEXPECTED = "unexpected_error"
        val KNOWN_ERROR_CODES = setOf(
            ERROR_INVALID_EXTERNAL_REFERENCE,
            ERROR_LOCAL_PAYMENT_NOT_FOUND,
            ERROR_INVALID_LOCAL_PAYMENT_ID,
            ERROR_UNAUTHORIZED,
            ERROR_PAYMENT_NOT_FOUND,
            ERROR_NO_INTERNET,
            ERROR_UNSUPPORTED_PAYMENT_STATUS,
            ERROR_REQUEST_IN_PROGRESS,
            ERROR_UNEXPECTED,
        )
    }
}
