package com.amerharb.atdate

data class AtDate(
    val rangeLevel: RangeLevel = RangeLevel.Level1,
    val resolutionLevel: ResolutionLevel = ResolutionLevel.Level0,
    val zoneLevel: ZoneLevel = ZoneLevel.Level0,
    val accuracy: Accuracy = Accuracy.Start,
    val leapSecondsFlag: UByte = 0U,
    val date: ULong = 0U,
    val time: ULong? = null,
    val zone: ULong? = null,
    val plusLeapSeconds: ULong? = null,
    val minusLeapSeconds: ULong? = null,
)

enum class Accuracy {
    Start,
    Whole,
    End,
}

enum class RangeLevel(val level: UByte) {
    Level0(0U),
    Level1(1U),
    Level2(2U),
    Level3(3U),
    Level4(4U),
}

enum class ResolutionLevel(val level: UByte) {
    Level0(0U),
    Level1(1U),
    Level2(2U),
    Level3(3U),
    Level4(4U),
    Level5(5U),
    Level6(6U),
    Level7(7U),
    Level8(8U),
    Level9(9U),
    Level10(10U),
    Level11(11U),
    Level12(12U),
    Level13(13U),
    Level14(14U),
    Level15(15U),
    Level16(16U),
    Level17(17U),
    Level18(18U),
    Level19(19U),
    Level20(20U),
}

enum class ZoneLevel(val level: UByte) {
    Level0(0U),
    Level1(1U),
    Level2(2U),
    Level3(3U),
    Level4(4U),
    Level5(5U),
    Level6(6U),
    Level7(7U),
    Level8(8U),
    Level9(9U),
    Level10(10U),
}
