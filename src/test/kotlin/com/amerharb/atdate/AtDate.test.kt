package com.amerharb.atdate

import org.junit.jupiter.api.Test

class TestAtDate {
    val datetimezone = AtDate(
        rangeLevel = RangeLevel.Level1,
        resolutionLevel = ResolutionLevel.Level5,
        zoneLevel = ZoneLevel.Level1,
        accuracy = Accuracy.Start,
        date = 0B11_11110001U,
        time = 0B1_00010111_10011100U,
        zone = 0B1000U,
        plusLeapSeconds = null,
        minusLeapSeconds = null,
    )

    @Test
    @OptIn(ExperimentalUnsignedTypes::class)
    fun testGetPayloadExample2() {
        val expected = ubyteArrayOf(0xD6U, 0x07U, 0xE3U, 0x17U, 0x9CU, 0x10U).toTypedArray()
        val actual = datetimezone.getPayload()
        println(expected.joinToString { it.toString(16) })
        println(actual.joinToString { it.toString(16) })
        assert(expected.contentEquals(actual))
    }
}