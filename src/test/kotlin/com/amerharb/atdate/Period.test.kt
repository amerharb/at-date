package com.amerharb.atdate

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TestPeriod {
    @Test
    @OptIn(ExperimentalUnsignedTypes::class)
    fun testGetPayloadExample4() {
        // 0x880002
        val expected = ubyteArrayOf(0x88U, 0x00U, 0x02U).toTypedArray()
        val actual = Examples.example4.getPayload()
        assertContentEquals(expected, actual)
    }

    @Test
    fun testGetNotationExample4() {
        val expected = "@P+1D { d:1 t:0 l:0-0 }@"
        val actual = Examples.example4.getNotation()
        assertEquals(expected, actual)
    }
}