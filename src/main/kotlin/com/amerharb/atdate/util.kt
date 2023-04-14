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
        ZoneLevel.Level2 -> 11
        ZoneLevel.Level3 -> 27
        ZoneLevel.Level4 -> 47
        ZoneLevel.Level5 -> 67
        ZoneLevel.Level6 -> 87
        ZoneLevel.Level7 -> 107
        ZoneLevel.Level8 -> 127
        ZoneLevel.Level9 -> 146
        ZoneLevel.Level10 -> TODO()
    }
}

fun getLeapSecondsBitCount(leapSecondsFlag: UByte): Int {
    return leapSecondsFlag.toInt() * 8 * 2
}

fun getBodyBitCount(atDateHeader: AtDateHeader): Int {
    return getRangeBitCount(atDateHeader.rangeLevel) +
        getResolutionBitCount(atDateHeader.resolutionLevel) +
        getZoneBitCount(atDateHeader.zoneLevel) +
        getLeapSecondsBitCount(atDateHeader.leapSecondsFlag)
}