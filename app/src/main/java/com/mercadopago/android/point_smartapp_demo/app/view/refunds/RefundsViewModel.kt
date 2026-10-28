package com.mercadopago.android.point_smartapp_demo.app.view.refunds

import androidx.lifecycle.LiveData
import com.mercadolibre.android.point_integration_sdk.nativesdk.MPManager
import com.mercadolibre.android.point_integration_sdk.nativesdk.message.utils.MPResponse
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundRequest
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundResponse
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.Refunds
import java.lang.ref.WeakReference
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadopago.android.point_smartapp_demo.app.model.RefundsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RefundsViewModel : ViewModel() {

    internal var sdkRefundsProvider: () -> Refunds = { MPManager.refunds }

    private val _sdkLoading = MutableLiveData(false)
    val sdkLoading: LiveData<Boolean> get() = _sdkLoading

    private val _sdkResult = MutableLiveData<MPResponse<RefundResponse>?>()
    val sdkResult: LiveData<MPResponse<RefundResponse>?> get() = _sdkResult

    fun performSdkRefund(request: RefundRequest) {
        if (_sdkLoading.value == true) return
        _sdkResult.value = null
        _sdkLoading.value = true
        val owner = WeakReference(this)
        sdkRefundsProvider().refund(request) { result ->
            owner.get()?.let { viewModel ->
                viewModel._sdkLoading.postValue(false)
                viewModel._sdkResult.postValue(result)
            }
        }
    }

    private val _successResult = MutableLiveData<String>()
    val successResult: LiveData<String> get() = _successResult

    private val _errorResult = MutableLiveData<Int>()
    val errorResult: LiveData<Int> get() = _errorResult

    fun performRefund(paymentId: Long, accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            RefundsManager().refundPayment(paymentId, accessToken.appendBearer())
                .catch { _errorResult.postValue(0) }
                .collect {
                    if (it.isSuccessful && it.body()?.paymentId != null) {
                        _successResult.postValue(it.body()?.paymentId?.toString())
                    } else {
                        _errorResult.postValue(if (it.isSuccessful) 0 else it.code())
                    }
                }
        }
    }

    private fun String.appendBearer(): String {
        if (this.contains(BEARER_SUFFIX).not()) {
            return BEARER_SUFFIX + this
        }
        return this
    }

    companion object {
        const val BEARER_SUFFIX = "Bearer "
    }
}
