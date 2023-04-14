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

fun getDateFromJdn(jdn: Long): BasicISODate {
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

data class BasicISOTime(
    val hour: Long,
    val minute: Long,
    val second: Double,
)

fun getTimeFromTimeLong(timeLevel: ResolutionLevel, time: ULong): BasicISOTime {
    return when (timeLevel) {
        ResolutionLevel.Level0 -> BasicISOTime(0L, 0L, 0.0)
        ResolutionLevel.Level1 -> BasicISOTime(time.toLong(), 0L, 0.0)
        ResolutionLevel.Level2 -> {
            val hour = time / 4UL
            val minute = time.mod(4UL)
            BasicISOTime(hour.toLong(), minute.toLong(), 0.0)
        }

        ResolutionLevel.Level3 -> {
            val hour = time / 12UL
            val minute = time.mod(12UL)
            return BasicISOTime(hour.toLong(), minute.toLong(), 0.0)
        }

        ResolutionLevel.Level4 -> {
            val hour = time / 60UL
            val minute = time.mod(60UL)
            return BasicISOTime(hour.toLong(), minute.toLong(), 0.0)
        }

        ResolutionLevel.Level5 -> {
            val secInHour = (60 * 60).toULong()
            val hour = time / secInHour
            val minute = time.mod(secInHour) / 60UL
            val second = time.mod(secInHour).mod(60UL).toDouble()
            return BasicISOTime(hour.toLong(), minute.toLong(), second)
        }

        ResolutionLevel.Level6 -> {
            val milisecInHour = (60 * 60 * 1000).toULong()
            val hour = time / milisecInHour
            val minute = time.mod(milisecInHour) / (60 * 1000).toULong()
            val second = time.mod(milisecInHour).mod((60 * 1000).toULong()).toDouble() / 1000.0
            return BasicISOTime(hour.toLong(), minute.toLong(), second)
        }

        ResolutionLevel.Level7 -> {
            val microsecInHour = 60UL * 60UL * 1000UL * 1000UL
            val hour = time / microsecInHour
            val minute = time.mod(microsecInHour) / (60UL * 1000UL * 1000UL)
            val second = time.mod(microsecInHour).mod(60UL * 1000UL * 1000UL).toDouble() / 1_000_000.0
            return BasicISOTime(hour.toLong(), minute.toLong(), second)
        }

        ResolutionLevel.Level8 -> {
            val nanosecInHour = 60UL * 60UL * 1000UL * 1000UL * 1000UL
            val hour = time / nanosecInHour
            val minute = time.mod(nanosecInHour) / (60UL * 1000UL * 1000UL * 1000UL)
            val second = time.mod(nanosecInHour).mod((60UL * 1000UL * 1000UL * 1000UL)).toDouble() / 1_000_000_000.0
            return BasicISOTime(hour.toLong(), minute.toLong(), second)
        }

        else -> TODO() // support for other levels
    }
}

fun getZone(zoneLevel: ZoneLevel, zone: ULong): String {
    return when (zoneLevel) {
        ZoneLevel.Level0 -> ""
        ZoneLevel.Level1 -> { // 7 bits
            val sign = if (zone.toInt() and 0b100_0000 != 0) "-" else "+"
            val hour = (zone.toInt() and 0b111100) shr 2
            val minute = (zone.toInt() and 0b000011) * 15
            "$sign${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
        }
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
}