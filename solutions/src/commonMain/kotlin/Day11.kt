import StoneState.Computed
import StoneState.InProgress
import `in`.procyk.adventofcode.solutions.AdventDay
import `in`.procyk.adventofcode.solutions.parallelMap
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

data object Day11 : AdventDay(n = 11) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    val line = lines.single()
    val stones = line.split(" ").map { it.toLong() }

    naiveBlink(stones, count = 25).second.size.part1()
    blink(stones, count = 75, cachedCount = 42).part2()
  }
}

private suspend fun blink(stones: List<Long>, count: Int, cachedCount: Int): Long {
  val (cache, computingStones) = coroutineScope {
    val cache = async {
      (0L..9L).parallelMap {
        val blinkResult = naiveBlink(listOf(it), count = cachedCount)
        blinkResult.first
      }
    }
    val computingStones = async {
      val naiveCount = count - cachedCount
      var computingStones = naiveBlink(stones, count = naiveCount).second.map {
        if (it.numberOfDigits() == 1) Computed(it, at = naiveCount - 1) else InProgress(it)
      }
      for (i in 0..<cachedCount) {
        val at = naiveCount + i
        computingStones = buildList {
          computingStones.forEach update@{
            if (it is Computed) {
              add(it)
              return@update
            }
            if (it.number == 0L) {
              add(Computed(1L, at = at))
              return@update
            }
            val nd = it.number.numberOfDigits()
            if (nd % 2 == 0) {
              val base = 10.0.pow(nd / 2).toLong()
              if (nd == 2) {
                add(Computed(it.number / base, at = at))
                add(Computed(it.number % base, at = at))
              } else {
                add(InProgress(it.number / base))
                add(InProgress(it.number % base))
              }

              return@update
            }
            add(InProgress(it.number * 2024L))
          }
        }
      }
      computingStones
    }
    cache.await() to computingStones.await()
  }

  return computingStones.sumOf {
    when (it) {
      is InProgress -> 1L
      is Computed -> cache[it.number.toInt()][count - it.at - 1].toLong()
    }
  }
}

private sealed class StoneState {
  abstract val number: Long

  data class Computed(override val number: Long, val at: Int) : StoneState()
  data class InProgress(override val number: Long) : StoneState()
}

private fun naiveBlink(stones: List<Long>, count: Int): Pair<List<Int>, List<Long>> {
  val counts = mutableListOf<Int>(stones.size)
  var currStones = stones

  repeat(count) {
    currStones = buildList {
      currStones.forEach update@{ idx ->
        if (idx == 0L) {
          add(1L)
          return@update
        }
        val nd = idx.numberOfDigits()
        if (nd % 2 == 0) {
          val base = 10.0.pow(nd / 2).toLong()
          add(idx / base)
          add(idx % base)
          return@update
        }

        add(idx * 2024L)
      }
    }.also { counts += it.size }
  }
  return counts to currStones
}

private fun Long.numberOfDigits(): Int =
  floor(log10(toDouble()) + 1).toInt()
