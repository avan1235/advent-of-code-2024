import kotlinx.io.buffered
import kotlinx.io.bytestring.decodeToString
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteString

abstract class AdventDay {

  private val _lines = mutableListOf<String>()
  val lines: List<String> get() = _lines

  abstract suspend fun solve()

  inline fun <reified T> reads() = getInputLines().map { it.value<T>() }

  fun getInputLines(): List<String> = SystemFileSystem.run {
    val inputPath = Path("input", "${this@AdventDay::class.simpleName}.in")
    return when {
      !exists(inputPath) -> error("Input file $inputPath does not exist")
      else -> source(inputPath).use { source ->
        source.buffered().readByteString()
      }.decodeToString().lines().let { lines ->
        lines.filterIndexed { idx, line -> idx < lines.lastIndex || line.isNotBlank() }
      }
    }
  }

  fun <T> T.printIt(): T = also { _lines.add(it.toString()) }
}
