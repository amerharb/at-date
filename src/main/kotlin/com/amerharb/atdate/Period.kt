package com.amerharb.atdate

data class Period(
    val sign: Boolean = true, // true: +, false: -
    val rangeLevel: RangeLevel = RangeLevel.Level1, // TODO: Period can be more than level4
    val resolutionLevel: ResolutionLevel = ResolutionLevel.Level0,
    val leapSecondsFlag: UByte = 0U,
    val date: ULong? = 0U,
    val time: ULong? = null,
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
        notation.append("P")
        notation.append(if (sign) "+" else "-")
        notation.append("${date}D")
        if (time != null) {
            notation.append("T")
            val isoTime = getTimeFromTimeLong(resolutionLevel, time)
            notation.append(
                "${withLeadingZero(isoTime.hour)}:${withLeadingZero(isoTime.minute)}:${
                    withLeadingZero(
                        isoTime.second
                    )
                }"
            )
        }
        notation.append(" {")
        notation.append(" d:${rangeLevel.no}")
        notation.append(" t:${resolutionLevel.no}")
        notation.append(" l:${plusLeapSeconds ?: 0}-${minusLeapSeconds ?: 0}")
        notation.append(" }@")
        return notation.toString()
    }

    private fun withLeadingZero(number: Long): String {
        return number.toString().padStart(2, '0')
    }

    private fun withLeadingZero(number: Double): String {
        val noRightZero = number.toString().replace(Regex("\\.?0+$"), "")
        return if (number < 10) "0$noRightZero" else noRightZero
    }

    private fun getHeader(): Array<UByte> {
        // check number of header bytes needed
        val headerCount = getHeadBitCount() / 8

        return when (headerCount) {
            1 -> getPeriodHeader1Byte()
            2 -> getPeriodHeader2Bytes()
            else -> throw Exception("Header count is not valid")
        }
    }

    private fun getBody(): Array<UByte> {
        var body: ULong = 0U
        // move date bits to the left then add it to body
        val shiftDate = 64 - getRangeBitCount()
        body = body or ((date ?: 0U) shl shiftDate)
        // move time bits to the left then add it to body
        val shiftTime = shiftDate - getResolutionBitCount()
        body = body or ((time ?: 0U) shl shiftTime)
        // move plus leap seconds bits to the left then add it to body
        val shiftPlusLeapSeconds = shiftTime - (getLeapSecondsBitCount() / 2)
        body = body or ((plusLeapSeconds ?: 0U) shl shiftPlusLeapSeconds)
        // move minus leap seconds bits to the left then add it to body
        val shiftMinusLeapSeconds = shiftPlusLeapSeconds - (getLeapSecondsBitCount() / 2)
        body = body or ((minusLeapSeconds ?: 0U) shl shiftMinusLeapSeconds)

        // how many bytes in body
        val bodyBitCount = getBodyBitCount()
        val bodyByteCount = if (bodyBitCount % 8 == 0) {
            bodyBitCount / 8
        } else {
            bodyBitCount / 8 + 1
        }

        // TODO: support body longer than 8 bytes
        val bodyList = mutableListOf<UByte>()
        for (i in (64 - 8) downTo (64 - (8 + bodyBitCount)) step 8) {
            bodyList.add((body shr i).toUByte())
        }
        return bodyList.toTypedArray()
    }

    private fun getPeriodHeader1Byte(): Array<UByte> {
        /** header design IKSDDTTT */
        var header: UByte = 0U
        header = header or 0b1000_0000U // I
        header = header and 0b1011_1111U // K (always 0 for Period)
        if (!sign) header = header or 0b0010_0000U // S
        header = when (rangeLevel) { // D
            RangeLevel.Level0 -> header and 0b1110_0111U
            RangeLevel.Level1 -> header or 0b0000_1000U
            RangeLevel.Level2 -> header or 0b0001_0000U
            RangeLevel.Level3 -> header or 0b0001_1000U
            else -> throw Exception("Range level $rangeLevel is not valid in 1-byte header")
        }
        header = when (resolutionLevel) { // TTT
            ResolutionLevel.Level0 -> header and 0b1111_1000U
            ResolutionLevel.Level1 -> header or 0b0000_0001U
            ResolutionLevel.Level2 -> header or 0b0000_0010U
            ResolutionLevel.Level3 -> header or 0b0000_0011U
            ResolutionLevel.Level4 -> header or 0b0000_0100U
            ResolutionLevel.Level5 -> header or 0b0000_0101U
            ResolutionLevel.Level6 -> header or 0b0000_0110U
            ResolutionLevel.Level7 -> header or 0b0000_0111U
            else -> throw Exception("Resolution level $resolutionLevel is not valid in 1-byte header")
        }
        return arrayOf(header)
    }

    private fun getPeriodHeader2Bytes(): Array<UByte> {
        /** header design IKSDDDTT ITTTLLL? */
        var header1: UByte = 0U
        var header2: UByte = 0U
        header1 = header1 or 0b0000_0000U // I
        header2 = header2 or 0b1000_0000U // I
        header1 = header1 and 0b1011_1111U // xKxx_xxxx
        if (!sign) header1 = header1 or 0b0010_0000U // xxSx_xxxx
        header1 = when (rangeLevel) { // xxxD_DDxx
            RangeLevel.Level0 -> header1 and 0b1110_0011U
            RangeLevel.Level1 -> header1 or 0b0000_0100U
            RangeLevel.Level2 -> header1 or 0b0000_1000U
            RangeLevel.Level3 -> header1 or 0b0000_1100U
            RangeLevel.Level4 -> header1 or 0b0001_0000U
            // TODO: support above level 4
        }
        val timeBits = when (resolutionLevel) { // xxxx_xxTT TTTx_xxxx
            ResolutionLevel.Level0 -> Pair(header1 and 0b1111_1100U, header2 and 0b0001_1111U)
            ResolutionLevel.Level1 -> Pair(header1 and 0b1111_1100U, header2 or 0b0010_0000U)
            ResolutionLevel.Level2 -> Pair(header1 and 0b1111_1100U, header2 or 0b0100_0000U)
            ResolutionLevel.Level3 -> Pair(header1 and 0b1111_1100U, header2 or 0b0110_0000U)
            ResolutionLevel.Level4 -> Pair(header1 and 0b1111_1100U, header2 or 0b1000_0000U)
            ResolutionLevel.Level5 -> Pair(header1 and 0b1111_1100U, header2 or 0b1010_0000U)
            ResolutionLevel.Level6 -> Pair(header1 and 0b1111_1100U, header2 or 0b1100_0000U)
            ResolutionLevel.Level7 -> Pair(header1 and 0b1111_1100U, header2 or 0b1110_0000U)
            ResolutionLevel.Level8 -> Pair(header1 or 0b0000_0001U, header2 and 0b0001_1111U)
            ResolutionLevel.Level9 -> Pair(header1 or 0b0000_0001U, header2 or 0b0010_0000U)
            ResolutionLevel.Level10 -> Pair(header1 or 0b0000_0001U, header2 or 0b0100_0000U)
            ResolutionLevel.Level11 -> Pair(header1 or 0b0000_0001U, header2 or 0b0110_0000U)
            ResolutionLevel.Level12 -> Pair(header1 or 0b0000_0001U, header2 or 0b1000_0000U)
            ResolutionLevel.Level13 -> Pair(header1 or 0b0000_0001U, header2 or 0b1010_0000U)
            ResolutionLevel.Level14 -> Pair(header1 or 0b0000_0001U, header2 or 0b1100_0000U)
            ResolutionLevel.Level15 -> Pair(header1 or 0b0000_0001U, header2 or 0b1110_0000U)
            else -> throw Exception("ResolutionLevel $resolutionLevel is not supported in 2-bytes period header")
        }
        header1 = timeBits.first
        header2 = timeBits.second
        header2 = when (leapSecondsFlag) { // xxxL_LLxx
            (0U).toUByte() -> header2 and 0b1110_0011U
            (1U).toUByte() -> header2 or 0b0000_0100U
            (2U).toUByte() -> header2 or 0b0000_1000U
            (3U).toUByte() -> header2 or 0b0000_1100U
            (4U).toUByte() -> header2 or 0b0001_0000U
            (5U).toUByte() -> header2 or 0b0001_0100U
            (6U).toUByte() -> header2 or 0b0001_1000U
            (7U).toUByte() -> header2 or 0b0001_1100U
            else -> throw Exception("LeapSecondsFlag $leapSecondsFlag is not supported in 2-bytes period header")
        }
        return arrayOf(header1, header2)
    }

    private fun getHeadBitCount(): Int {
        var headBitCount = 8
        if (rangeLevel.no > RangeLevel.Level3.no
            || resolutionLevel.no > ResolutionLevel.Level7.no
            || leapSecondsFlag > 0U
        ) {
            headBitCount += 8
        }
        return headBitCount
    }

    private fun getBodyBitCount(): Int {
        return getRangeBitCount() + getResolutionBitCount() + getLeapSecondsBitCount()
    }

    private fun getRangeBitCount(): Int = getRangeBitCount(this.rangeLevel)

    private fun getResolutionBitCount(): Int = getResolutionBitCount(this.resolutionLevel)

    private fun getLeapSecondsBitCount(): Int = getLeapSecondsBitCount(this.leapSecondsFlag)
}

data class AtPeriodHeader(
    val sign: Boolean,
    val rangeLevel: RangeLevel,
    val resolutionLevel: ResolutionLevel,
    val leapSecondsFlag: UByte,
)
