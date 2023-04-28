package com.amerharb.atdate

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TestPeriod {
    private val example4 = Period(
        sign = true,
        rangeLevel = RangeLevel.Level1,
        resolutionLevel = ResolutionLevel.Level0,
        date = 1U,
        time = null,
        plusLeapSeconds = null,
        minusLeapSeconds = null,
    )

    @Test
    @OptIn(ExperimentalUnsignedTypes::class)
    fun testGetPayloadExample4() {
        // 0x880002
        val expected = ubyteArrayOf(0x88U, 0x00U, 0x02U).toTypedArray()
        val actual = example4.getPayload()
        assertContentEquals(expected, actual)
    }

    @Test
    fun testGetNotationExample4() {
        val expected = "@P+1D { d:1 t:0 l:0-0 }@"
        val actual = example4.getNotation()
        assertEquals(expected, actual)
    }
}