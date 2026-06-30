package com.mercadopago.android.point_smartapp_demo.app.data

import com.mercadopago.android.point_smartapp_demo.app.data.NetworkConstants.CONNECTION_TIMEOUT
import com.mercadopago.android.point_smartapp_demo.app.data.NetworkConstants.READ_TIMEOUT
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkDependencyProvider {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
    }

    private val converterFactory: GsonConverterFactory by lazy {
        GsonConverterFactory.create()
    }

    private val mpApiClient: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    val refundsService: RefundsService =
        mpApiClient.create(RefundsService::class.java)
}
