import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel

abstract class AdventDay(val n: Int) : Comparable<AdventDay> {

  protected abstract suspend fun SolveContext.solve(lines: List<String>)

  suspend fun solve(with: AdventInputReader): Solution =
    SolveContext().use { with(it) { solve(with) } }

  suspend fun SolveContext.solve(with: AdventInputReader): Solution {
    val lines = with.readInput(this@AdventDay).trim().lines()
    solve(lines)
    return Solution(part1 ?: NotSolvedDescription, part2 ?: NotSolvedDescription)
  }

  suspend fun solve(input: String): Solution =
    SolveContext().use { with(it) { solve(input) } }

  suspend fun SolveContext.solve(input: String): Solution =
    solve(with = { input })

  override fun compareTo(other: AdventDay): Int =
    n.compareTo(other.n)

  class Exception(override val message: String) : kotlin.Exception(message)

  data class Solution(val part1: String, val part2: String)

  @Suppress("CONTEXT_RECEIVERS_DEPRECATED")
  inner class SolveContext(
    private val _debug: Channel<String> = Channel(capacity = UNLIMITED)
  ) : AutoCloseable {
    val debug: ReceiveChannel<String> get() = _debug

    var part1: String? = null
      private set
    var part2: String? = null
      private set

    override fun close() {
      _debug.close()
    }

    suspend fun <T> T.printIt(): T = also { _debug.send(toString()) }

    suspend fun <T> T.part1(): T = also {
      val solution = toString()
      part1 = solution
      "Day $n Part 1: $solution".printIt()
    }

    suspend fun <T> T.part2(): T = also {
      val solution = toString()
      part2 = solution
      "Day $n Part 2: $solution".printIt()
    }

    fun <T : Any> T?.notNull(message: String): T =
      this ?: throw Exception(message)
  }
}

private const val NotSolvedDescription: String = "<no-solution>"
