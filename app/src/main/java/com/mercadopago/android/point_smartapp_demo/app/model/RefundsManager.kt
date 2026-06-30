package com.mercadopago.android.point_smartapp_demo.app.model

import com.mercadopago.android.point_smartapp_demo.app.data.NetworkDependencyProvider
import com.mercadopago.android.point_smartapp_demo.app.data.RefundsService
import com.mercadopago.android.point_smartapp_demo.app.data.dto.RefundResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class RefundsManager(private val refundsClient: RefundsService = NetworkDependencyProvider.refundsService) {
    suspend fun refundPayment(paymentId: Long, accessToken: String): Flow<Response<RefundResponse>> {
        return flow {
            emit(refundsClient.createRefund(paymentId.toString(), accessToken))
        }
    }
}
