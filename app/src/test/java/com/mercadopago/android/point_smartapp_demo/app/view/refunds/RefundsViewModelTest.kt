package com.mercadopago.android.point_smartapp_demo.app.view.refunds

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import com.mercadolibre.android.point_integration_sdk.nativesdk.exception.GenericErrorCode
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundError
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundErrorCode
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundCallback
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundProgressStatus
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundRequest
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundResponse
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.RefundStatus
import com.mercadolibre.android.point_integration_sdk.nativesdk.refund.provider.Refunds
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RefundsViewModelTest {
    private val refunds = FakeRefunds()
    private val viewModel = RefundsViewModel().apply { sdkRefundsProvider = { refunds } }

    @Before
    fun setUp() {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
            override fun postToMainThread(runnable: Runnable) = runnable.run()
            override fun isMainThread() = true
        })
    }

    @After
    fun tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    @Test
    fun refundEmitsProgressAndSuccessAndRestoresAvailability() {
        val request = RefundRequest("123")
        val response = RefundResponse(refundId = "456")
        viewModel.performSdkRefund(request)
        assertTrue(viewModel.sdkLoading.value == true)
        refunds.callback.onProgress(RefundProgressStatus.PROCESSING)
        assertEquals(SdkRefundEvent.Progress(RefundProgressStatus.PROCESSING), viewModel.sdkResult.value)
        refunds.callback.onSuccess(response)
        assertFalse(viewModel.sdkLoading.value == true)
        assertEquals(SdkRefundEvent.Success(response), viewModel.sdkResult.value)
    }

    @Test
    fun unsupportedHostIsSurfacedAsError() {
        val error = RefundError(GenericErrorCode.NOT_SUPPORTED, "not supported")
        viewModel.performSdkRefund(RefundRequest("123"))
        refunds.callback.onError(error)
        assertFalse(viewModel.sdkLoading.value == true)
        assertSame(error, (viewModel.sdkResult.value as SdkRefundEvent.Error).error)
    }

    @Test
    fun timeoutIsSurfacedAsUnknownOutcome() {
        val error = RefundError(RefundErrorCode.RESULT_TIMEOUT, "outcome unknown")
        viewModel.performSdkRefund(RefundRequest("123"))

        refunds.callback.onError(error)

        assertFalse(viewModel.sdkLoading.value == true)
        assertEquals(RefundErrorCode.RESULT_TIMEOUT, (viewModel.sdkResult.value as SdkRefundEvent.Error).error.code)
    }

    @Test
    fun hostErrorPreservesGeneratedKey() {
        val error = RefundError(
            code = RefundErrorCode.RESULT_TIMEOUT,
            message = "outcome unknown",
            status = RefundStatus.ERROR,
            idempotencyKey = "host-generated-key",
            hostErrorCode = "RESULT_TIMEOUT",
        )
        viewModel.performSdkRefund(RefundRequest("123"))

        refunds.callback.onError(error)

        val result = (viewModel.sdkResult.value as SdkRefundEvent.Error).error
        assertEquals("host-generated-key", result.idempotencyKey)
        assertEquals(RefundStatus.ERROR, result.status)
        assertFalse(viewModel.sdkLoading.value == true)
    }

    @Test
    fun secondRefundIsIgnoredWhileFirstRequestIsProcessing() {
        val first = RefundRequest("123")
        viewModel.performSdkRefund(first)
        viewModel.performSdkRefund(RefundRequest("456"))
        assertEquals(listOf(first), refunds.requests)
    }

    private class FakeRefunds : Refunds {
        val requests = mutableListOf<RefundRequest>()
        lateinit var callback: RefundCallback
        override fun refund(request: RefundRequest, callback: RefundCallback) {
            requests += request
            this.callback = callback
        }
    }
}
