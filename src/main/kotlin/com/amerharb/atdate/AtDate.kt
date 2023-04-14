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

    fun getNotation(): String {
        val notation = StringBuilder()
        notation.append("@")
        val isoDate = getDateFromJdn(getJdn())
        notation.append("${isoDate.year}-${withLeadingZero(isoDate.month)}-${withLeadingZero(isoDate.day)}")
        if (time != null) {
            notation.append("T")
            val isoTime = getTimeFromTimeLong(resolutionLevel, time)
            notation.append("${withLeadingZero(isoTime.hour)}:${withLeadingZero(isoTime.minute)}:${withLeadingZero(isoTime.second)}")
        }
        if (zone != null) {
            notation.append(getZone(zoneLevel, zone))
        }
        notation.append(" {")
        notation.append(" d:${rangeLevel.no}")
        notation.append(" t:${resolutionLevel.no}")
        notation.append(" z:${zoneLevel.no}")
        notation.append(" a:${accuracy.letter}")
        notation.append(" l:${plusLeapSeconds ?: 0}-${minusLeapSeconds ?: 0}")
        notation.append(" }@")
        return notation.toString()
    }

    private fun withLeadingZero(number: Long): String {
        return if (number < 10) "0$number" else "$number"
    }

    private fun withLeadingZero(number: Double): String {
        val noRightZero = number.toString().replace(Regex("\\.?0+$"), "")
        return if (number < 10) "0$noRightZero" else "$noRightZero"
    }

    private fun getHeader(): Array<UByte> {
        // TODO: later to support header with more than 1 byte
        /** header design IKDTTTZA */
        var header: UByte = 0U
        header = header or 0b1000_0000U // I
        header = header or 0b0100_0000U // K
        header = when (rangeLevel) { // D
            RangeLevel.Level0 -> TODO()
            RangeLevel.Level1 -> header and 0b1101_1111U
            RangeLevel.Level2 -> header or 0b0010_0000U
            RangeLevel.Level3 -> TODO() // needs 2-bytes header
            RangeLevel.Level4 -> TODO()
        }
        header = when (resolutionLevel) { // TTT
            ResolutionLevel.Level0 -> header or 0b0000_0000U
            ResolutionLevel.Level1 -> header or 0b0000_0100U
            ResolutionLevel.Level2 -> header or 0b0000_1000U
            ResolutionLevel.Level3 -> header or 0b0000_1100U
            ResolutionLevel.Level4 -> header or 0b0001_0000U
            ResolutionLevel.Level5 -> header or 0b0001_0100U
            ResolutionLevel.Level6 -> header or 0b0001_1000U
            ResolutionLevel.Level7 -> header or 0b0001_1100U
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
            ZoneLevel.Level0 -> header or 0b0000_0000U
            ZoneLevel.Level1 -> header or 0b0000_0010U
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
            Accuracy.Start -> header or 0b0000_0000U
            Accuracy.Whole -> header or 0b0000_0001U
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
        for (i in (64 - 8) downTo (64 - (getHeadBitCount() + getBodyBitCount())) step 8) {
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

    private fun getRangeBitCount(): Int = getRangeBitCount(this.rangeLevel)

    private fun getResolutionBitCount(): Int = getResolutionBitCount(this.resolutionLevel)

    private fun getZoneBitCount(): Int = getZoneBitCount(this.zoneLevel)

    private fun getLeapSecondsBitCount(): Int = getLeapSecondsBitCount(this.leapSecondsFlag)

    private fun getJdn(): Long {
        when (rangeLevel) {
            RangeLevel.Level0 -> throw Exception("Level 0 is not supported in Date")
            RangeLevel.Level1 -> {
                return date.toLong() or 0b00100101_10000000_00000000L
            }

            RangeLevel.Level2 -> {
                return date.toLong()
            }

            RangeLevel.Level3 -> TODO()
            RangeLevel.Level4 -> TODO()
        }

    }
}

data class AtDateHeader(
    val rangeLevel: RangeLevel,
    val resolutionLevel: ResolutionLevel,
    val zoneLevel: ZoneLevel,
    val accuracy: Accuracy,
    val leapSecondsFlag: UByte,
)

enum class Accuracy(val letter: Char) {
    Start('s'),
    Whole('w'),
    End('e'),
}

enum class RangeLevel(val no: UByte) {
    Level0(0U),
    Level1(1U),
    Level2(2U),
    Level3(3U),
    Level4(4U),
}

enum class ResolutionLevel(val no: UByte) {
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

enum class ZoneLevel(val no: UByte) {
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
