package com.amerharb.atdate

object Examples {
	/**
	 * moment of 2019-05-05
	 * Not: @2019-05-05 { d:1 t:0 z:0 }@
	 * Hex: 0xC007E2
	 * Bin: 1100 0000 0000 0111 1110 0010
	 * Exp: IKDT TTZA <----- date ----->p
	 */
	val example1 = Moment(
		rangeLevel = RangeLevel.Level1,
		resolutionLevel = ResolutionLevel.Level0,
		zoneLevel = ZoneLevel.Level0,
		accuracy = Accuracy.Start,
		date = 0b11_11110001U,
		time = null,
		zone = null,
		plusLeapSeconds = null,
		minusLeapSeconds = null
	)

	/**
	 * moment of 2019-05-05 19:50:00+02:00
	 * Not: @2019-05-05T19:50:00+02:00 { d:1 t:0 z:0 }@
	 * Hex: 0xD607E3179C10
	 * Bin: 1101 0110 0000 0111 1110 0011 0001 0111 1001 1100 0001 0000
	 * Exp: IKDT TTZA <----- date -----><------- time ------> <-zone->p
	 */
	val example2 = Moment(
		rangeLevel = RangeLevel.Level1,
		resolutionLevel = ResolutionLevel.Level5,
		zoneLevel = ZoneLevel.Level1,
		accuracy = Accuracy.Start,
		date = 0b11_11110001U,
		time = 0b1_00010111_10011100U,
		zone = 0b1000U,
		plusLeapSeconds = null,
		minusLeapSeconds = null
	)

	/**
	 * plus 1 day period
	 * Not: @P+1D { d:1 t:0 l:0-0 }@
	 * Hex: 0x880002
	 * Bin: 1000 1000 0000 0000 0000 0010
	 * Exp: IKSD DTTT <----- date ----->p
	 */
	val example4 = Period(
		sign = true,
		rangeLevel = RangeLevel.Level1,
		resolutionLevel = ResolutionLevel.Level0,
		leapSecondsFlag = 0U,
		date = 1U,
		time = null,
		plusLeapSeconds = null,
		minusLeapSeconds = null
	)

	/**
	 * minus tiny period
	 * Not: @P-tp@
	 * Hex: 0xA0
	 * Bin: 1010 0000
	 * Exp: IKSD DTTT
	 */
	val example5 = Period(
		sign = false,
		rangeLevel = RangeLevel.Level0,
		resolutionLevel = ResolutionLevel.Level0,
		leapSecondsFlag = 0U,
		date = null,
		time = null,
		plusLeapSeconds = null,
		minusLeapSeconds = null
	)

	/**
	 * minus 1 day period
	 * Not: @P-1D { d:1 t:0 l:0-0 }@
	 * Hex: 0xA80002
	 * Bin: 1010 1000 0000 0000 0000 0010
	 * Exp: IKSD DTTT <----- date ----->p
	 */
	val example6 = Period(
		sign = false,
		rangeLevel = RangeLevel.Level1,
		resolutionLevel = ResolutionLevel.Level0,
		leapSecondsFlag = 0U,
		date = 1U,
		time = null,
		plusLeapSeconds = null,
		minusLeapSeconds = null
	)
}
