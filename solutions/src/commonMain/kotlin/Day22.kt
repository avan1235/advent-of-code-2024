import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

data object Day22 : AdventDay(n = 22) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    lines.parallelMap { it.simulate() }.sum().part1()
    lines.findMostPossibleBananas().part2()
  }
}

private suspend fun List<String>.findMostPossibleBananas(): BigInteger = let { lines ->
  val buyersQuadruples = lines.parallelMap { it.findAllQuadruples() }
  val cache = buyersQuadruples.map { buyerQuadruples ->
    buildMap {
      for ((q, bananas) in buyerQuadruples) {
        if (q !in this) put(q, bananas)
      }
    }
  }
  val possibleQuadruples = buildSet {
    for (buyerQuadruples in buyersQuadruples) for ((q, _) in buyerQuadruples) add(q)
  }
  possibleQuadruples.parallelMap { q ->
    cache.map { buyerQuadruples ->
      buyerQuadruples[q] ?: BigInteger.ZERO
    }.sum()
  }.max()
}

private fun String.simulate(): BigInteger {
  var curr = toBigInteger()
  repeat(2000) { curr = curr.process() }
  return curr
}

private data class Quadruple(val a: BigInteger, val b: BigInteger, val c: BigInteger, val d: BigInteger)

private fun String.findAllQuadruples(): List<Pair<Quadruple, BigInteger>> {
  var curr = toBigInteger()
  var mods = mutableListOf<BigInteger>(curr % 10)
  repeat(2000) {
    curr = curr.process().also { mods.add(it % 10) }
  }
  return mods.zipWithNext().map { (a, b) -> b to b - a }.windowed(4)
    .map { (a, b, c, d) -> Quadruple(a.second, b.second, c.second, d.second) to d.first }
}

@Suppress("NOTHING_TO_INLINE")
private inline fun BigInteger.mix(other: BigInteger) = this xor other

@Suppress("NOTHING_TO_INLINE")
private inline fun BigInteger.prune() = rem(16777216)

private fun BigInteger.process(): BigInteger =
  let { (it * 64).mix(it).prune() }.let { (it / 32).mix(it).prune() }.let { (it * 2048).mix(it).prune() }
