package com.amerharb.atdate

fun encodeMoment(input: String): Moment {
    // takes input like "@2019-01-01T00:00:00Z {d:1 t:5 z:1 a:s l:0-0}@"
    // takes input like "@1979-11-14 {d:1 t:5 z:1 a:s l:0-0}@"
    // and returns an AtDate object
    val ad = input.trim().substringAfter("@").substringBefore("@").trim()
    val datetime = ad.substringBefore("{").trim()
    val datetimeArr = datetime.split("T")
    val datePart = datetimeArr[0]
    val timezonePart = if (datetimeArr.size > 1) datetimeArr[1] else ""
    val prop = ad.substringAfter("{").substringBefore("}")
    val propArr = prop.trim().split(" ").map { it.trim() }

    val dValue = propArr.find { it.startsWith("d:") }?.substringAfter(":")
    val providedRangeLevel = RangeLevel.values().find { it.no == dValue?.toUByte() }

    val tValue = propArr.find { it.startsWith("t:") }?.substringAfter(":")
    val providedResolutionLevel = ResolutionLevel.values().find { it.no == tValue?.toUByte() }

    val aValue = propArr.find { it.startsWith("a:") }?.substringAfter(":")?.firstOrNull() ?: 's'
    val accuracy = Accuracy.values().find { it.letter == aValue } ?: throw Exception("Invalid accuracy level")

    val lValue = propArr.find { it.startsWith("l:") }?.substringAfter(":")
    val lList = lValue?.split("-")?.map { it.toULong() } ?: listOf(0UL, 0UL)
    val pl = lList[0]
    val ml = lList[1]
    val leapSecondsFlag: UByte = when {
        (pl == 0UL && ml == 0UL) -> 0U
        (pl < 0xFFUL && ml < 0xFFUL) -> 1U
        (pl < 0xFFFFUL && ml < 0xFFFFUL) -> 2U
        (pl < 0xFF_FFFFUL && ml < 0xFF_FFFFUL) -> 3U
        (pl < 0xFFFF_FFFFUL && ml < 0xFFFF_FFFFUL) -> 4U
        (pl < 0xFF_FFFF_FFFFUL && ml < 0xFF_FFFF_FFFFUL) -> 5U
        (pl < 0xFFFF_FFFF_FFFFUL && ml < 0xFFFF_FFFF_FFFFUL) -> 6U
        (pl < 0xFF_FFFF_FFFF_FFFFUL && ml < 0xFF_FFFF_FFFF_FFFFUL) -> 7U
        (pl < 0xFFFF_FFFF_FFFF_FFFFUL && ml < 0xFFFF_FFFF_FFFF_FFFFUL) -> 8U
        else -> throw Exception("leap second over 8 bytes is not supported")
    }

    val zValue = propArr.find { it.startsWith("z:") }?.substringAfter(":")
    val providedZoneLevel = ZoneLevel.values().find { it.no == zValue?.toUByte() }

    // TODO: fix the case where year is minus, then it will start with - and split wrong
    val (year, month, day) = datePart.split("-").map { it.toLong() }
    val jdn = getJdn(year, month, day)
    val rangeLevel = providedRangeLevel ?: getSuitableRangeLevel(jdn)
    val dateULong = when (rangeLevel) {
        RangeLevel.Level0 -> throw Exception("range level 0 only allowed with tp")
        RangeLevel.Level1 -> {
            // only most right 15 bits are used
            jdn.toULong() and 0b01111111_11111111UL
        }

        RangeLevel.Level2 -> jdn.toULong()
        RangeLevel.Level3 -> TODO()
        RangeLevel.Level4 -> TODO()
    }

    // read time iso format after T and before Z, + or -
    val timePart = when {
        timezonePart.endsWith("Z") -> timezonePart.substringBeforeLast("Z")
        timezonePart.contains("+") -> timezonePart.substringBeforeLast("+")
        timezonePart.contains("-") -> timezonePart.substringBeforeLast("-")
        timezonePart.contains("@") -> timezonePart.substringBeforeLast("@").trim()
        else -> timezonePart.substringAfter("T").trim()
    }

    val (resolutionLevel, timeULong) = if (timePart.trim() != "") {
        val (hour, min, sec, secFraction, precision) = destructTimePart(timePart)
        val rLevel = providedResolutionLevel ?: getSuitableResolutionLevel(precision)

        when (rLevel) {
            ResolutionLevel.Level0 -> Pair(rLevel, null)
            ResolutionLevel.Level1 -> Pair(rLevel, hour)
            ResolutionLevel.Level2 -> Pair(rLevel, (hour * 4UL) + (min / 15UL)) // count every 15 minutes
            ResolutionLevel.Level3 -> Pair(rLevel, (hour * 12UL) + (min / 5UL)) // count every 5 minutes
            ResolutionLevel.Level4 -> Pair(rLevel, (hour * 60UL) + min) // count minutes
            ResolutionLevel.Level5 -> Pair(rLevel, (hour * 3600UL) + (min * 60UL) + sec) // count seconds
            ResolutionLevel.Level6 -> Pair(
                rLevel, (hour * 3600_000UL) + (min * 60_000UL) + (sec * 1000UL)
                        + secFraction.adaptFraction(rLevel, precision)
            ) // count milliseconds

            ResolutionLevel.Level7 -> Pair(
                rLevel, (hour * 3600_000_000UL) + (min * 60_000_000UL) + (sec * 1000_000UL)
                        + secFraction.adaptFraction(rLevel, precision)
            ) // count microseconds

            ResolutionLevel.Level8 -> Pair(
                rLevel,
                (hour * 3600_000_000_000UL) + (min * 60_000_000_000UL) + (sec * 1000_000_000UL)
                        + secFraction.adaptFraction(rLevel, precision)
            ) // count nanoseconds

            ResolutionLevel.Level9 -> {
                Pair(
                    rLevel,
                    (hour * 3600_000_000_000_000UL) + (min * 60_000_000_000_000UL) + (sec * 1000_000_000_000UL)
                            + secFraction.adaptFraction(rLevel, precision)
                ) // count picoseconds
            }

            ResolutionLevel.Level10 -> {
                Pair(
                    rLevel,
                    (hour * 3600_000_000_000_000_000UL) + (min * 60_000_000_000_000_000UL)
                            + (sec * 1000_000_000_000_000UL) + secFraction.adaptFraction(rLevel, precision)
                ) // count femtoseconds
            }

            // from Level10 ULong is not enough, go be support later with more than 1 variable
//            ResolutionLevel.Level10 -> throw Exception("Time resolution $rlevel is not supported yet") // TODO:
            ResolutionLevel.Level11 -> throw Exception("Time resolution $rLevel is not supported yet") // TODO:
            ResolutionLevel.Level12 -> throw Exception("Time resolution $rLevel is not supported yet") // TODO:
            ResolutionLevel.Level13 -> throw Exception("Time resolution $rLevel is not supported yet") // TODO:
            ResolutionLevel.Level14 -> throw Exception("Time resolution $rLevel is not supported yet") // TODO:
            ResolutionLevel.Level15 -> throw Exception("Time resolution $rLevel is not supported yet") // TODO:
            ResolutionLevel.Level16 -> throw Exception("Time resolution $rLevel is not supported yet") // TODO:
            ResolutionLevel.Level17 -> throw Exception("Time resolution $rLevel is not supported yet") // TODO:
            ResolutionLevel.Level18 -> throw Exception("Time resolution $rLevel is not supported yet") // TODO:
            ResolutionLevel.Level19 -> throw Exception("Time resolution $rLevel is not supported yet") // TODO:
            ResolutionLevel.Level20 -> throw Exception("Time resolution $rLevel is not supported yet") // TODO:
        }
    } else {
        Pair(ResolutionLevel.Level0, null)
    }

    // read offset part of iso format
    val (zoneLevel, zoneULong) = when {
        timezonePart.endsWith("Z") -> Pair(providedZoneLevel ?: ZoneLevel.Level1, 0UL)
        timezonePart.contains("+") -> {
            val offset = timezonePart.substringAfterLast("+").substringBeforeLast("@").split(":")
            val z = offset[0].toByte() * 4 + offset[1].toByte() / 15
            Pair(providedZoneLevel ?: ZoneLevel.Level1, (z and 0b00111111).toULong())
        }

        timezonePart.contains("-") -> {
            val offset = timezonePart.substringAfterLast("-").substringBeforeLast("@").split(":")
            val z = offset[0].toByte() * 4 + offset[1].toByte() / 15
            val z6bits = z and 0b00111111
            Pair(providedZoneLevel ?: ZoneLevel.Level1, (z6bits or 0b01000000).toULong())
        }

        else -> Pair(providedZoneLevel ?: ZoneLevel.Level0, null)
    }

    return Moment(
        rangeLevel = rangeLevel,
        resolutionLevel = resolutionLevel,
        zoneLevel = zoneLevel,
        accuracy = accuracy,
        leapSecondsFlag = leapSecondsFlag,
        date = dateULong,
        time = timeULong,
        zone = zoneULong,
        plusLeapSeconds = if (leapSecondsFlag == 0.toUByte()) null else pl,
        minusLeapSeconds = if (leapSecondsFlag == 0.toUByte()) null else ml,
    )
}

