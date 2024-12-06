import GuardMap.Direction.*

data object Day6 : AdventDay() {
  override fun solve() {
    val lines = reads<String>() ?: return

    val map = lines.toGuardMap()
    generateSequence(map) { it.moveOnMap() }
      .map { it.guard }
      .distinct()
      .count()
      .printIt()

    var count = 0
    for (y in 0..<map.ySize) for (x in 0..<map.xSize) {
      val newObstacle = x to y
      if (newObstacle in map.obstacles) continue
      if (newObstacle == map.guard) continue

      val newMap = map.copy(obstacles = map.obstacles + newObstacle)
      if (newMap.isLooping) {
        count += 1
      }
    }
    count.printIt()
  }
}
private typealias V2 = Pair<Int, Int>

private operator fun V2.plus(other: V2) = first + other.first to second + other.second

private data class GuardMap(
  val guard: V2,
  val direction: Direction,
  val obstacles: Set<V2>,
  val xSize: Int,
  val ySize: Int,
) {
  enum class Direction { U, R, D, L }

  fun moveOnMap(): GuardMap? {
    val move = when (direction) {
      U -> V2(0, -1)
      R -> V2(1, 0)
      D -> V2(0, 1)
      L -> V2(-1, 0)
    }

    val newGuard = guard + move
    if (newGuard in obstacles) {
      val newDirection = when (direction) {
        U -> R
        R -> D
        D -> L
        L -> U
      }
      return GuardMap(guard, newDirection, obstacles, xSize, ySize)
    }

    if (newGuard.first !in 0..<xSize || newGuard.second !in 0..<ySize) {
      return null
    }
    return GuardMap(newGuard, direction, obstacles, xSize, ySize)
  }

  override fun toString(): String {
    return buildString {
      for (y in 0..<ySize) {
        for (x in 0..<xSize) {
          val c = x to y
          if (c in obstacles) append('#')
          else if (c == guard) append(
            when (direction) {
              U -> '^'
              R -> '>'
              D -> 'v'
              L -> '<'
            }
          )
          else append('.')
        }
        appendLine()
      }
    }
  }

  val isLooping: Boolean
    get() {
      var curr = this
      val visited = mutableSetOf<Pair<V2, Direction>>()
      while (true) {
        val currDirection = curr.direction
        val currGuard = curr.guard

        val position = currGuard to currDirection
        if (position in visited) {
          return true
        }
        visited += position

        curr = curr.moveOnMap() ?: return false
      }
      return false
    }
}

private fun List<String>.toGuardMap(): GuardMap = let { lines ->
  val ySize = lines.size
  val xSize = lines.map { it.length }.toSet().single()
  var guardDirection: Pair<V2, GuardMap.Direction>? = null
  val obstacles = mutableSetOf<V2>()
  for (y in 0..<ySize) for (x in 0..<xSize) {
    val char = lines[y][x]
    val v = x to y
    when (char) {
      '#' -> obstacles += v
      '>' -> guardDirection = v to R
      '<' -> guardDirection = v to L
      '^' -> guardDirection = v to U
      'v' -> guardDirection = v to D
    }
  }
  val (guard, direction) = guardDirection ?: error("no guard on map")
  return GuardMap(guard, direction, obstacles, xSize, ySize)
}
