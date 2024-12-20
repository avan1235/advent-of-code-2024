import AdventDay.Exception
import WeightedGraph.E
import com.ionspin.kotlin.bignum.integer.BigInteger

data object Day20 : AdventDay(n = 20) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    val racetrack = lines.toRacetrack()
    racetrack.countCheats(length = 2).part1()
    racetrack.countCheats(length = 20).part2()
  }
}

private data class Racetrack(
  val graph: WeightedGraph<V2, Unit>,
  val start: V2,
  val end: V2,
  val size: V2,
  val original: Matrix2D<Char>,
  val afterAtPath: Map<V2, Set<V2>>
) {
  suspend fun countCheats(length: Int): Long {
    val originalPath = shortestPath()
    val cheats = generateCheats(length)
    return graph.nodes.parallelMap { from ->
      cheats.count { cheat ->
        val to = from + cheat.v
        if (original[to].let { it == null || it == '#' }) return@count false
        if (to !in afterAtPath[from].orEmpty()) return@count false

        val gp = LazyDefaultMap(::mutableSetOf, graph.adj.toMutableMap()).also {
          it[from] += E(to, Unit)
        }.let(::WeightedGraph)

        val sp = copy(graph = gp).shortestPath() + cheat.cost
        sp <= originalPath - Saved
      }.toLong()
    }.sum()
  }

  private fun shortestPath(): BigInteger =
    graph.shortestPath(start, end)
}

private fun List<String>.toRacetrack(): Racetrack = Matrix2D(map(String::toList)).let { m ->
  val g = LazyDefaultMap<V2, MutableSet<E<V2, Unit>>>(::mutableSetOf)
  val size = m.size2D
  val (sizeX, sizeY) = size
  var s: V2? = null
  var e: V2? = null
  for (y in 0..<sizeY) for (x in 0..sizeX) {
    val c = x xy y
    if (m[c] == '#') continue
    if (m[c] == 'S') s = c
    if (m[c] == 'E') e = c
    for (d in Dir.entries) {
      val n = c + d.v
      if (m[n].let { it == '#' || it == null }) continue
      g[c] += E(n, Unit)
    }
  }
  val afterAtPath = buildMap {
    val track = buildList {
      object : Graph<V2> {
        override val nodes = g.keys.toList().asSequence()
        override fun neighbours(node: V2) = g[node].asSequence().map { it.to }
      }.search(
        from = s!!, action = { n, dist ->
          if (this@buildList.size != dist) throw Exception("it's not a linear path on map")
          this += n
        })
    }
    if (track.last() != e!!) throw IllegalStateException("path doesn't finish at end")

    track.forEachIndexed { idx, v ->
      put(v, track.subList(idx + 1, track.size).toSet())
    }
  }
  return Racetrack(WeightedGraph(g), s!!, e!!, size, m, afterAtPath)
}

private data class Cheat(val v: V2, val cost: Int)

private fun generateCheats(length: Int): List<Cheat> = buildList {
  Dir.entries.repeat(2).take(5).zipWithNext().forEach { (d1, d2) ->
    for (l in 1..length) for (d1l in 0..<l) {
      val v = d1l * d1.v + (l - d1l) * d2.v
      add(Cheat(v, v.abs.run { x + y } - 1))
    }
  }
}

private const val Saved = 100
