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
    Advent2024.solve(
      solve = { day -> now.run { monthNumber != 12 || dayOfMonth > 25 || dayOfMonth == day } }
    )
  }
  solved.forEach { (day, duration) ->
    println("--- Day ${day.n} ($duration)")
    day.debug.forEach(::println)
  }
  println("Total time: $duration")
}

private suspend fun Advent.solve(
  solve: (day: Int) -> Boolean = { true },
): List<Pair<AdventDay, Duration>> = coroutineScope {
  days.map { day ->
    if (solve(day.n)) async { day to measureTime { day.solve(with = FileAdventInputReader) } }
    else CompletableDeferred(value = day to null)
  }.awaitAll()
}.mapNotNull { (a, d) -> d?.let { a to d } }
