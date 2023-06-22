package com.amerharb.atdate

data class Moment(
	val rangeLevel: RangeLevel = RangeLevel.Level1,
	val resolutionLevel: ResolutionLevel = ResolutionLevel.Level0,
	val zoneLevel: ZoneLevel = ZoneLevel.Level0,
	val accuracy: Accuracy = Accuracy.Start,
	val leapSecondsFlag: UByte = 0U,
	val date: ULong = 0U,
	val time: ULong? = null,
	val zone: ULong? = null,
	val plusLeapSeconds: ULong? = null,
	val minusLeapSeconds: ULong? = null
) : AtDate() {
	override fun getNotation(): String {
		val notation = StringBuilder()
		notation.append("@")
		val isoDate = getDateFromJdn(getJdn())
		notation.append("${isoDate.year}-${withLeadingZero(isoDate.month)}-${withLeadingZero(isoDate.day)}")
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
		return number.toString().padStart(2, '0')
	}

	private fun withLeadingZero(number: Double): String {
		val noRightZero = number.toString().replace(Regex("\\.?0+$"), "")
		return if (number < 10) "0$noRightZero" else noRightZero
	}

	override fun getHeader(): Array<UByte> {
		// check number of header bytes needed
		val headerCount = getHeadBitCount() / 8

		return when (headerCount) {
			1 -> getMomentHeader1Byte()
			2 -> getMomentHeader2Bytes()
			3 -> TODO() // needs 3-bytes header (not supported yet)
			else -> throw Exception("Header count is not valid")
		}
	}

	override fun getBody(): Array<UByte> {
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
		val bodyBitCount = getBodyBitCount()

		// TODO: support body longer than 8 bytes
		val bodyList = mutableListOf<UByte>()
		for (i in (64 - 8) downTo (64 - (8 + bodyBitCount)) step 8) {
			bodyList.add((body shr i).toUByte())
		}
		return bodyList.toTypedArray()
	}

	private fun getMomentHeader1Byte(): Array<UByte> {
		/** header design IKDTTTZA */
		var header: UByte = 0U
		header = header or 0b1000_0000U // I
		header = header or 0b0100_0000U // K (always 1 for Moment)
		header = when (rangeLevel) { // D
			RangeLevel.Level0 -> throw Exception("Range level $rangeLevel is not valid in Moment header")
			RangeLevel.Level1 -> header and 0b1101_1111U
			RangeLevel.Level2 -> header or 0b0010_0000U
			else -> throw Exception("Range level $rangeLevel is not valid in 1-byte header")
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
			else -> throw Exception("Resolution level $resolutionLevel is not valid in 1-byte header")
		}
		header = when (zoneLevel) { // Z
			ZoneLevel.Level0 -> header or 0b0000_0000U
			ZoneLevel.Level1 -> header or 0b0000_0010U
			else -> throw Exception("Zone level $zoneLevel is not valid in 1-byte header")
		}
		header = when (accuracy) { // A
			Accuracy.Start -> header or 0b0000_0000U
			Accuracy.Whole -> header or 0b0000_0001U
			Accuracy.End -> throw Exception("Accuracy End is not valid in 1-byte header")
		}
		return arrayOf(header)
	}

	private fun getMomentHeader2Bytes(): Array<UByte> {
		/** header design IKDDTTTT IZZZLLAA */
		var header1: UByte = 0U
		var header2: UByte = 0U
		header1 = header1 or 0b0000_0000U // I
		header2 = header2 or 0b1000_0000U // I

		header1 = header1 or 0b0100_0000U // K
		header1 = when (rangeLevel) { // DD
			RangeLevel.Level0 -> throw Exception("Range level $rangeLevel is not valid in Moment header")
			RangeLevel.Level1 -> header1 and 0b1100_1111U
			RangeLevel.Level2 -> header1 or 0b0001_0000U
			RangeLevel.Level3 -> header1 or 0b0010_0000U
			RangeLevel.Level4 -> header1 or 0b0011_0000U
		}
		header1 = when (resolutionLevel) { // TTTT
			ResolutionLevel.Level0 -> header1 or 0b0000_0000U
			ResolutionLevel.Level1 -> header1 or 0b0000_0001U
			ResolutionLevel.Level2 -> header1 or 0b0000_0010U
			ResolutionLevel.Level3 -> header1 or 0b0000_0011U
			ResolutionLevel.Level4 -> header1 or 0b0000_0100U
			ResolutionLevel.Level5 -> header1 or 0b0000_0101U
			ResolutionLevel.Level6 -> header1 or 0b0000_0110U
			ResolutionLevel.Level7 -> header1 or 0b0000_0111U
			ResolutionLevel.Level8 -> header1 or 0b0000_1000U
			ResolutionLevel.Level9 -> header1 or 0b0000_1001U
			ResolutionLevel.Level10 -> header1 or 0b0000_1010U
			ResolutionLevel.Level11 -> header1 or 0b0000_1011U
			ResolutionLevel.Level12 -> header1 or 0b0000_1100U
			ResolutionLevel.Level13 -> header1 or 0b0000_1101U
			ResolutionLevel.Level14 -> header1 or 0b0000_1110U
			ResolutionLevel.Level15 -> header1 or 0b0000_1111U
			else -> throw Exception("ResolutionLevel $resolutionLevel is not supported in 2-bytes header")
		}
		header2 = when (zoneLevel) { // ZZZ
			ZoneLevel.Level0 -> header2 or 0b0000_0000U
			ZoneLevel.Level1 -> header2 or 0b0001_0000U
			ZoneLevel.Level2 -> header2 or 0b0010_0000U
			ZoneLevel.Level3 -> header2 or 0b0011_0000U
			ZoneLevel.Level4 -> header2 or 0b0100_0000U
			ZoneLevel.Level5 -> header2 or 0b0101_0000U
			ZoneLevel.Level6 -> header2 or 0b0110_0000U
			ZoneLevel.Level7 -> header2 or 0b0111_0000U
			else -> throw Exception("ZoneLevel $zoneLevel is not supported in 2-bytes header")
		}
		header2 = when (leapSecondsFlag) { // LL
			(0U).toUByte() -> header2 or 0b0000_0000U
			(1U).toUByte() -> header2 or 0b0000_0100U
			(2U).toUByte() -> header2 or 0b0000_1000U
			(3U).toUByte() -> header2 or 0b0000_1100U
			else -> throw Exception("LeapSecondsFlag $leapSecondsFlag is not supported in 2-bytes header")
		}
		header2 = when (accuracy) { // AA
			Accuracy.Start -> header2 or 0b0000_0000U
			Accuracy.Whole -> header2 or 0b0000_0001U
			Accuracy.End -> header2 or 0b0000_0010U
		}
		return arrayOf(header1, header2)
	}

	private fun getHeadBitCount(): Int {
		var headBitCount = 8
		if (rangeLevel.no > RangeLevel.Level2.no ||
			resolutionLevel.no > ResolutionLevel.Level7.no ||
			zoneLevel.no > ZoneLevel.Level1.no ||
			accuracy == Accuracy.End ||
			leapSecondsFlag > 0U
		) {
			headBitCount += 8
		}
		if (resolutionLevel.no > ResolutionLevel.Level15.no ||
			zoneLevel.no > ZoneLevel.Level7.no ||
			leapSecondsFlag > 3U
		) {
			headBitCount += 8
		}
		return headBitCount
	}

	private fun getBodyBitCount(): Int {
		return getRangeBitCount() + getResolutionBitCount() + getZoneBitCount() + getLeapSecondsBitCount()
	}

	private fun getRangeBitCount(): Int = getRangeBitCount(this.rangeLevel)

	private fun getResolutionBitCount(): Int = getResolutionBitCount(this.resolutionLevel)

	private fun getZoneBitCount(): Int = getZoneBitCount(this.zoneLevel)

	private fun getLeapSecondsBitCount(): Int = getLeapSecondsBitCount(this.leapSecondsFlag)

	private fun getJdn(): Long {
		return when (rangeLevel) {
			RangeLevel.Level0 -> throw Exception("Level 0 is not supported in Date")
			RangeLevel.Level1 -> {
				((date and 0b01111111_11111111UL) or 0b00100101_10000000_00000000UL).toLong()
			}

			RangeLevel.Level2 -> {
				date.toLong()
			}

			RangeLevel.Level3 -> { // 32 bits 0 JDN = 0x80000000 = 0b10000000_00000000_00000000_00000000L
				val right31 = date and 0x7F_FF_FF_FFUL
				if (date >= 0x80_00_00_00UL) {
					right31.toLong()
				} else {
					0L - (0x80_00_00_00UL - right31).toLong()
				}
			}

			RangeLevel.Level4 -> { // 48 bits 0 JDN = 0x800000000000 = 0b10000000_00000000_00000000_00000000_00000000_00000000L
				val right47 = date and 0x7F_FF_FF_FF_FF_FFUL
				if (date >= 0x80_00_00_00_00_00UL) {
					right47.toLong()
				} else {
					0L - (0x80_00_00_00_00_00UL - right47).toLong()
				}
			}
		}
	}
}

data class AtMomentHeader(
	val rangeLevel: RangeLevel,
	val resolutionLevel: ResolutionLevel,
	val zoneLevel: ZoneLevel,
	val accuracy: Accuracy,
	val leapSecondsFlag: UByte
)

enum class Accuracy(val letter: Char) {
	Start('s'),
	Whole('w'),
	End('e')
}

enum class RangeLevel(val no: UByte) {
	Level0(0U),
	Level1(1U),
	Level2(2U),
	Level3(3U),
	Level4(4U)
}

enum class ResolutionLevel(val no: UByte) {
	Level0(0U),
	Level1(1U),
	Level2(2U),
	Level3(3U),
	Level4(4U),
	Level5(5U), // Seconds
	Level6(6U), // Milliseconds
	Level7(7U), // Microseconds
	Level8(8U), // Nanoseconds
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
	Level20(20U)
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
	Level10(10U)
}
