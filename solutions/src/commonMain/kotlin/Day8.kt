data object Day8 : AdventDay() {
  override suspend fun solve() {
    val lines = reads<String>()

    val antennas = lines.toAntennas()
    val (sizeX, sizeY) = lines.size2D

    antennas.countAntiNodes(
      sizeX, sizeY,
      includeSelf = false,
      limit = true
    ).printIt()

    antennas.countAntiNodes(
      sizeX, sizeY,
      includeSelf = true,
      limit = false
    ).printIt()
  }
}

private fun collectAntiNodes(
  sizeX: Int,
  sizeY: Int,
  includeSelf: Boolean,
  limit: Boolean,
  start: V2,
  next: V2,
  antiNodes: MutableSet<V2>,
) = generateSequence(start) { it + (start - next) }
  .runIf(!includeSelf) { filter { it != start } }
  .takeWhile { it.x in 0..<sizeX && it.y in 0..<sizeY }
  .runIf(limit) { take(1) }
  .forEach(antiNodes::add)

private fun LazyDefaultMap<Char, MutableList<V2>>.countAntiNodes(
  sizeX: Int,
  sizeY: Int,
  includeSelf: Boolean,
  limit: Boolean,
): Int = let { antennas ->
  val antiNodes = mutableSetOf<V2>()
  antennas.forEach { (_, group) ->
    for (i in 0..group.lastIndex - 1) for (j in i + 1..group.lastIndex) {
      collectAntiNodes(sizeX, sizeY, includeSelf, limit, group[i], group[j], antiNodes)
      collectAntiNodes(sizeX, sizeY, includeSelf, limit, group[j], group[i], antiNodes)
    }
  }
  antiNodes.size
}

private fun List<String>.toAntennas(): LazyDefaultMap<Char, MutableList<V2>> = let { lines ->
  LazyDefaultMap<Char, MutableList<V2>>(
    default = { mutableListOf<V2>() }
  ).also { nodes ->
    lines.forEachIndexed { y, line ->
      line.forEachIndexed { x, c ->
        if (c != '.') nodes[c] += x xy y
      }
    }
  }
}
