import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun main() {
  solveAdventDays().forEach {
    println("--- ${it::class.java.simpleName}")
    it.lines.forEach(::println)
  }
}

suspend fun solveAdventDays(): List<AdventDay> {
  val days = AdventDay.all
  coroutineScope {
    days.map { async { it.solve() } }.awaitAll()
  }
  return days
}
