data object Day25 : AdventDay(n = 25) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    val (locks, keys) = lines.groupSeparatedByBlankLine().partition { it.isLock }

    val locksCodes = locks.map { it.countFilled() }
    val keysCodes = keys.map { it.countFilled() }

    var count = 0
    for (lock in locksCodes) for (key in keysCodes) {
      if (key.zip(lock).all { (k, l) -> k + l < 6 }) count++
    }
    count.part1()
  }
}

private val List<String>.isLock: Boolean
  get() = first().all { it == '#' } && last().all { it == '.' }

private fun List<String>.countFilled(): List<Int> =
  map(String::toList).transpose().map { it.count { it == '#' } - 1 }
