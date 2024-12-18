import WeightedGraph.E
import com.ionspin.kotlin.bignum.integer.BigInteger

data object Day18 : AdventDay() {
  override suspend fun solve() {
    val lines = reads<String>()
    val positions = lines.map {
      BytePositionRegex.matchEntire(it)!!.groups.let { it["x"]!!.value.toInt() xy it["y"]!!.value.toInt() }
    }

    positions.shortestPath(n = 1024).printIt()

    for (n in positions.indices) {
      if (positions.shortestPath(n + 1) != BigInteger.fromLong(Long.MAX_VALUE)) continue
      positions[n].let { (x, y) -> "$x,$y" }.printIt()
      break
    }
  }
}

private fun List<V2>.shortestPath(n: Int): BigInteger =
  take(n)
    .toSet<V2>()
    .toWeightedGraph()
    .shortestPath<Unit>(
      source = 0 xy 0,
      destination = Size.x - 1 xy Size.y - 1,
      startDistanceContext = Unit,
      zeroDistanceContext = Unit,
      maxDistanceContext = Unit,
      cost = { qn, e -> BigInteger.ONE },
      alterContext = { _, _ -> }
    ).value

private val Size = 71 xy 71

private fun Set<V2>.toWeightedGraph(): WeightedGraph<V2, Unit> {
  val (sizeX, sizeY) = Size

  val map = LazyDefaultMap<V2, MutableList<E<V2, Unit>>>(::mutableListOf)
  val ys = 0..<sizeY
  val xs = 0..<sizeX
  for (y in ys) for (x in xs) {
    val c = x xy y
    if (c in this) continue

    for (d in Dir.entries) {
      val n = c + d.v
      if (n in this || n.x !in xs || n.y !in ys) continue

      map[c] += E(n, Unit)
    }
  }
  return WeightedGraph<V2, Unit>(map)
}

private val BytePositionRegex = Regex("""(?<x>\d+),(?<y>\d+)""")
