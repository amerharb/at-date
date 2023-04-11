data class AtDate(
    val rangeLevel: RangeLevel = RangeLevel.Level0,
    val resolutionLevel: UByte = 0U,
    val zoneLevel: UByte = 0U,
    val accuracyLevel: Accuracy = Accuracy.Start,

    val date: ULong = 0U,
    val time: ULong = 0U,
    val zone: ULong = 0U,
    val plusLeapSeconds: ULong = 0U,
    val minusLeapSeconds: ULong = 0U,
)

enum class Accuracy {
    Start,
    Whole,
    End,
}

enum class RangeLevel(val level: UByte) {
    Level0(0U),
    Level1(1U),
    Level2(2U),
    Level3(3U),
    Level4(4U),
}