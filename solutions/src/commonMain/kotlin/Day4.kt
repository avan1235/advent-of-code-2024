data object Day4 : AdventDay(n = 4) {
  override suspend fun solve(lines: List<String>) {
    val map = lines.toMap()

    map.countXmas().printIt()
    map.countXMAS().printIt()
  }
}

private fun List<String>.toMap(): DefaultMap<V2, Char> = flatMapIndexed { y, line ->
  line.mapIndexed { x, c ->
    x xy y to c
  }
}.toMap().toDefaultMap('.')

private fun DefaultMap<V2, Char>.countXmas(): Int = let { map ->
  val directions = listOf(
    1 xy 0,
    -1 xy 0,
    0 xy 1,
    0 xy -1,
    1 xy 1,
    1 xy -1,
    -1 xy 1,
    -1 xy -1,
  )
  map.keys.toList().sumOf { curr ->
    directions.count { d ->
      "XMAS".withIndex().all { (i, c) -> map[curr + i * d] == c }
    }
  }
}

private fun DefaultMap<V2, Char>.countXMAS(): Int = let { map ->
  val directions = listOf(
    1 xy 1,
    1 xy -1,
    -1 xy -1,
    -1 xy 1,
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
