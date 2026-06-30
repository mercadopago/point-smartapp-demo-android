package com.mercadopago.android.point_smartapp_demo.app.view.refunds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadopago.android.point_smartapp_demo.app.model.RefundsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RefundsViewModel : ViewModel() {

    private val _successResult = MutableLiveData<String>()
    val successResult: LiveData<String> get() = _successResult

    private val _errorResult = MutableLiveData<String>()
    val errorResult: LiveData<String> get() = _errorResult

    fun performRefund(paymentId: Long, accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            RefundsManager().refundPayment(paymentId, accessToken.appendBearer())
                .catch { _errorResult.postValue(it.message.toString()) }
                .collect {
                    if (it.body()?.paymentId != null) {
                        _successResult.postValue(it.body()?.paymentId?.toString())
                    } else {
                        _errorResult.postValue(it.errorBody()?.string() ?: "Unknown Error")
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
