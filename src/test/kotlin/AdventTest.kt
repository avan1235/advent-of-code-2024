import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import java.util.stream.Stream.of as streamOf

internal class AdventTest {

  @OptIn(ExperimentalCoroutinesApi::class)
  @ParameterizedTest
  @MethodSource("provideDaysWithExpectedOutputs")
  fun `test advent day`(day: AdventDay, part1: String, part2: String) = runBlocking(Dispatchers.Default) {
    val output = day.apply { solve() }.lines
    assertEquals(listOf(part1, part2), output, "${day::class.simpleName} output is not as expected")
  }

  companion object {
    @JvmStatic
    private fun provideDaysWithExpectedOutputs() = streamOf(
      arguments(Day1, "2904518", "18650129"),
      arguments(Day2, "486", "540"),
      arguments(Day3, "189527826", "63013756"),
      arguments(Day4, "2593", "1950"),
      arguments(Day5, "6242", "5169"),
      arguments(Day6, "4883", "1655"),
      arguments(Day7, "6392012777720", "61561126043536"),
      arguments(Day8, "381", "1184"),
      arguments(Day9, "6385338159127", "6415163624282"),
      arguments(Day10, "822", "1801"),
      arguments(Day11, "220722", "261952051690787"),
      arguments(Day12, "1549354", "937032"),
      arguments(Day13, "26299", "107824497933339"),
      arguments(Day14, "230461440", "6668"),
      arguments(Day15, "1465523", "1471049"),
      arguments(Day16, "93436", "486"),
    )
  }
}
