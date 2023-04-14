package com.amerharb.atdate

fun decode(input: Array<UByte>): AtDate {
    // takes input array of UByte then returns an AtDate object
    val headerList = mutableListOf<UByte>()
    val bodyList = mutableListOf<UByte>()
    var lastHeadFound = false
    for (b in input) {
        if (!lastHeadFound) {
            headerList.add(b)
            if (b.toUInt() and 0b1000_0000U == 0b1000_0000U) {
                lastHeadFound = true
            }
            continue
        } else {
            bodyList.add(b)
        }
    }

    val atDateHeader = when (headerList.size) {
        0 -> throw Exception("Invalid input")
        1 -> {
            val h = headerList[0]
            if (h and (0b0100_0000U).toUByte() != (0b0100_0000).toUByte()) throw Exception("Period is not supported yet")
            val rangeLevel =
                if (h and 0b0010_0000U == (0b0010_0000U).toUByte()) RangeLevel.Level2 else RangeLevel.Level1
            val resolutionLevel = when (h and 0b0001_1100U) {
                (0b0000_0000U).toUByte() -> ResolutionLevel.Level0
                (0b0000_0100U).toUByte() -> ResolutionLevel.Level1
                (0b0000_1000U).toUByte() -> ResolutionLevel.Level2
                (0b0000_1100U).toUByte() -> ResolutionLevel.Level3
                (0b0001_0000U).toUByte() -> ResolutionLevel.Level4
                (0b0001_0100U).toUByte() -> ResolutionLevel.Level5
                (0b0001_1000U).toUByte() -> ResolutionLevel.Level6
                (0b0001_1100U).toUByte() -> ResolutionLevel.Level7
                else -> throw Exception("Invalid input")
            }
            val zoneLevel = if (h and 0b0000_0010U == (0b0000_0010U).toUByte()) ZoneLevel.Level1 else ZoneLevel.Level0
            val accuracy = if (h and 0b0000_0001U == (0b0000_0001U).toUByte()) Accuracy.End else Accuracy.Start

            AtDateHeader(
                rangeLevel = rangeLevel,
                resolutionLevel = resolutionLevel,
                zoneLevel = zoneLevel,
                accuracy = accuracy,
                leapSecondsFlag = 0U,
            )

        }

        else -> throw Exception("2-bytes and 3 bytes headers are not supported yet")
    }

    val bodyArray = bodyList.toTypedArray()
    var pointer = 0
    val date = when (atDateHeader.rangeLevel) {
        RangeLevel.Level0 -> throw Exception("Range level 0 is not possible in Date")
        else -> {
            val length = getRangeBitCount(atDateHeader.rangeLevel)
            val result = getLongValueFromBytes(bodyArray, pointer, length)
            pointer += length
            result
        }
    } ?: throw Exception("Date value can not be null")

    val timeLength = getResolutionBitCount(atDateHeader.resolutionLevel)
    val time = getLongValueFromBytes(bodyArray, pointer, timeLength)
    pointer += timeLength


    val zoneLength = getZoneBitCount(atDateHeader.zoneLevel)
    val zone = getLongValueFromBytes(bodyArray, pointer, zoneLength)
    pointer += zoneLength

    val leapLength = getLeapSecondsBitCount(atDateHeader.leapSecondsFlag) / 2
    val plusLeapSeconds = getLongValueFromBytes(bodyArray, pointer, leapLength)
    pointer += leapLength
    val minusLeapSeconds = getLongValueFromBytes(bodyArray, pointer, leapLength)
    pointer += leapLength


    return AtDate(
        rangeLevel = atDateHeader.rangeLevel,
        resolutionLevel = atDateHeader.resolutionLevel,
        zoneLevel = atDateHeader.zoneLevel,
        accuracy = atDateHeader.accuracy,
        leapSecondsFlag = atDateHeader.leapSecondsFlag,
        date = date,
        time = time,
        zone = zone,
        plusLeapSeconds = plusLeapSeconds,
        minusLeapSeconds = minusLeapSeconds,
    )
}

fun getLongValueFromBytes(bytes: Array<UByte>, startBit: Int, length: Int): ULong? {
    require(startBit >= 0 && startBit < 8 * bytes.size) { "Start bit index is out of range." }
    require(length >= 0) { "Length has be greater or equal zero." }
    if (length == 0) return null
    val endBit = startBit + length - 1
    require(endBit >= 0 && endBit < 8 * bytes.size) { "End bit index is out of range." }

    var value = 0UL
    for (i in startBit..endBit) {
        val byteIndex = i / 8
        val bitIndex = i % 8
        val bitMask = 0b1000_0000 shr bitIndex
        if (bytes[byteIndex].toInt() and bitMask != 0) {
            value = value or (1UL shl (endBit - i))
        }
    }
    return value
}

data class BasicISODate(
    val year: Long,
    val month: Long,
    val day: Long,
)

fun getDateFromJDN(jdn: Long): BasicISODate {
    val a = jdn + 32044
    val b = (4 * a + 3) / 146097
    val c = a - (146097 * b) / 4
    val d = (4 * c + 3) / 1461
    val e = c - (1461 * d) / 4
    val m = (5 * e + 2) / 153
    val day = e - (153 * m + 2) / 5 + 1
    val month = m + 3 - 12 * (m / 10)
    val year = 100 * b + d - 4800 + (m / 10)
    return BasicISODate(year, month, day)
}