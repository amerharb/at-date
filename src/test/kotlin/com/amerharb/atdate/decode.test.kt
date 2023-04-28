package com.amerharb.atdate

import kotlin.test.Test
import kotlin.test.assertEquals

// test decode Moment function
class TestDecodeMoment {
    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun example1() {
        // "@2019-05-05 {d:1}@"
        val input = ubyteArrayOf(0xC0U, 0x07U, 0xE2U).toTypedArray()
        val actual = decodeMoment(input)
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
        assertEquals(expected, actual)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun example2() {
        // "@2019-05-05T19:53:00+02:00 {d:1, t:5, a:s, l:0-0, z:1}@"
        val input = ubyteArrayOf(0xD6U, 0x07U, 0xE3U, 0x17U, 0x9CU, 0x10U).toTypedArray()
        val actual = decodeMoment(input)
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
        assertEquals(expected, actual)
    }
}

// test decode Period function
class TestDecodePeriod {
    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun example4() {
        // "@P1D@"
        val input = ubyteArrayOf(0b1000_1000U, 0U, 2U).toTypedArray()
        val actual = decodePeriod(input)
        val expected = Period(
            sign = true,
            rangeLevel = RangeLevel.Level1,
            resolutionLevel = ResolutionLevel.Level0,
            leapSecondsFlag = 0U,
            date = 1U,
            time = null,
            plusLeapSeconds = null,
            minusLeapSeconds = null,
        )
        assertEquals(expected, actual)
    }
}

class TestGetDateFromJdn {
    @Test
    fun test20190505() {
        val actual = getDateFromJdn(2458609L)
        val expected = BasicISODate(2019, 5, 5)
        assertEquals(expected, actual)
    }

    @Test
    fun test20200606() {
        val actual = getDateFromJdn(2459007L)
        val expected = BasicISODate(2020, 6, 6)
        assertEquals(expected, actual)
    }

    @Test
    fun testDay0() {
        val actual = getDateFromJdn(0)
        val expected = BasicISODate(-4713, 11, 24)
        assertEquals(expected, actual)
    }
}