data object Day23 : AdventDay(n = 23) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    val N = lines.buildLANConnectionsGraph()

    N.count3CliquesWithT().part1()

    N.findMaxCliques().single().sorted().joinToString(",").part2()
  }
}

private typealias LANConnectionsGraph = LazyDefaultMap<String, MutableSet<String>>

private fun LANConnectionsGraph.findMaxCliques(): List<Set<String>> = let { N ->
  val maxCliques = mutableSetOf<Set<String>>()

  fun BronKerbosch(R: Set<String>, P: Set<String>, X: Set<String>): Unit = when {
    P.isEmpty() && X.isEmpty() -> maxCliques += R
    else -> {
      val P = P.toMutableSet()
      val X = X.toMutableSet()
      for (v in P.toSet()) {
        BronKerbosch(R + v, P intersect N[v], X intersect N[v])
        P -= v
        X += v
      }
    }
  }

  BronKerbosch(emptySet(), N.keys.toSet(), emptySet())

  val maxSize = maxCliques.maxOf { it.size }
  maxCliques.filter { it.size == maxSize }
}

private fun LANConnectionsGraph.count3CliquesWithT(): Int = let { N ->
  val counted = mutableSetOf<Set<String>>()
  N.keys.map { n0 ->
    for (n1 in N[n0]) for (n2 in N[n0]) if (n1 != n2 && n1 in N[n2]) counted += setOf(n0, n1, n2)
  }
  counted.count { it.any { it.startsWith("t") } }
}

private fun List<String>.buildLANConnectionsGraph(): LANConnectionsGraph = let { lines ->
  LazyDefaultMap<String, MutableSet<String>>(::mutableSetOf).also { N ->
    lines.forEach {
      ConnectionRegex.matchEntire(it)!!.groups.let {
        val n1 = it["n1"]!!.value
        val n2 = it["n2"]!!.value
        N[n1] += n2
        N[n2] += n1
      }
    }
  }
}

private val ConnectionRegex = Regex("""(?<n1>[a-z][a-z])-(?<n2>[a-z][a-z])""")
