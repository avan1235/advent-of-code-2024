import com.ionspin.kotlin.bignum.integer.BigInteger

import `in`.procyk.adventofcode.solutions.*

data object Day21 : AdventDay(n = 21) {
  override suspend fun SolveContext.solve(lines: List<String>) {

    lines.map { it.find(robots = 2) }.sum().part1()
    lines.map { it.find(robots = 25) }.sum().part2()
  }
}

private fun String.find(robots: Int): BigInteger {
  val length = "A$this".zipWithNext().map { it.findOptimalMovesLength(robots + 1, true) }.sum()
  val code = filter { it.isDigit() }.dropWhile { it == '0' }.toInt()
  return length * code
}

private val memoized: MutableMap<CacheKey, BigInteger> = mutableMapOf()

private typealias Move = Pair<Char, Char>

private data class CacheKey(
  val move: Move,
  val start: Boolean,
  val level: Int,
)

private fun Move.findOptimalMovesLength(level: Int, start: Boolean): BigInteger = let { move ->
  if (level == 0) return BigInteger.ONE

  val key = CacheKey(move, start, level)
  memoized[key]?.let { return it }

  val possibleWays = (if (start) OptimalMovesNumericKeypad else OptimalMovesDirectionalKeypad)[move]!!

  possibleWays.minOf { way ->
    buildList { add('A'); addAll(way) }.zipWithNext().map { wayMove ->
      wayMove.findOptimalMovesLength(level - 1, false)
    }.sum()
  }.also { memoized[key] = it }
}

private val NumericKeypad: Map<Char, V2> = mapOf(
  'A' to (0 xy 0),
  '0' to (-1 xy 0),
  '1' to (-2 xy 1),
  '2' to (-1 xy 1),
  '3' to (0 xy 1),
  '4' to (-2 xy 2),
  '5' to (-1 xy 2),
  '6' to (0 xy 2),
  '7' to (-2 xy 3),
  '8' to (-1 xy 3),
  '9' to (0 xy 3),
)

private val NumericKeypadRev: Map<V2, Char> = NumericKeypad.entries.associateBy({ it.value }, { it.key })

private fun calculateOptimalMoves(
  keypad: Map<Char, V2>, keypadRev: Map<V2, Char>
) = buildMap<Move, List<List<Char>>> {
  for ((from, fromV) in keypad.entries) for ((to, toV) in keypad.entries) {
    if (from == to) {
      put(from to to, listOf(listOf('A')))
    } else {
      val move = toV - fromV
      val moveAbs = move.abs
      val moveLength = moveAbs.x + moveAbs.y

      val (singleMoveX, singleMoveXV) = when {
        move.x == 0 -> null to null
        move.x > 0 -> '>' to Dir.E.v
        else -> '<' to Dir.W.v
      }
      val (singleMoveY, singleMoveYV) = when {
        move.y == 0 -> null to null
        move.y > 0 -> '^' to Dir.N.v
        else -> 'v' to Dir.S.v
      }

      fun go(collected: List<Char>, currentV: V2, collectedX: Int, collectedY: Int): List<List<Char>> {
        if (collected.size == moveLength) return listOf(collected + 'A')
        else {
          val result = mutableListOf<List<Char>>()
          if (collectedX < moveAbs.x && singleMoveXV != null && singleMoveX != null) {
            val nextV = currentV + singleMoveXV
            if (nextV in keypadRev) {
              result += go(collected + singleMoveX, nextV, collectedX + 1, collectedY)
            }
          }
          if (collectedY < moveAbs.y && singleMoveYV != null && singleMoveY != null) {
            val nextV = currentV + singleMoveYV
            if (nextV in keypadRev) {
              result += go(collected + singleMoveY, nextV, collectedX, collectedY + 1)
            }
          }
          return result
        }
      }

      put(from to to, go(emptyList(), fromV, 0, 0))
    }
  }
}

private val OptimalMovesNumericKeypad: Map<Move, List<List<Char>>> =
  calculateOptimalMoves(NumericKeypad, NumericKeypadRev)

private val DirectionalKeypad: Map<Char, V2> = mapOf(
  'A' to (0 xy 0),
  '^' to (-1 xy 0),
  '<' to (-2 xy -1),
  'v' to (-1 xy -1),
  '>' to (0 xy -1),
)

private val DirectionalKeypadRev: Map<V2, Char> =
  DirectionalKeypad.entries.associateBy({ it.value }, { it.key })

private val OptimalMovesDirectionalKeypad: Map<Move, List<List<Char>>> =
  calculateOptimalMoves(DirectionalKeypad, DirectionalKeypadRev)