fun encodePeriod(input: String): Period {
    TODO() // TODO:
}

fun getJdn(year: Long, month: Long, day: Long): Long {
    val p1 = (1461 * (year + 4800 + (month - 14) / 12)) / 4
    val p2 = (367 * (month - 2 - 12 * ((month - 14) / 12))) / 12
    val p3 = -(3 * ((year + 4900 + (month - 14) / 12) / 100)) / 4 + day - 32075
    return p1 + p2 + p3
}

private fun getSuitableRangeLevel(jdn: Long): RangeLevel {
    return when {
        jdn in 0x258000..0x25FFFF -> RangeLevel.Level1
        jdn in 0..0x3FFFFF -> RangeLevel.Level2
        jdn in -0x80000000..0xFFFFFFFF -> RangeLevel.Level3
        else -> RangeLevel.Level4
    }
}

private fun getSuitableResolutionLevel(precision: UByte): ResolutionLevel =
    ResolutionLevel.values().find { it.no == (precision + 5U).toUByte() }
        ?: throw Exception("Can't find suitable resolution level for precision $precision")

private fun destructTimePart(timePart: String): TimeParts {
    val (h, m, s) = timePart.split(":")
    val hour = h.toULong()
    val minute = m.toULong()
    val secondArr = s.split(".")
    val second = secondArr.first().toULong()
    val secondFractionString = if (secondArr.size > 1) secondArr[1].trimEnd('0') else ""
    val precision = if (secondFractionString.length % 3 == 0) {
        (secondFractionString.length / 3).toUByte()
    } else {
        (secondFractionString.length / 3 + 1).toUByte()
    }
    val secondFraction = if (secondFractionString.isBlank()) 0UL else secondFractionString.toULong()
    return TimeParts(hour, minute, second, secondFraction, precision)
}

data class TimeParts(
    val hour: ULong,
    val minute: ULong,
    val second: ULong,
    val secondFraction: ULong,
    val precision: UByte, // 0-second, 1-millisecond, 2-microsecond, 3-nanosecond, 4-picosecond
)

fun ULong.pow(exponent: UByte): ULong {
    var result = 1UL
    repeat(exponent.toInt()) {
        result *= this
    }
    return result
}

fun ULong.adaptFraction(resolutionLevel: ResolutionLevel, precision: UByte): ULong =
    this * 1000UL.pow((resolutionLevel.no - 5U - precision).toUByte())
