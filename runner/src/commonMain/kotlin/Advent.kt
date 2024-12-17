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

private fun adventDays(): List<AdventDay> = listOf<AdventDay>(
  Day1,
  Day2,
  Day3,
  Day4,
  Day5,
  Day6,
  Day7,
  Day8,
  Day9,
  Day10,
  Day11,
  Day12,
  Day13,
  Day14,
  Day15,
  Day16,
  Day17,
)

private suspend fun solveAdventDays(
  solve: (day: Int) -> Boolean = { true },
): List<Pair<AdventDay, Duration>> = coroutineScope {
  adventDays().mapIndexed { idx, day ->
    if (solve(idx + 1)) async { day to measureTime { day.solve() } }
    else CompletableDeferred(value = day to null)
  }.awaitAll()
}.mapNotNull { (a, d) -> d?.let { a to d } }
