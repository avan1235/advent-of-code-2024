import WarehouseMap.Element.BOX
import WarehouseMap.Element.WALL
import WideWarehouseMap.Element.*

data object Day15 : AdventDay(n = 15) {
  override suspend fun solve(lines: List<String>) {
    val (rawMap, rawMoves) = lines.groupSeparatedBy(separator = { it == "" }, transform = { it })

    val moves = rawMoves.joinToString("")

    moves.fold(rawMap.toWarehouseMap()) { acc, c -> acc.move(c) }
      .boxesCoordinates()
      .sumOf { (x, y) -> x + 100L * y }
      .printIt()

    moves.fold(rawMap.toWideWarehouseMap()) { acc, c -> acc.move(c) }
      .boxesCoordinates()
      .sumOf { (x, y) -> x + 100L * y }
      .printIt()
  }
}

private data class WarehouseMap(
  private val robot: V2,
  private val elements: Map<V2, Element>,
  private val size: V2,
) {
  enum class Element { BOX, WALL, }

  fun move(c: Char): WarehouseMap {
    val nextRobot = robot + c.toMove()
    return when (elements[nextRobot]) {
      null -> copy(robot = nextRobot)
      WALL -> this
      BOX -> {
        val firstFree = generateSequence(nextRobot + c.toMove()) { it + c.toMove() }
          .takeWhile { elements[it] != WALL }
          .firstOrNull { elements[it] == null }

        if (firstFree == null) this
        else copy(
          robot = nextRobot,
          elements = buildMap {
            putAll(elements)
            remove(nextRobot)
            put(firstFree, BOX)
          }
        )
      }
    }
  }

  fun boxesCoordinates(): List<V2> = elements.keys.filter { elements[it] == BOX }
}

private fun List<String>.toWarehouseMap(): WarehouseMap {
  var robot: V2? = null
  val size2D = size2D
  val elements = buildMap {
    val (sizeX, sizeY) = size2D
    for (y in 0..<sizeY) for (x in 0..<sizeX) {
      val c = this@toWarehouseMap[y][x]
      when (c) {
        '@' -> robot = x xy y
        '#' -> put(x xy y, WALL)
        'O' -> put(x xy y, BOX)
      }
    }
  }
  return WarehouseMap(robot ?: error("No robot on map"), elements, size2D)
}

private data class WideWarehouseMap(
  private val robot: V2,
  private val elements: Map<V2, Element>,
  private val size: V2,
) {
  enum class Element { LBOX, RBOX, WWALL }

  fun move(c: Char): WideWarehouseMap {
    val move = c.toMove()
    val nextRobot = robot + move
    return when (elements[nextRobot]) {
      null -> copy(robot = nextRobot)
      WWALL -> this
      LBOX, RBOX -> if (c == '<' || c == '>') {
        val firstFree = generateSequence(nextRobot + move) { it + move }
          .takeWhile { elements[it] != WWALL }
          .firstOrNull { elements[it] == null }

        if (firstFree == null) this
        else copy(
          robot = nextRobot,
          elements = buildMap {
            putAll(elements)
            remove(nextRobot)

            val sign = -move.x
            generateSequence(firstFree) { it + sign * Dir.E.v }
              .takeWhile { if (c == '<') it.x < nextRobot.x else it.x > nextRobot.x }
              .forEach { put(it, if (c == '<') LBOX else RBOX) }
            generateSequence(firstFree + sign * Dir.E.v) { it + sign * 2 * Dir.E.v }
              .takeWhile { if (c == '<') it.x < nextRobot.x else it.x > nextRobot.x }
              .forEach { put(it, if (c == '<') RBOX else LBOX) }
          }
        )
      } else {
        val movedElements = mutableListOf<Pair<V2, Element>>()
        fun canMoveTo(cord: V2): Boolean = when (val element = elements[cord]) {
          null -> true
          WWALL -> false
          LBOX, RBOX -> {
            val pairCord = if (element == LBOX) cord + Dir.E.v else cord + Dir.W.v
            val canMove = canMoveTo(cord + move) && canMoveTo(pairCord + move)
            if (canMove) {
              movedElements += cord to if (element == LBOX) LBOX else RBOX
              movedElements += pairCord to if (element == LBOX) RBOX else LBOX
            }
            canMove
          }
        }

        if (!canMoveTo(nextRobot)) this
        else copy(
          robot = nextRobot,
          elements = buildMap {
            putAll(this@WideWarehouseMap.elements)

            movedElements.forEach { (c, _) -> remove(c) }
            movedElements.forEach { (c, e) -> put(c + move, e) }
          }
        )
      }
    }
  }

  fun boxesCoordinates(): List<V2> = elements.keys.filter { elements[it] == LBOX }
}

private fun List<String>.toWideWarehouseMap(): WideWarehouseMap {
  var robot: V2? = null
  val size2D = size2D
  val (sizeX, sizeY) = size2D
  val elements = buildMap {
    for (y in 0..<sizeY) for (x in 0..<sizeX) {
      val c = this@toWideWarehouseMap[y][x]
      when (c) {
        '@' -> robot = 2 * x xy y

        '#' -> {
          put(2 * x xy y, WWALL)
          put(2 * x + 1 xy y, WWALL)
        }

        'O' -> {
          put(2 * x xy y, LBOX)
          put(2 * x + 1 xy y, RBOX)
        }
      }
    }
  }
  return WideWarehouseMap(robot ?: error("No robot on map"), elements, 2 * sizeX xy sizeY)
}
