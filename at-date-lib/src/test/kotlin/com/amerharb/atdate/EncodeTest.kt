package com.amerharb.atdate

import kotlin.test.Test
import kotlin.test.assertEquals

// test encodeMoment function
class EncodeMomentTest {
	@Test
	fun example1() {
		val input = "@2019-05-05 {d:1}@"
		val actual = encodeMoment(input)
		val expected = Examples.example1
		assertEquals(expected, actual)
	}

	@Test
	fun example2() {
		val input = "@2019-05-05T19:53:00+02:00 {d:1 t:5 a:s l:0-0 z:1}@"
		val actual = encodeMoment(input)
		val expected = Examples.example2
		assertEquals(expected, actual)
	}

	@Test
	fun example2UTC() {
		val input = "@2019-05-05T19:53:00Z {d:1 t:5 a:s l:0-0}@"
		val actual = encodeMoment(input)
		val expected = Moment(
			rangeLevel = RangeLevel.Level1,
			resolutionLevel = ResolutionLevel.Level5,
			zoneLevel = ZoneLevel.Level1,
			accuracy = Accuracy.Start,
			date = 0b11_11110001U,
			time = 0b1_00010111_10011100U,
			zone = 0U,
			plusLeapSeconds = null,
			minusLeapSeconds = null
		)
		assertEquals(expected, actual)
	}

	@Test
	fun example2LocalTime() {
		val input = "@2019-05-05T19:53:00 {d:1 t:5 a:s l:0-0}@"
		val actual = encodeMoment(input)
		val expected = Moment(
			rangeLevel = RangeLevel.Level1,
			resolutionLevel = ResolutionLevel.Level5,
			zoneLevel = ZoneLevel.Level0,
			accuracy = Accuracy.Start,
			date = 0b11_11110001U,
			time = 0b1_00010111_10011100U,
			zone = null,
			plusLeapSeconds = null,
			minusLeapSeconds = null
		)
		assertEquals(expected, actual)
	}

	@Test
	fun findResolutionLevel8() {
		val input = "@1979-11-14T00:00:00.123456789+02:00@"
		val expected = Moment(
			rangeLevel = RangeLevel.Level2,
			resolutionLevel = ResolutionLevel.Level8,
			zoneLevel = ZoneLevel.Level1,
			accuracy = Accuracy.Start,
			date = 2444192UL,
			time = 123456789UL,
			zone = 0b1000U,
			plusLeapSeconds = null,
			minusLeapSeconds = null
		)
		val actual = encodeMoment(input)
		assertEquals(expected, actual)
	}
}

// test encodePeriod function
class EncodePeriodTest {
	@Test
	fun example4() {
		val input = "@P1D@"
		val actual = encodePeriod(input)
		val expected = Examples.example4
		assertEquals(expected, actual)
	}

// example tiny period
	@Test
	fun example5() {
		val input = "@P-tp@"
		val actual = encodePeriod(input)
		val expected = Examples.example5
		assertEquals(expected, actual)
	}

	@Test
	fun case1() {
		val input = "@P-1DT00:00:02@"
		val actual = encodePeriod(input)
		val expected = Period(
			sign = false,
			rangeLevel = RangeLevel.Level1,
			resolutionLevel = ResolutionLevel.Level5,
			leapSecondsFlag = 0U,
			date = 1U,
			time = 2U,
			plusLeapSeconds = null,
			minusLeapSeconds = null
		)
		assertEquals(expected, actual)
	}

	@Test
	fun case2() {
		val input = "@P-1DT00:00:02@"
		val actual = encodePeriod(input)
		val expected = Period(
			sign = false,
			rangeLevel = RangeLevel.Level1,
			resolutionLevel = ResolutionLevel.Level5,
			leapSecondsFlag = 0U,
			date = 1U,
			time = 2U,
			plusLeapSeconds = null,
			minusLeapSeconds = null
		)
		assertEquals(expected, actual)
	}
}

/**
 * test cases tested from https://aa.usno.navy.mil/data/JulianDate
 */
class GetJdnTest {
	@Test
	fun test20190505() {
		val input = "2019-05-05"
		val (year, month, day) = input.split("-").map { it.toLong() }
		val actual = getJdn(year, month, day)
		val expected = 0b00100101_10000011_11110001L // 2458609
		assertEquals(expected, actual)
	}

	@Test
	fun testDay0() {
		val actual = getJdn(-4713, 11, 24) // year: 4714BC month: 11 day: 24
		val expected = 0L
		assertEquals(expected, actual)
	}
}
