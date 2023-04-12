package com.amerharb.atdate

import kotlin.test.Test

// test encode function
class TestEncode {
    @Test
    fun example2() {
        val input = "@2019-05-05T19:53:00+02:00 {d:1, t:5, a:s, l:0-0, z:1}@"
        val actual = encode(input)
        val expected = AtDate(
            rangeLevel = RangeLevel.Level1,
            resolutionLevel = ResolutionLevel.Level5,
            zoneLevel = ZoneLevel.Level1,
            accuracy = Accuracy.Start,
            date = 0B11_11110000U,
            time = 0B1_00010111_10011100U,
            zone = 0B1000U,
            plusLeapSeconds = null,
            minusLeapSeconds = null,
        )
        println(actual)
        println(expected)
        assert(actual == expected)
    }

    @Test
    fun example2UTC() {
        val input = "@2019-05-05T19:53:00Z {d:1, t:5, a:s, l:0-0}@"
        val actual = encode(input)
        val expected = AtDate(
            rangeLevel = RangeLevel.Level1,
            resolutionLevel = ResolutionLevel.Level5,
            zoneLevel = ZoneLevel.Level1,
            accuracy = Accuracy.Start,
            date = 0B11_11110000U,
            time = 0B1_00010111_10011100U,
            zone = 0U,
            plusLeapSeconds = null,
            minusLeapSeconds = null,
        )
        println(actual)
        println(expected)
        assert(actual == expected)
    }

    @Test
    fun example2LocalTime() {
        val input = "@2019-05-05T19:53:00 {d:1, t:5, a:s, l:0-0}@"
        val actual = encode(input)
        val expected = AtDate(
            rangeLevel = RangeLevel.Level1,
            resolutionLevel = ResolutionLevel.Level5,
            zoneLevel = ZoneLevel.Level0,
            accuracy = Accuracy.Start,
            date = 0B11_11110000U,
            time = 0B1_00010111_10011100U,
            zone = null,
            plusLeapSeconds = null,
            minusLeapSeconds = null,
        )
        println(actual)
        println(expected)
        assert(actual == expected)
    }
}