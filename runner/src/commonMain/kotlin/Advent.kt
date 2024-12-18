import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

fun main() = runBlocking(Dispatchers.Default) {
  val now = Clock.System.now().toLocalDateTime(TimeZone.of("UTC-5"))
  val (solved, duration) = measureTimedValue {
    solveAdventDays(
      solve = { day -> now.run { monthNumber != 12 || dayOfMonth > 25 || dayOfMonth == day } }
    )
  }
  solved.forEach { (day, duration) ->
    println("--- ${day::class.simpleName} ($duration)")
    day.lines.forEach(::println)
  }
  println("Total time: $duration")
}

private suspend fun solveAdventDays(
  solve: (day: Int) -> Boolean = { true },
): List<Pair<AdventDay, Duration>> = coroutineScope {
  adventDays().mapIndexed { idx, day ->
    if (solve(idx + 1)) async { day to measureTime { day.solve() } }
    else CompletableDeferred(value = day to null)
  }.awaitAll()
}.mapNotNull { (a, d) -> d?.let { a to d } }
