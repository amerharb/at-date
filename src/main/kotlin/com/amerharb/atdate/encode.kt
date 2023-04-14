package com.amerharb.atdate

fun encode(input: String): AtDate {
    // takes input like "@2019-01-01T00:00:00Z {d:1, r:0, a:s, l:0-0}@"
    // and returns an AtDate object
    val ad = input.trim().substringAfter("@").substringBefore("@").trim()
    val datetime = ad.substringAfter("@").substringBefore(" ") // TODO: change this to regex that take "{"
    val datetimeArr = datetime.split("T")
    val datePart = datetimeArr[0]
    val timezonePart = if (datetimeArr.size > 1) datetimeArr[1] else ""
    val prop = ad.substringAfter("{").substringBefore("}")
    val propArr = prop.trim().split(",").map { it.trim() }

    val dValue = propArr.find { it.startsWith("d:") }?.substringAfter(":")
    val d = dValue?.toUInt() ?: 1U
    val rangeLevel = when (d) {
        0U -> RangeLevel.Level0
        1U -> RangeLevel.Level1
        2U -> RangeLevel.Level2
        3U -> RangeLevel.Level3
        4U -> RangeLevel.Level4
        else -> throw Exception("Invalid date level")
    }

    val tValue = propArr.find { it.startsWith("t:") }?.substringAfter(":")
    val t = tValue?.toUInt() ?: 0U
    val resolutionLevel = when (t) {
        0U -> ResolutionLevel.Level0
        1U -> ResolutionLevel.Level1
        2U -> ResolutionLevel.Level2
        3U -> ResolutionLevel.Level3
        4U -> ResolutionLevel.Level4
        5U -> ResolutionLevel.Level5
        6U -> ResolutionLevel.Level6
        7U -> ResolutionLevel.Level7
        8U -> ResolutionLevel.Level8
        9U -> ResolutionLevel.Level9
        10U -> ResolutionLevel.Level10
        11U -> ResolutionLevel.Level11
        12U -> ResolutionLevel.Level12
        13U -> ResolutionLevel.Level13
        14U -> ResolutionLevel.Level14
        15U -> ResolutionLevel.Level15
        16U -> ResolutionLevel.Level16
        17U -> ResolutionLevel.Level17
        18U -> ResolutionLevel.Level18
        19U -> ResolutionLevel.Level19
        20U -> ResolutionLevel.Level20
        else -> throw Exception("Invalid resolution level")
    }

    val aValue = propArr.find { it.startsWith("a:") }?.substringAfter(":")
    val accuracy = when (aValue?.lowercase()) {
        "s" -> Accuracy.Start
        "w" -> Accuracy.Whole
        "e" -> Accuracy.End
        null -> Accuracy.Start
        else -> throw Exception("Invalid accuracy level")
    }

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
    val providedZoneLevel = when (zValue?.toUInt()) {
        null -> null
        0U -> ZoneLevel.Level0
        1U -> ZoneLevel.Level1
        2U -> ZoneLevel.Level2
        3U -> ZoneLevel.Level3
        4U -> ZoneLevel.Level4
        5U -> ZoneLevel.Level5
        6U -> ZoneLevel.Level6
        7U -> ZoneLevel.Level7
        8U -> ZoneLevel.Level8
        9U -> ZoneLevel.Level9
        10U -> ZoneLevel.Level10
        else -> throw Exception("Invalid date level")
    }

    val (year, month, day) = datePart.split("-").map { it.toInt() }
    val jdn = getJdn(year, month, day)
    val dateULong = when (rangeLevel) {
        RangeLevel.Level0 -> TODO() // range level 0 only allowed with tp
        RangeLevel.Level1 -> {
            // only most right 15 bits are used
            jdn and 0b01111111_11111111UL
        }

        RangeLevel.Level2 -> jdn
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
    val timeULong = if (timePart.trim() != "") {
        val timeArray = timePart.split(":")
        val hour = timeArray[0].toULong()
        val minute = timeArray[1].toULong()
        val second = timeArray[2].toDouble()
        when (resolutionLevel) {
            ResolutionLevel.Level0 -> null
            ResolutionLevel.Level1 -> hour
            ResolutionLevel.Level2 -> hour * 4UL + minute / 15UL // count every 15 minutes
            ResolutionLevel.Level3 -> hour * 12UL + minute / 5UL // count every 5 minutes
            ResolutionLevel.Level4 -> hour * 60UL + minute // count minutes
            ResolutionLevel.Level5 -> hour * 3600UL + minute * 60UL + second.toULong() // count seconds
            ResolutionLevel.Level6 -> hour * 3600_000UL + minute * 60_000UL + (second * 1000).toULong() // count milliseconds
            ResolutionLevel.Level7 -> hour * 3600_000_000UL + minute * 60_000_000UL + (second * 1000_000).toULong() // count microseconds
            ResolutionLevel.Level8 -> {
                // count nanoseconds
                hour * 3600_000_000_000UL + minute * 60_000_000_000UL + (second * 1000_000_000).toULong()
            }

            ResolutionLevel.Level9 -> {
                // count picoseconds
                hour * 3600_000_000_000_000UL + minute * 60_000_000_000_000UL + (second * 1000_000_000_000).toULong()
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
        null
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

    return AtDate(
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

fun getJdn(year: Int, month: Int, day: Int): ULong {
    val p1 = (1461 * (year + 4800 + (month - 14) / 12)) / 4
    val p2 = (367 * (month - 2 - 12 * ((month - 14) / 12))) / 12
    val p3 = -(3 * ((year + 4900 + (month - 14) / 12) / 100)) / 4 + day - 32075
    return (p1 + p2 + p3).toULong()
}
