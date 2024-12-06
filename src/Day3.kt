data object Day3 : AdventDay() {
  override suspend fun solve() {
    val lines = reads<String>() ?: return
    val memory = lines.joinToString("")

    sumMulInstructions(memory).printIt()

    findDoRanges(memory).sumOf { sumMulInstructions(memory.substring(it)) }.printIt()
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
