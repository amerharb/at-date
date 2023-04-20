package com.amerharb.atdate

import kotlin.test.Test

class TestMoment {
    @Test
    @OptIn(ExperimentalUnsignedTypes::class)
    fun testGetPayloadExample1() {
        val example1 = Moment(
            rangeLevel = RangeLevel.Level1,
            resolutionLevel = ResolutionLevel.Level0,
            zoneLevel = ZoneLevel.Level0,
            accuracy = Accuracy.Start,
            date = 0b11_11110001U,
            time = null,
            zone = null,
            plusLeapSeconds = null,
            minusLeapSeconds = null,
        )
        // 0xC007E2
        val expected = ubyteArrayOf(0xC0U, 0x07U, 0xE2U).toTypedArray()
        val actual = example1.getPayload()
        println(expected.joinToString { it.toString(16) })
        println(actual.joinToString { it.toString(16) })
        assert(expected.contentEquals(actual))
    }

    @Test
    @OptIn(ExperimentalUnsignedTypes::class)
    fun testGetPayloadExample2() {
        val example2 = Moment(
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
        // 0xD6 07 E3 17 9C 10
        val expected = ubyteArrayOf(0xD6U, 0x07U, 0xE3U, 0x17U, 0x9CU, 0x10U).toTypedArray()
        val actual = example2.getPayload()
        println(expected.joinToString { it.toString(16) })
        println(actual.joinToString { it.toString(16) })
        assert(expected.contentEquals(actual))
    }

    @Test
    @OptIn(ExperimentalUnsignedTypes::class)
    fun testGetNotationExample1() {
        val example1 = Moment(
            rangeLevel = RangeLevel.Level1,
            resolutionLevel = ResolutionLevel.Level0,
            zoneLevel = ZoneLevel.Level0,
            accuracy = Accuracy.Start,
            date = 0b11_11110001U,
            time = null,
            zone = null,
            plusLeapSeconds = null,
            minusLeapSeconds = null,
        )
        val expected = "@2019-05-05 { d:1 t:0 z:0 a:s l:0-0 }@"
        val actual = example1.getNotation()
        println(expected)
        println(actual)
        assert(expected == actual)
    }

    @Test
    @OptIn(ExperimentalUnsignedTypes::class)
    fun testGetNotationExample2() {
        val example2 = Moment(
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
        val expected = "@2019-05-05T19:53:00+02:00 { d:1 t:5 z:1 a:s l:0-0 }@"
        val actual = example2.getNotation()
        println(expected)
        println(actual)
        assert(expected == actual)
    }
}