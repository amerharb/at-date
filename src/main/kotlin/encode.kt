import java.text.SimpleDateFormat
import java.util.*

fun encode(input: String): AtDate {
    // takes input like "@2019-01-01T00:00:00Z {d:1, r:0, a:s, l:0-0}@"
    // and returns an AtDate object
    val datetime = input.substringAfter("@").substringBefore(" ")
    val prop = input.substringAfter("{").substringBefore("}")
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

    val rValue = propArr.find { it.startsWith("t:") }?.substringAfter(":")
    val r = rValue?.toUInt() ?: 0U
    val resolutionLevel = when (r) {
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

    val dateString = datetime.substringBefore("T")
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    val date = formatter.parse(dateString)
    val calendar = Calendar.getInstance()
    calendar.time = date
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val jdn = getJDN(year, month, day)
    val dateULong = when (rangeLevel) {
        RangeLevel.Level0 -> TODO()
        RangeLevel.Level1 -> {
            // only most right 15 bits are used
            jdn.toULong() and 0b01111111_11111111UL
        }
        RangeLevel.Level2 -> TODO()
        RangeLevel.Level3 -> TODO()
        RangeLevel.Level4 -> TODO()
    }

    // read time iso format after T and before Z, + or -
    val timeString = when {
        datetime.endsWith("Z") -> datetime.substringAfter("T").substringBeforeLast("Z")
        datetime.contains("+") -> datetime.substringAfter("T").substringBeforeLast("+")
        datetime.contains("-") -> datetime.substringAfter("T").substringBeforeLast("-")
        else -> datetime.substringAfter("T")
    }
    val timeArray = timeString.split(":")
    val hour = timeArray[0].toULong()
    val minute = timeArray[1].toULong()
    val second = timeArray[2].toDouble()
    val timeULong = when (resolutionLevel) {
        ResolutionLevel.Level0 -> 0UL
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
    // read offset part of iso format

    val (zoneLevel, zoneULong) = when {
        datetime.endsWith("Z") -> Pair(ZoneLevel.Level0, 0UL)
        datetime.contains("+") -> {
            val offset = datetime.substringAfterLast("+").substringBeforeLast("@").split(":")
            val z = offset[0].toByte() * 4 + offset[1].toByte() / 15
            Pair(ZoneLevel.Level1, (z and 0b00111111).toULong())
        }
        datetime.contains("-") -> {
            val offset = datetime.substringAfterLast("-").substringBeforeLast("@").split(":")
            val z = offset[0].toByte() * 4 + offset[1].toByte() / 15
            val z6bits = z and 0b00111111
            Pair(ZoneLevel.Level1, (z6bits or 0b01000000).toULong())
        }
        else -> Pair(ZoneLevel.Level0, 0UL)
    }

    return AtDate(
        rangeLevel = rangeLevel,
        resolutionLevel = resolutionLevel,
        zoneLevel = zoneLevel,
        accuracy = accuracy,
        date = dateULong,
        time = timeULong,
        zone = zoneULong,
        plusLeapSeconds = pl,
        minusLeapSeconds = ml,
    )
}

fun getJDN(year: Int, month: Int, day: Int): Int {
    val a = (14 - month) / 12
    val y = year + 4800 - a
    val m = month + 12 * a - 3
    return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32046
}