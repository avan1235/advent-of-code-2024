import MutableCharSequenceTrie.Companion.charSequenceTrieOf
import com.ionspin.kotlin.bignum.integer.BigInteger

data object Day19 : AdventDay(n = 19) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    val (availableRaw, display) = lines.groupSeparatedBy(separator = { it == "" }) { it }
    val available = charSequenceTrieOf(*availableRaw.single().split(", ").toTypedArray())

    val matches = display.map { it.countPossibleMatches(available) }

    matches.count { it > BigInteger.ZERO }.part1()
    matches.sum().part2()
  }
}

private fun String.countPossibleMatches(available: CharSequenceTrie): BigInteger = let { pattern ->
  val canBeBuildTo = Array(pattern.length + 1) { BigInteger.ZERO }
  canBeBuildTo[0] = BigInteger.ONE

  for (i in 1..pattern.length) {
    for (existingLength in 0..<i) {
      if (canBeBuildTo[existingLength] == BigInteger.ZERO) continue
      val newPart = pattern.subSequence(existingLength, i)
      if (!available.contains(newPart)) continue
      canBeBuildTo[i] += canBeBuildTo[existingLength]
    }
  }
  return canBeBuildTo[pattern.length]
}
