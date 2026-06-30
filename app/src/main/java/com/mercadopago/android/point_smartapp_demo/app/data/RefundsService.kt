package com.mercadopago.android.point_smartapp_demo.app.data

import com.mercadopago.android.point_smartapp_demo.app.data.dto.RefundResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RefundsService {
    @POST(NetworkConstants.REFUNDS_PATH)
    suspend fun createRefund(
        @Path("payment_id") paymentId: String,
        @Header("Authorization") token: String
    ): Response<RefundResponse>
}
