import kotlin.math.pow

data object Day7 : AdventDay(n = 7) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    val equations = lines.map { lines ->
      lines.split(": ").let { (r, p) -> r.toLong() to p.split(" ").map(String::toLong) }
    }

    equations.fold(radix = 2) { result, element, bit ->
      when (bit) {
        0 -> result + element
        1 -> result * element
        else -> error("unexpected bit: $bit")
      }
    }.part1()

    equations.fold(radix = 3) { result, element, bit ->
      when (bit) {
        0 -> result + element
        1 -> result * element
        2 -> "$result$element".toLong()
        else -> error("unexpected bit: $bit")
      }
    }.part2()
  }
}

private suspend inline fun List<Pair<Long, List<Long>>>.fold(
  radix: Int,
  crossinline f: (result: Long, element: Long, bit: Int) -> Long,
): Long = parallelMap sum@{ (result, parts) ->
  val bits = parts.size - 1

  for (mask in 0..<radix.toDouble().pow(bits).toInt()) {
    var currResult = parts[0]
    var currentMask = mask

    for (bitPos in 0..<bits) {
      val oper = currentMask % radix
      currentMask /= radix

      currResult = f(currResult, parts[bitPos + 1], oper)
    }

    if (currResult == result) return@sum result
  }
  0L
}.sum()

