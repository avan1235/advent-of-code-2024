import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun main() {
  val days = AdventDay.all
  coroutineScope {
    days.map { async { it.solve() } }.awaitAll()
  }
  days.forEach {
    println("--- ${it::class.java.simpleName}")
    it.lines.forEach(::println)
  }
}
