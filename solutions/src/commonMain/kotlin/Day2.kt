import `in`.procyk.adventofcode.solutions.AdventDay
import kotlin.math.abs

data object Day2 : AdventDay(n = 2) {
  override suspend fun SolveContext.solve(lines: List<String>) {

    val reports = lines.map { it.split(" ").map { string -> string.toLong() } }

    reports.count(::isReportValid).part1()

    reports.count { report ->
      isReportValid(report) || report.withEachElementRemoved().any(::isReportValid)
    }.part2()
  }
}

private fun <R> List<R>.withEachElementRemoved(): Sequence<List<R>> = sequence {
  for (removedIdx in indices) {
    yield(filterIndexed { idx, _ -> idx != removedIdx })
  }
}

private fun isReportValid(report: List<Long>): Boolean {
  val increase = report[1] >= report[0]
  for (idx in report.indices) {
    if (idx == 0) continue
    if (increase && report[idx] <= report[idx - 1]) return false
    if (!increase && report[idx] >= report[idx - 1]) return false
    if (abs(report[idx] - report[idx - 1]) < 1) return false
    if (abs(report[idx] - report[idx - 1]) > 3) return false
  }
  return true
}
