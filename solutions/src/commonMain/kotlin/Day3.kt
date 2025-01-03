import `in`.procyk.adventofcode.solutions.AdventDay

data object Day3 : AdventDay(n = 3) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    val memory = lines.joinToString("")

    sumMulInstructions(memory).part1()

    findDoRanges(memory).sumOf { sumMulInstructions(memory.substring(it)) }.part2()
  }
}

private fun findDoRanges(memory: String): List<IntRange> = buildList {
  add(0..Int.MAX_VALUE)

  val doDontRegex = Regex("""do\(\)|don't\(\)""")
  var curr = true
  for (match in doDontRegex.findAll(memory)) when {
    match.value == "do()" && !curr -> {
      add(match.range.last + 1..Int.MAX_VALUE)
      curr = true
    }

    match.value == "don't()" && curr -> {
      replaceLastIndex(match.range.first - 1)
      curr = false
    }
  }
  if (last().last == Int.MAX_VALUE) {
    replaceLastIndex(memory.lastIndex)
  }
}

private fun MutableList<IntRange>.replaceLastIndex(with: Int) {
  val last = last()
  removeAt(lastIndex)
  add(last.first..with)
}

private fun sumMulInstructions(memory: String): Long {
  val mulRegex = Regex("""mul\((\d+)\,(\d+)\)""")
  val matches = mulRegex.findAll(memory)
  return matches.sumOf { it.groupValues[1].toLong() * it.groupValues[2].toLong() }
}
