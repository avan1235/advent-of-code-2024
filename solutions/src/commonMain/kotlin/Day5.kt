data object Day5 : AdventDay(n = 5) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    val (rawRules, rawUpdates) = lines.groupSeparatedByBlankLine()

    val rules = rawRules.map {
      it.split("|").let { (before, after) -> before.toLong() to after.toLong() }
    }
    val after = rules.groupingBy { it.first }.fold(
      initialValue = emptySet<Long>(),
      operation = { acc, (_, after) -> acc + after },
    ).toDefaultMap(emptySet())

    val updates = rawUpdates.map { it.split(",").map { it.toLong() } }

    val (valid, invalid) = updates.partition { update ->
      (0..update.lastIndex - 1).all { i ->
        (i + 1..update.lastIndex).none { j ->
          update[i] in after[update[j]]
        }
      }
    }

    valid.sumOf { it[it.size / 2] }.part1()

    invalid.sumOf { update ->
      val relevantAfter = after.filter { it.key in update }
      val fixedUpdate = relevantAfter.topologicalSort().filter { it in update }
      fixedUpdate[fixedUpdate.size / 2]
    }.part2()
  }
}
