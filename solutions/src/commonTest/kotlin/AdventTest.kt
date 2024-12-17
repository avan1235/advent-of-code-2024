import kotlinx.coroutines.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AdventTest {

  private fun testAdventDay(day: AdventDay, part1: String, part2: String) = runBlocking<Unit>(Dispatchers.Default) {
    val output = day.apply { solve() }.lines
    assertEquals(listOf(part1, part2), output, "${day::class.simpleName} output is not as expected")
  }

  @Test fun testDay1() = testAdventDay(Day1, "2904518", "18650129")
  @Test fun testDay2() = testAdventDay(Day2, "486", "540")
  @Test fun testDay3() = testAdventDay(Day3, "189527826", "63013756")
  @Test fun testDay4() = testAdventDay(Day4, "2593", "1950")
  @Test fun testDay5() = testAdventDay(Day5, "6242", "5169")
  @Test fun testDay6() = testAdventDay(Day6, "4883", "1655")
  @Test fun testDay7() = testAdventDay(Day7, "6392012777720", "61561126043536")
  @Test fun testDay8() = testAdventDay(Day8, "381", "1184")
  @Test fun testDay9() = testAdventDay(Day9, "6385338159127", "6415163624282")
  @Test fun testDay10() = testAdventDay(Day10, "822", "1801")
  @Test fun testDay11() = testAdventDay(Day11, "220722", "261952051690787")
  @Test fun testDay12() = testAdventDay(Day12, "1549354", "937032")
  @Test fun testDay13() = testAdventDay(Day13, "26299", "107824497933339")
  @Test fun testDay14() = testAdventDay(Day14, "230461440", "6668")
  @Test fun testDay15() = testAdventDay(Day15, "1465523", "1471049")
  @Test fun testDay16() = testAdventDay(Day16, "93436", "486")
  @Test fun testDay17() = testAdventDay(Day17, "7,5,4,3,4,5,3,4,6", "164278899142333")
}
