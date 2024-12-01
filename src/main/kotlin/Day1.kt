import kotlin.math.abs

data object Day1 : AdventDay() {
  override fun solve() {
    val lines = reads<String>() ?: return
    val (fst, snd) = lines.map { line ->
      line.split(Regex("""\s+""")).let { (a, b) ->
        listOf(a.toLong(), b.toLong())
      }
    }.transpose()

    fst.sorted().zip(snd.sorted()).sumOf { (a, b) -> abs(a - b) }.printIt()

    val sndEachCount = snd.groupingBy { it }.eachCount().toDefaultMap(0)
    fst.sumOf { sndEachCount[it] * it }.printIt()
  }
}
