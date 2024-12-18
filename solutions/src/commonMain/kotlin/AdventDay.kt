abstract class AdventDay {

  private val _lines = mutableListOf<String>()
  val lines: List<String> get() = _lines

  abstract suspend fun solve(lines: List<String>)

  suspend fun solve(with: AdventInputReader) {
    val lines = with.readInput(this).lines().let { lines ->
      lines.filterIndexed { idx, line -> idx < lines.lastIndex || line.isNotBlank() }
    }
    solve(lines)
  }

  suspend fun solve(input: String) = solve(with = { input })

  fun <T> T.printIt(): T = also { _lines.add(it.toString()) }
}
