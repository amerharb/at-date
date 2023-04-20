package com.amerharb.atdate

fun encode(input: String): Moment {
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
    val d = dValue?.toUByte()
    val providedRangeLevel = RangeLevel.values().find { it.no == d }

    val tValue = propArr.find { it.startsWith("t:") }?.substringAfter(":")
    val t = tValue?.toUByte()
    val providedResolutionLevel = ResolutionLevel.values().find { it.no == t }

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
    val z = zValue?.toUByte()
    val providedZoneLevel = ZoneLevel.values().find { it.no == z }

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
        val (hour, min, sec) = destructTimePart(timePart)
        val rlevel = providedResolutionLevel ?: getSuitableResolutionLevel(sec)

        when (rlevel) {
            ResolutionLevel.Level0 -> Pair(rlevel, null)
            ResolutionLevel.Level1 -> Pair(rlevel, hour)
            ResolutionLevel.Level2 -> Pair(rlevel, hour * 4UL + min / 15UL) // count every 15 minutes
            ResolutionLevel.Level3 -> Pair(rlevel, hour * 12UL + min / 5UL) // count every 5 minutes
            ResolutionLevel.Level4 -> Pair(rlevel, hour * 60UL + min) // count minutes
            ResolutionLevel.Level5 -> Pair(rlevel, hour * 3600UL + min * 60UL + sec.toULong())// count seconds
            ResolutionLevel.Level6 -> Pair(
                rlevel,
                hour * 3600_000UL + min * 60_000UL + (sec * 1000).toULong()
            ) // count milliseconds
            ResolutionLevel.Level7 -> Pair(
                rlevel,
                hour * 3600_000_000UL + min * 60_000_000UL + (sec * 1000_000).toULong()
            ) // count microseconds
            ResolutionLevel.Level8 -> {
                // count nanoseconds
                Pair(rlevel, hour * 3600_000_000_000UL + min * 60_000_000_000UL + (sec * 1000_000_000).toULong())
            }

            ResolutionLevel.Level9 -> {
                // count picoseconds
                Pair(
                    rlevel,
                    hour * 3600_000_000_000_000UL + min * 60_000_000_000_000UL + (sec * 1000_000_000_000).toULong()
                )
            }
            // from Level10 ULong is not enough, go be support later with more than 1 variable
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

private fun getSuitableResolutionLevel(seconds: Double): ResolutionLevel =
    ResolutionLevel.values().find { it.no == (countDigitsAfterDecimal(seconds) / 3 + 5).toUByte() }
        ?: throw Exception("Can't find suitable resolution level for $seconds")


private fun countDigitsAfterDecimal(num: Double): Int {
    val numAsString = num.toString()
    val decimalPointIndex = numAsString.indexOf('.')

    return if (decimalPointIndex != -1) {
        // Removing trailing zeros
        val stringWithoutTrailingZeros = numAsString.trimEnd('0')

        stringWithoutTrailingZeros.length - decimalPointIndex - 1
    } else {
        0
    }
}

private fun destructTimePart(timePart: String): Triple<ULong, ULong, Double> {
    val (h, m, s) = timePart.split(":")
    val hour = h.toULong()
    val minute = m.toULong()
    val second = s.toDouble()
    return Triple(hour, minute, second)
}