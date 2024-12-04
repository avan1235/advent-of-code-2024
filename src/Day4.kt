data object Day4 : AdventDay() {
  override fun solve() {
    val lines = reads<String>() ?: return

    val map = lines.toMap()

    map.countXmas().printIt()
    map.countXMAS().printIt()
  }
}

private fun List<String>.toMap(): DefaultMap<Pair<Int, Int>, Char> = flatMapIndexed { y, line ->
  line.mapIndexed { x, c ->
    Pair(x, y) to c
  }
}.toMap().toDefaultMap('.')

private fun DefaultMap<Pair<Int, Int>, Char>.countXmas(): Int = let { map ->
  val directions = listOf(
    1 to 0,
    -1 to 0,
    0 to 1,
    0 to -1,
    1 to 1,
    1 to -1,
    -1 to 1,
    -1 to -1,
  )
  map.keys.toList().sumOf { curr ->
    directions.count { d ->
      "XMAS".withIndex().all { (i, c) -> map[curr + i * d] == c }
    }
  }
}

private fun DefaultMap<Pair<Int, Int>, Char>.countXMAS(): Int = let { map ->
  val directions = listOf(
    1 to 1,
    1 to -1,
    -1 to -1,
    -1 to 1,
  )
  map.keys.toList().count { curr ->
    if (map[curr] != 'A') return@count false

    listOf(
      "MMSS",
      "MSSM",
      "SMMS",
      "SSMM",
    ).any { w ->
      directions.zip(w.asIterable())
        .all { (d, c) -> map[curr + d] == c }
    }
  }
}

private operator fun Int.times(other: Pair<Int, Int>) = Pair(other.first * this, other.second * this)
private operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(first + other.first, second + other.second)
