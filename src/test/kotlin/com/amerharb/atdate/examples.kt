package com.amerharb.atdate

object Examples {
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
    val example4 = Period(
        sign = true,
        rangeLevel = RangeLevel.Level1,
        resolutionLevel = ResolutionLevel.Level0,
        leapSecondsFlag = 0U,
        date = 1U,
        time = null,
        plusLeapSeconds = null,
        minusLeapSeconds = null,
    )
}