import WeightedGraph.D
import java.math.BigDecimal

data object Day16 : AdventDay() {
  override suspend fun solve() {
    val lines = reads<String>()

    val maze = lines.toReindeerMaze()
    val shortestPath = maze.shortestPath(startDirection = Direction.E)
    shortestPath.value.printIt()

    maze.nodes.parallelCount { node ->
      when (node) {
        maze.start -> true
        maze.end -> true
        else -> {
          val p1 = maze.shortestPath(startDirection = Direction.E, destination = node)
          val p2 = maze.shortestPath(startDirection = p1.context!!, source = node)
          p1.value + p2.value == shortestPath.value
        }
      }
    }.printIt()
  }
}

private class ReindeerMaze(
  private val graph: WeightedGraph<V2, Direction>,
  val start: V2,
  val end: V2,
) {
  val nodes: List<V2> = graph.nodes

  fun shortestPath(
    startDirection: Direction,
    source: V2 = start,
    destination: V2 = end
  ): D<Direction?> = graph.shortestPath<Direction?>(
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
      }.let(::BigDecimal)
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
      val c = x to y
      if (matrix[c] == '#') continue
      if (matrix[c] == 'S') start = c
      if (matrix[c] == 'E') end = c

      Direction.entries.forEach {
        val n = c + it.v
        if (matrix[n] != '#') getOrPut(c) { mutableListOf() }.add(WeightedGraph.E(n, it))
      }
    }
  }
    .let(::WeightedGraph)
    .let { ReindeerMaze(it, start!!, end!!) }
}

private enum class Direction(val v: V2) {
  N(0 to -1), S(0 to 1), E(1 to 0), W(-1 to 0);

  val reversed: Direction
    get() = when (this) {
      N -> S; E -> W; S -> N; W -> E
    }
}
