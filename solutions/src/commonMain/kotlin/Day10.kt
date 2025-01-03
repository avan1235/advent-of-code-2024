import `in`.procyk.adventofcode.solutions.*

data object Day10 : AdventDay(n = 10) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    val map = lines.toTopographicMap()

    map.sum(checkIfVisited = true).part1()
    map.sum(checkIfVisited = false).part2()
  }
}

private fun Graph<V2>.sum(checkIfVisited: Boolean): Int = nodes.sumOf { v ->
  var sum = 0
  search(
    from = v,
    checkIfVisited = checkIfVisited,
    action = { _, dist -> if (dist == 9) sum += 1 }
  )
  sum
}

private fun List<String>.toTopographicMap(): Graph<V2> = let { lines ->
  val (sizeX, sizeY) = lines.size2D
  val data = M2(lines)

  object : Graph<V2> {
    private val directions = listOf(
      1 to 0, 0 to 1, -1 to 0, 0 to -1,
    )

    override val nodes: Sequence<V2> = sequence {
      for (y in 0..<sizeY) for (x in 0..<sizeX) yield(x xy y)
    }

    override fun neighbours(node: V2) = sequence {
      val nodeHeight = data[node]?.digitToIntOrNull() ?: return@sequence
      for (c in Dir.entries) {
        val neighbour = node + c.v
        val neighbourHeight = data[neighbour]?.digitToIntOrNull() ?: continue
        if (neighbourHeight != nodeHeight + 1) continue

        yield(neighbour)
      }
    }
  }
}
