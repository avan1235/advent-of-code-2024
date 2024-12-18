abstract class AdventDay(val n: Int) : Comparable<AdventDay> {

  private val _debug = mutableListOf<String>()
  val debug: List<String> get() = _debug

  lateinit var part1: String
    private set
  lateinit var part2: String
    private set

  abstract suspend fun solve(lines: List<String>)

  suspend fun solve(with: AdventInputReader) {
    val lines = with.readInput(this).lines().let { lines ->
      lines.filterIndexed { idx, line -> idx < lines.lastIndex || line.isNotBlank() }
    }
    solve(lines)
  }

  suspend fun solve(input: String) = solve(with = { input })

  fun <T> T.printIt(): T = also { _debug.add(it.toString()) }

  fun <T> T.part1(): T = also { part1 = it.toString() }

  fun <T> T.part2(): T = also { part2 = it.toString() }

  override fun compareTo(other: AdventDay): Int =
    n.compareTo(other.n)
}
