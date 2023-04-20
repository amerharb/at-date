package com.amerharb.atdate

import kotlin.test.Test
import kotlin.test.assertEquals

// test encode function
class TestEncode {
    @Test
    fun example1() {
        val input = "@2019-05-05 {d:1}@"
        val actual = encode(input)
        val expected = Moment(
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
        assertEquals(expected, actual)
    }

    @Test
    fun example2() {
        val input = "@2019-05-05T19:53:00+02:00 {d:1 t:5 a:s l:0-0 z:1}@"
        val actual = encode(input)
        val expected = Moment(
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
        assertEquals(expected, actual)
    }

    @Test
    fun example2UTC() {
        val input = "@2019-05-05T19:53:00Z {d:1 t:5 a:s l:0-0}@"
        val actual = encode(input)
        val expected = Moment(
            rangeLevel = RangeLevel.Level1,
            resolutionLevel = ResolutionLevel.Level5,
            zoneLevel = ZoneLevel.Level1,
            accuracy = Accuracy.Start,
            date = 0b11_11110001U,
            time = 0b1_00010111_10011100U,
            zone = 0U,
            plusLeapSeconds = null,
            minusLeapSeconds = null,
        )
        println(actual)
        println(expected)
        assertEquals(expected, actual)
    }

    @Test
    fun example2LocalTime() {
        val input = "@2019-05-05T19:53:00 {d:1 t:5 a:s l:0-0}@"
        val actual = encode(input)
        val expected = Moment(
            rangeLevel = RangeLevel.Level1,
            resolutionLevel = ResolutionLevel.Level5,
            zoneLevel = ZoneLevel.Level0,
            accuracy = Accuracy.Start,
            date = 0b11_11110001U,
            time = 0b1_00010111_10011100U,
            zone = null,
            plusLeapSeconds = null,
            minusLeapSeconds = null,
        )
        println(actual)
        println(expected)
        assertEquals(expected, actual)
    }
}

/**
 * test cases tested from https://aa.usno.navy.mil/data/JulianDate
 */
class TestGetJdn {
    @Test
    fun test20190505() {
        val input = "2019-05-05"
        val (year, month, day) = input.split("-").map { it.toLong() }
        val actual = getJdn(year, month, day)
        val expected = 0b00100101_10000011_11110001L // 2458609
        println(actual)
        println(expected)
        assertEquals(expected, actual)
    }

    @Test
    fun testDay0() {
        val actual = getJdn(-4713, 11, 24) // year: 4714BC month: 11 day: 24
        val expected = 0L
        println(actual)
        println(expected)
        assertEquals(expected, actual)
    }
}