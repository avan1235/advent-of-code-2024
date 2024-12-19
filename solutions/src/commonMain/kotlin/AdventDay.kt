import AdventDay.SolveContext.Solution
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED

abstract class AdventDay(val n: Int) : Comparable<AdventDay> {
  protected abstract suspend fun SolveContext.solve(lines: List<String>)

  suspend fun solve(with: AdventInputReader): Solution =
    SolveContext().use { with(it) { solve(with) } }

  suspend fun SolveContext.solve(with: AdventInputReader): Solution {
    val lines = with.readInput(this@AdventDay).trim().lines()
    solve(lines)
    return Solution(part1, part2)
  }

  suspend fun solve(input: String): Solution =
    SolveContext().use { with(it) { solve(input) } }

  suspend fun SolveContext.solve(input: String): Solution =
    solve(with = { input })

  override fun compareTo(other: AdventDay): Int =
    n.compareTo(other.n)

  class SolveContext(
    private val debug: Channel<String> = Channel(capacity = UNLIMITED)
  ) : AutoCloseable {
    class Exception(override val message: String) : kotlin.Exception(message)

    data class Solution(val part1: String?, val part2: String?)

    var part1: String? = null
      private set
    var part2: String? = null
      private set

    override fun close() {
      debug.close()
    }

    suspend fun <T> T.printIt(): T = also { debug.send(toString()) }

    suspend fun <T> T.part1(): T = also { part1 = toString().printIt() }

    suspend fun <T> T.part2(): T = also { part2 = toString().printIt() }

    fun <T : Any> T?.notNull(message: String): T =
      this ?: throw Exception(message)
  }
}
