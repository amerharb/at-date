package com.amerharb.atdate

fun getRangeBitCount(rangeLevel: RangeLevel): Int {
    return when (rangeLevel) {
        RangeLevel.Level0 -> 0
        RangeLevel.Level1 -> 15
        RangeLevel.Level2 -> 22
        RangeLevel.Level3 -> 32
        RangeLevel.Level4 -> 48
    }
}

fun getResolutionBitCount(resolutionLevel: ResolutionLevel): Int {
    return when (resolutionLevel) {
        ResolutionLevel.Level0 -> 0
        ResolutionLevel.Level1 -> 5
        ResolutionLevel.Level2 -> 7
        ResolutionLevel.Level3 -> 9
        ResolutionLevel.Level4 -> 11
        ResolutionLevel.Level5 -> 17
        ResolutionLevel.Level6 -> 27
        ResolutionLevel.Level7 -> 37
        ResolutionLevel.Level8 -> 47
        ResolutionLevel.Level9 -> 57
        ResolutionLevel.Level10 -> 67
        ResolutionLevel.Level11 -> 77
        ResolutionLevel.Level12 -> 87
        ResolutionLevel.Level13 -> 97
        ResolutionLevel.Level14 -> 107
        ResolutionLevel.Level15 -> 117
        ResolutionLevel.Level16 -> 127
        ResolutionLevel.Level17 -> 136
        ResolutionLevel.Level18 -> 146
        ResolutionLevel.Level19 -> 156
        ResolutionLevel.Level20 -> TODO()
    }
}

fun getZoneBitCount(zoneLevel: ZoneLevel): Int {
    return when (zoneLevel) {
        ZoneLevel.Level0 -> 0
        ZoneLevel.Level1 -> 7
        ZoneLevel.Level2 -> getResolutionBitCount(ResolutionLevel.Level4) + 1
        ZoneLevel.Level3 -> getResolutionBitCount(ResolutionLevel.Level6) + 1
        ZoneLevel.Level4 -> getResolutionBitCount(ResolutionLevel.Level8) + 1
        ZoneLevel.Level5 -> getResolutionBitCount(ResolutionLevel.Level10) + 1
        ZoneLevel.Level6 -> getResolutionBitCount(ResolutionLevel.Level12) + 1
        ZoneLevel.Level7 -> getResolutionBitCount(ResolutionLevel.Level14) + 1
        ZoneLevel.Level8 -> getResolutionBitCount(ResolutionLevel.Level16) + 1
        ZoneLevel.Level9 -> getResolutionBitCount(ResolutionLevel.Level18) + 1
        ZoneLevel.Level10 -> getResolutionBitCount(ResolutionLevel.Level20) + 1
    }
}

fun getLeapSecondsBitCount(leapSecondsFlag: UByte): Int {
    return leapSecondsFlag.toInt() * 8 * 2
}

fun getBodyBitCount(atMomentHeader: AtMomentHeader): Int {
    return getRangeBitCount(atMomentHeader.rangeLevel) +
        getResolutionBitCount(atMomentHeader.resolutionLevel) +
        getZoneBitCount(atMomentHeader.zoneLevel) +
        getLeapSecondsBitCount(atMomentHeader.leapSecondsFlag)
}