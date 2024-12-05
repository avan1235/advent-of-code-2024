import kotlin.test.Test
import kotlin.test.assertEquals

internal class AdventTest {

  @Test
  fun `test days outputs`() {
    expectedOutputs.forEachIndexed { idx, expect ->
      val out = catchSystemOut { AdventDay.all[idx].solve() }
      assertEquals(expect, out)
    }
    println("Passed tests for ${expectedOutputs.size} days")
  }

  private val expectedOutputs = listOf(
    "2904518\n18650129\n",
    "486\n540\n",
    "189527826\n63013756\n",
    "2593\n1950\n",
    "6242\n5169\n",
  )
}
