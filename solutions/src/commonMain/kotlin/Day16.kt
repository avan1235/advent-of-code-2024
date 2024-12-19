import WeightedGraph.D
import com.ionspin.kotlin.bignum.integer.BigInteger

data object Day16 : AdventDay(n = 16) {
  override suspend fun SolveContext.solve(lines: List<String>) {

    val maze = lines.toReindeerMaze()
    val shortestPath = maze.shortestPath(startDirection = Dir.E)
    shortestPath.value.part1()

    maze.nodes.parallelCount { node ->
      when (node) {
        maze.start -> true
        maze.end -> true
        else -> {
          val p1 = maze.shortestPath(startDirection = Dir.E, destination = node)
          val p2 = maze.shortestPath(startDirection = p1.context!!, source = node)
          p1.value + p2.value == shortestPath.value
        }
      }
    }.part2()
  }
}

private class ReindeerMaze(
  private val graph: WeightedGraph<V2, Dir>,
  val start: V2,
  val end: V2,
) {
  val nodes: List<V2> = graph.nodes

  fun shortestPath(
    startDirection: Dir,
    source: V2 = start,
    destination: V2 = end
  ): D<Dir?> = graph.shortestPath<Dir?>(
    source = source,
    destination = destination,
    startDistanceContext = startDirection,
    zeroDistanceContext = null,
    maxDistanceContext = null,
    cost = { qn, e ->
      when (qn.distance.context!!) {
        e.context -> 1
        e.context.reversed -> 2001
        else -> 1001
      }.let(::BigInteger)
    },
    alterContext = { e, _ -> e.context },
  )
}

private fun List<String>.toReindeerMaze(): ReindeerMaze {
  val matrix = Matrix2D(map(String::toList))
  val (sizeX, sizeY) = size2D
  var start: V2? = null
  var end: V2? = null

  return buildMap {
    for (y in 0..<sizeY) for (x in 0..<sizeX) {
      val c = x xy y
      if (matrix[c] == '#') continue
      if (matrix[c] == 'S') start = c
      if (matrix[c] == 'E') end = c

      Dir.entries.forEach {
        val n = c + it.v
        if (matrix[n] != '#') getOrPut(c) { mutableListOf() }.add(WeightedGraph.E(n, it))
      }
    }
  }
    .let(::WeightedGraph)
    .let { ReindeerMaze(it, start!!, end!!) }
}
