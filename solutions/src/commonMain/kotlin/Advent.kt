import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

abstract class Advent {
  abstract val year: Int
  abstract val days: List<AdventDay>

  fun today(now: Instant = Clock.System.now()): AdventDay? {
    val localDateTime = now.toLocalDateTime(TimeZone.of("UTC-5"))
    if (localDateTime.year != year) return null
    if (localDateTime.monthNumber != 12) return null
    return days.firstOrNull { it.n == localDateTime.dayOfMonth }
  }
}
