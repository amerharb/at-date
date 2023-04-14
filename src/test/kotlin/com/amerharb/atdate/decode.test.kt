package com.amerharb.atdate

import kotlin.test.Test

// test encode function
class TestDecode {
    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun example1() {
        // "@2019-05-05 {d:1}@"
        val input = ubyteArrayOf(0xC0U, 0x07U, 0xE2U).toTypedArray()
        val actual = decode(input)
        val expected = AtDate(
            rangeLevel = RangeLevel.Level1,
            resolutionLevel = ResolutionLevel.Level0,
            zoneLevel = ZoneLevel.Level0,
            accuracy = Accuracy.Start,
            leapSecondsFlag = 0U,
            date = 0b11_11110001U,
            time = null,
            zone = null,
            plusLeapSeconds = null,
            minusLeapSeconds = null,
        )
        println(actual)
        println(expected)
        assert(actual == expected)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun example2() {
        // "@2019-05-05T19:53:00+02:00 {d:1, t:5, a:s, l:0-0, z:1}@"
        val input =  ubyteArrayOf(0xD6U, 0x07U, 0xE3U, 0x17U, 0x9CU, 0x10U).toTypedArray()
        val actual = decode(input)
        val expected = AtDate(
            rangeLevel = RangeLevel.Level1,
            resolutionLevel = ResolutionLevel.Level5,
            zoneLevel = ZoneLevel.Level1,
            accuracy = Accuracy.Start,
            date = 0b11_11110001U,
            time = 0b1_00010111_10011100U,
            zone = 0b1000U,
            plusLeapSeconds = null,
            minusLeapSeconds = null,
        )
        println(actual)
        println(expected)
        assert(actual == expected)
    }
}
