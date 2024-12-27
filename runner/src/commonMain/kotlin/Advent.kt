import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlin.time.measureTime

fun main() = Advent2024.solve()

fun Advent.solve() = runBlocking(Dispatchers.Default) {
  val today = today()
  val debug = Channel<String>()
  try {
    launch { debug.consumeAsFlow().collect(::println) }
    val duration = measureTime {
      supervisorScope {
        days.mapNotNull { day ->
          if (today == null || today == day) launch {
            val dayDebug = Channel<String>()
            launch { dayDebug.consumeAsFlow().collect(debug::send) }
            day.SolveContext(dayDebug).use { context ->
              val duration = measureTime { with(day) { context.solve(with = FileAdventInputReader) } }
              dayDebug.send("--- Day ${day.n} finished ($duration)")
            }
          } else null
        }.joinAll()
      }
    }
    debug.send("Total time: $duration")
  } finally {
    debug.close()
  }
}
