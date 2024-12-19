import kotlin.math.abs

data object Day1 : AdventDay(n = 1) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    val (fst, snd) = lines.map { line ->
      line.split(Regex("""\s+""")).let { (a, b) ->
        listOf(a.toLong(), b.toLong())
      }
    }.transpose()

    fst.sorted().zip(snd.sorted()).sumOf { (a, b) -> abs(a - b) }.part1()

    val sndEachCount = snd.groupingBy { it }.eachCount().toDefaultMap(0)
    fst.sumOf { sndEachCount[it] * it }.part2()
  }
}
