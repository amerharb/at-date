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

    val rValue = propArr.find { it.startsWith("r:") }?.substringAfter(":")
    val r = rValue?.toUByte() ?: 0U

    val aValue = propArr.find { it.startsWith("d:") }?.substringAfter(":")
    val a = when (aValue?.lowercase()) {
        "s" -> Accuracy.Start
        "w" -> Accuracy.Whole
        "e" -> Accuracy.End
        null -> Accuracy.Start
        else -> throw Exception("Invalid accuracy level")
    }

    val lValue = propArr.find { it.startsWith("l:") }?.substringAfter(":")
    val lList = lValue?.split("-")?.map { it.toULong() } ?: listOf(0U, 0U)
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
    val date4Bytes = when (rangeLevel) {
        RangeLevel.Level0 -> TODO()
        RangeLevel.Level1 -> {
            // calc the Julian Day Number of date
            // only most right 15 bits are used
            jdn.toULong() and 0b01111111_11111111UL
        }
        RangeLevel.Level2 -> TODO()
        RangeLevel.Level3 -> TODO()
        RangeLevel.Level4 -> TODO()
        else -> throw Exception("Invalid date level")
    }

    val timeString = datetime.substringAfter("T").substringBefore("Z")
    val timeArray = timeString.split(":").map { it.toInt() }
    val hour = timeArray[0]
    val minute = timeArray[1]
    val second = timeArray[2]
    // TODO: later to take into account fraction of the second

}

fun getJDN(year: Int, month: Int, day: Int): Int {
    val a = (14 - month) / 12
    val y = year + 4800 - a
    val m = month + 12 * a - 3
    return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
}