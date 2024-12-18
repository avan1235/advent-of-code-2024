import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

abstract class AdventDay(val n: Int) : Comparable<AdventDay> {

  private val _debug = Channel<String>(capacity = UNLIMITED)
  val debug: Flow<String> get() = _debug.consumeAsFlow()

  lateinit var part1: String
    private set
  lateinit var part2: String
    private set

  protected abstract suspend fun solve(lines: List<String>)

  suspend fun solve(with: AdventInputReader) {
    try {
      val lines = with.readInput(this).trim().lines()
      solve(lines)
    } finally {
      _debug.close()
    }
  }

  suspend fun solve(input: String) = solve(with = { input })

  fun <T> T.printIt(): T = also { _debug.trySend(toString()) }

  fun <T> T.part1(): T = also { part1 = toString().printIt() }

  fun <T> T.part2(): T = also { part2 = toString().printIt() }

  override fun compareTo(other: AdventDay): Int =
    n.compareTo(other.n)
}
