import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

sealed class AdventDay(private val readFromStdIn: Boolean = false) {

  abstract suspend fun solve()

  inline fun <reified T> reads() = getInputLines()?.map { it.value<T>() }

  fun getInputLines() =
    if (readFromStdIn) generateSequence { readLine() }.toList()
    else Path("src/input/${this::class.java.simpleName}.in").toFile().readLines()

  companion object {
    val all = AdventDay::class.sealedSubclasses
      .mapNotNull { it.objectInstance }
      .sortedBy { it::class.java.simpleName.removePrefix("Day").toInt() }
  }
}
