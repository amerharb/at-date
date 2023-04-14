package com.amerharb.atdate

data class AtDate(
    // TODO: support period
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
) {
    fun getPayload(): Array<UByte> {
        val payload = mutableListOf<UByte>()
        payload.addAll(getHeader())
        payload.addAll(getBody())
        return payload.toTypedArray()
    }

    private fun getHeader(): Array<UByte> {
        // TODO: later to support header with more than 1 byte
        /** header design IKDTTTZA */
        var header: UByte = 0U
        header = header or 0B1000_0000U // I
        header = header or 0B0100_0000U // K
        header = when (rangeLevel) { // D
            RangeLevel.Level0 -> TODO()
            RangeLevel.Level1 -> header and 0B1101_1111U
            RangeLevel.Level2 -> header or 0B0010_0000U
            RangeLevel.Level3 -> TODO() // needs 2-bytes header
            RangeLevel.Level4 -> TODO()
        }
        header = when (resolutionLevel) { // TTT
            ResolutionLevel.Level0 -> header or 0B0000_0000U
            ResolutionLevel.Level1 -> header or 0B0000_0100U
            ResolutionLevel.Level2 -> header or 0B0000_1000U
            ResolutionLevel.Level3 -> header or 0B0000_1100U
            ResolutionLevel.Level4 -> header or 0B0001_0000U
            ResolutionLevel.Level5 -> header or 0B0001_0100U
            ResolutionLevel.Level6 -> header or 0B0001_1000U
            ResolutionLevel.Level7 -> header or 0B0001_1100U
            ResolutionLevel.Level8 -> TODO()
            ResolutionLevel.Level9 -> TODO()
            ResolutionLevel.Level10 -> TODO()
            ResolutionLevel.Level11 -> TODO()
            ResolutionLevel.Level12 -> TODO()
            ResolutionLevel.Level13 -> TODO()
            ResolutionLevel.Level14 -> TODO()
            ResolutionLevel.Level15 -> TODO()
            ResolutionLevel.Level16 -> TODO()
            ResolutionLevel.Level17 -> TODO()
            ResolutionLevel.Level18 -> TODO()
            ResolutionLevel.Level19 -> TODO()
            ResolutionLevel.Level20 -> TODO()
        }
        header = when (zoneLevel) { // Z
            ZoneLevel.Level0 -> header or 0B0000_0000U
            ZoneLevel.Level1 -> header or 0B0000_0010U
            ZoneLevel.Level2 -> TODO()
            ZoneLevel.Level3 -> TODO()
            ZoneLevel.Level4 -> TODO()
            ZoneLevel.Level5 -> TODO()
            ZoneLevel.Level6 -> TODO()
            ZoneLevel.Level7 -> TODO()
            ZoneLevel.Level8 -> TODO()
            ZoneLevel.Level9 -> TODO()
            ZoneLevel.Level10 -> TODO()
        }
        header = when (accuracy) { // A
            Accuracy.Start -> header or 0B0000_0000U
            Accuracy.Whole -> header or 0B0000_0001U
            Accuracy.End -> TODO()
        }
        return arrayOf(header)
    }

    private fun getBody(): Array<UByte> {
        var body: ULong = 0U
        // move date bits to the left then add it to body
        val shiftDate = 64 - getRangeBitCount()
        body = body or (date shl shiftDate)
        // move time bits to the left then add it to body
        val shiftTime = shiftDate - getResolutionBitCount()
        body = body or ((time ?: 0U) shl shiftTime)
        // move zone bits to the left then add it to body
        val shiftZone = shiftTime - getZoneBitCount()
        body = body or ((zone ?: 0U) shl shiftZone)
        // move plus leap seconds bits to the left then add it to body
        val shiftPlusLeapSeconds = shiftZone - (getLeapSecondsBitCount() / 2)
        body = body or ((plusLeapSeconds ?: 0U) shl shiftPlusLeapSeconds)
        // move minus leap seconds bits to the left then add it to body
        val shiftMinusLeapSeconds = shiftPlusLeapSeconds - (getLeapSecondsBitCount() / 2)
        body = body or ((minusLeapSeconds ?: 0U) shl shiftMinusLeapSeconds)

        // how many bytes in body
        val bodyByteCount = if (getBodyBitCount() % 8 == 0) {
            getBodyBitCount() / 8
        } else {
            getBodyBitCount() / 8 + 1
        }

        // TODO: support body longer than 8 bytes
        val bodyList = mutableListOf<UByte>()
        for (i in (64 - 8) downTo   (64 - (getHeadBitCount() + getBodyBitCount())) step 8) {
            bodyList.add((body shr i).toUByte())
        }
        return bodyList.toTypedArray()
    }

    private fun getHeadBitCount(): Int {
        // TODO: support header with more than 1 byte
        return 8
    }

    private fun getBodyBitCount(): Int {
        return getRangeBitCount() + getResolutionBitCount() + getZoneBitCount() + getLeapSecondsBitCount()
    }

    private fun getRangeBitCount(): Int {
        return when (rangeLevel) {
            RangeLevel.Level0 -> 0
            RangeLevel.Level1 -> 15
            RangeLevel.Level2 -> 22
            RangeLevel.Level3 -> 32
            RangeLevel.Level4 -> 48
        }
    }

    private fun getResolutionBitCount(): Int {
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

    private fun getZoneBitCount(): Int {
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

    private fun getLeapSecondsBitCount(): Int {
        return leapSecondsFlag.toInt() * 8 * 2
    }
}

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
