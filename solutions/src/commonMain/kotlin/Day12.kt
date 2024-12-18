data object Day12 : AdventDay() {
  override suspend fun solve(lines: List<String>) {
    val m = Matrix2D(lines.map { it.toList() })

    val g = object : Graph<V2> {
      override val nodes
        get() = lines.size2D.let { (sizeX, sizeY) ->
          sequence { for (y in 0..<sizeY) for (x in 0..<sizeX) yield(x xy y) }
        }

      override fun neighbours(node: V2) = sequence {
        val currC = m[node] ?: return@sequence
        for (cv in Dir.entries) {
          val neigh = node + cv.v
          val neighC = m[neigh] ?: continue
          if (neighC != currC) continue
          yield(neigh)
        }
      }
    }

    val regionNodes = LazyDefaultMap<V2, MutableSet<V2>>(::mutableSetOf)
    for (node in g.nodes) {
      if (node in regionNodes) continue

      regionNodes[node] += node
      g.search(
        from = node,
        action = { neigh, _ -> regionNodes[node] += neigh }
      )
      for (neigh in regionNodes[node]) {
        if (neigh != node) regionNodes[neigh] += regionNodes[node]
      }
    }

    val regions = regionNodes.mapTo(mutableSetOf()) { it.value }

    regions.sumOf { region ->
      region.sumOf { node ->
        4 - g.neighbours(node).count()
      }.let { perimeter -> perimeter * region.size }
    }.printIt()


    val directions = Dir.entries.map(Dir::v).repeat(count = 2).zipWithNext().take(n = 4)
    regions.sumOf { region ->
      region.sumOf { node ->
        directions.sumOf { (n1, n3) ->
          if (
            (m[node + n1] != m[node] && m[node + n3] != m[node]) ||
            (m[node + n1] == m[node] && m[node + n3] == m[node] && m[node + n1 + n3] != m[node])
          ) 1L else 0L
        }
      }.let { sides -> sides * region.size }
    }.printIt()
  }
}
