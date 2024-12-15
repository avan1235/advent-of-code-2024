import kotlin.io.path.Path

sealed class AdventDay(private val readFromStdIn: Boolean = false) {

  private val _lines = mutableListOf<String>()
  val lines: List<String> get() = _lines

  abstract suspend fun solve()

  inline fun <reified T> reads() = getInputLines().map { it.value<T>() }

  fun getInputLines(): List<String> =
    if (readFromStdIn) generateSequence { readLine() }.toList()
    else this::class.java.getResource("/input/${this::class.java.simpleName}.in")
      ?.openStream()?.bufferedReader()?.readLines().orEmpty()

  fun <T> T.printIt(): T =
    also { _lines.add(it.toString()) }

  companion object {
    val all = AdventDay::class.sealedSubclasses
      .mapNotNull { it.objectInstance }
      .sortedBy { it::class.java.simpleName.removePrefix("Day").toInt() }
  }
}
