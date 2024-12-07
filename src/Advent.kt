import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.TreeMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

suspend fun main() {
  val (solved, duration) = measureTimedValue { solveAdventDays() }
  solved.forEach { (day, duration) ->
    println("--- ${day::class.java.simpleName} ($duration)")
    day.lines.forEach(::println)
  }
  println("Total time: $duration")
}

suspend fun solveAdventDays(): List<Pair<AdventDay, Duration>> {
  val days = AdventDay.all
  val durations = ConcurrentHashMap<Int, Duration>()
  coroutineScope {
    days.mapIndexed { idx, day -> async { durations[idx] = measureTime { day.solve() } } }.awaitAll()
  }
  return days zip TreeMap(durations).values.toList()
}
