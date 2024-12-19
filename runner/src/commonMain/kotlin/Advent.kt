import AdventDay.SolveContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.measureTime

fun main() = runBlocking(Dispatchers.Default) {
  val now = Clock.System.now().toLocalDateTime(TimeZone.of("UTC-5"))
  val debug = Channel<String>(capacity = Channel.UNLIMITED)
  try {
    launch { debug.consumeAsFlow().collect(::println) }
    val duration = measureTime {
      Advent2024.solve(
        debug = debug,
        solve = { day -> now.run { monthNumber != 12 || dayOfMonth > 25 || dayOfMonth == day } },
      )
    }
    debug.send("Total time: $duration")
  } finally {
    debug.close()
  }
}

private suspend fun Advent.solve(
  debug: Channel<String>,
  solve: (day: Int) -> Boolean = { true },
): Unit = supervisorScope {
  days.map { day ->
    if (solve(day.n)) launch {
      val dayDebug = Channel<String>(capacity = Channel.UNLIMITED)
      launch { dayDebug.consumeAsFlow().collect(debug::send) }
      day.SolveContext(dayDebug).use { context ->
        val duration = measureTime { with(day) { context.solve(with = FileAdventInputReader) } }
        dayDebug.send("--- Day ${day.n} finished ($duration)")
      }
    } else Job().apply { complete() }
  }.joinAll()
}
