package com.mercadopago.android.point_smartapp_demo.app.view.refunds

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SdkRefundInputTest {
    @Test
    fun `empty optional fields create a total refund request`() {
        val request = (parseSdkRefundInput("123", "", "") as SdkRefundInput.Valid).request
        assertEquals("123", request.paymentId)
        assertNull(request.amount)
        assertNull(request.idempotencyKey)
    }

    @Test
    fun `amount and key create a partial refund request`() {
        val request = (parseSdkRefundInput("123", "10.50", "key-1") as SdkRefundInput.Valid).request
        assertEquals(BigDecimal("10.50"), request.amount)
        assertEquals("key-1", request.idempotencyKey)
    }

    @Test
    fun `invalid payment ids are rejected`() {
        listOf("", "0", "abc", " 123", "-1").forEach {
            assertTrue(parseSdkRefundInput(it, "", "") is SdkRefundInput.InvalidPaymentId)
        }
    }

    @Test
    fun `invalid amounts are rejected`() {
        listOf("0", "-1", "abc", " ").forEach {
            assertTrue(parseSdkRefundInput("123", it, "") is SdkRefundInput.InvalidAmount)
        }
    }

    @Test
    fun `invalid idempotency keys are rejected`() {
        listOf(" ", "x".repeat(256)).forEach {
            assertTrue(parseSdkRefundInput("123", "", it) is SdkRefundInput.InvalidIdempotencyKey)
        }
    }
}
