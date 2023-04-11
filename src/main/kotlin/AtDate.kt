data class AtDate(
    val rangeLevel: UByte = 0U,
    val resolutionLevel: UByte = 0U,
    val zoneLevel: UByte = 0U,
    val accuracyLevel: UByte = 0U,

    val date: ULong = 0U,
    val time: ULong = 0U,
    val zone: ULong = 0U,
    val plusLeapSeconds: ULong = 0U,
    val minusLeapSeconds: ULong = 0U,
)