import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration

internal class AdventTest {

  @Test
  fun `test days outputs`() = runTest(timeout = Duration.INFINITE) {
    val days = solveAdventDays()
    expectedOutputs.forEachIndexed { idx, expect ->
      val out = days[idx].first.lines.joinToString("\n", postfix = "\n")
      assertEquals(expect, out, "Day ${idx + 1} output is not as expected")
    }
    println("Passed tests for ${expectedOutputs.size} days")
  }

  private val expectedOutputs = listOf(
    "2904518\n18650129\n",
    "486\n540\n",
    "189527826\n63013756\n",
    "2593\n1950\n",
    "6242\n5169\n",
    "4883\n1655\n",
    "6392012777720\n61561126043536\n",
    "381\n1184\n",
    "6385338159127\n6415163624282\n",
    "822\n1801\n",
    "220722\n261952051690787\n",
    "1549354\n937032\n",
    "26299\n107824497933339\n",
    "230461440\n6668\n",
  )
}
