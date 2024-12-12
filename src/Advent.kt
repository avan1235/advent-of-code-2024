import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

suspend fun main() {
  val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
  val (solved, duration) = measureTimedValue {
    solveAdventDays(
      solve = { day -> now.run { monthNumber != 12 || dayOfMonth > 25 || dayOfMonth == day } }
    )
  }
  solved.forEach { (day, duration) ->
    println("--- ${day::class.java.simpleName} ($duration)")
    day.lines.forEach(::println)
  }
  println("Total time: $duration")
}

suspend fun solveAdventDays(
  solve: (day: Int) -> Boolean = { true },
): List<Pair<AdventDay, Duration>> {
  val days = AdventDay.all
  val durations = ConcurrentHashMap<Int, Duration>()
  coroutineScope {
    days.mapIndexed { idx, day ->
      if (solve(idx + 1)) async { durations[idx] = measureTime { day.solve() } }
      else CompletableDeferred(value = Unit)
    }.awaitAll()
  }
  return durations.toSortedMap().entries.map { days[it.key] to it.value }
}
