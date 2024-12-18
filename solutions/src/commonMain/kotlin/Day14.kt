import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

data object Day14 : AdventDay() {
  override suspend fun solve() {
    val lines = reads<String>()
    val robots = lines.map { it.toRobot() }

    val counts = longArrayOf(0, 0, 0, 0)
    val (mx, my) = MapSize
    val after100 = (1..100).fold(robots) { acc, _ -> acc.move() }
    for (robot in after100) robot.p.let { (x, y) ->
      when {
        x < mx / 2 && y < my / 2 -> counts[0]++
        x < mx / 2 && y > my / 2 -> counts[1]++
        x > mx / 2 && y < my / 2 -> counts[2]++
        x > mx / 2 && y > my / 2 -> counts[3]++
      }
    }
    counts.fold(1L) { acc, v -> acc * v }.printIt()

    var curr = robots.groupBy { it.p }
    var idx = 0
    find@ while (true) {
      if (curr.matchesExpectedImage()) {
        idx.printIt()
        break@find
      }
      curr = curr.move()
      idx += 1
    }
  }
}

private typealias Robots = Map<V2, List<Robot>>

private suspend fun Robots.matchesExpectedImage(): Boolean {
  val (sizeX, sizeY) = ExpectedImageSize
  return coroutineScope {
    buildList {
      for (x in 0..<MapSize.x - sizeX) for (y in 0..<MapSize.y - sizeY) async {
        var match = true
        check@ for (yy in 0..<sizeY) for (xx in 0..<sizeX) {
          val robots = this@matchesExpectedImage[x + xx xy y + yy] ?: emptyList()
          val actualPixel = if (robots.isNotEmpty()) 1 else 0
          if (actualPixel != ExpectedImage[yy][xx].digitToInt()) {
            match = false
            break@check
          }
        }
        match
      }.let(::add)
    }.awaitAll().any { it }
  }
}

private fun List<Robot>.move(): List<Robot> = map { it.move() }

private fun Robots.move(): Robots = buildMap<V2, MutableList<Robot>> {
  for (group in this@move.values) for (robot in group) {
    val moved = robot.move()
    getOrPut(moved.p, ::mutableListOf).add(moved)
  }
}

private val MapSize = V2(101, 103)

private data class Robot(val p: V2, val v: V2) {
  fun move(): Robot = Robot((p + v).mod(MapSize), v)
}

private fun String.toRobot(): Robot = RobotRegex.matchEntire(this)!!.groups.let {
  Robot(
    p = it["x"]!!.value.toInt() xy it["y"]!!.value.toInt(), v = it["vx"]!!.value.toInt() xy it["vy"]!!.value.toInt()
  )
}

private val RobotRegex = Regex("""p=(?<x>-?\d+),(?<y>-?\d+) v=(?<vx>-?\d+),(?<vy>-?\d+)""")

private val ExpectedImage = listOf(
  "1111111111111111111111111111111",
  "1000000000000000000000000000001",
  "1000000000000000000000000000001",
  "1000000000000000000000000000001",
  "1000000000000000000000000000001",
  "1000000000000001000000000000001",
  "1000000000000011100000000000001",
  "1000000000000111110000000000001",
  "1000000000001111111000000000001",
  "1000000000011111111100000000001",
  "1000000000000111110000000000001",
  "1000000000001111111000000000001",
  "1000000000011111111100000000001",
  "1000000000111111111110000000001",
  "1000000001111111111111000000001",
  "1000000000011111111100000000001",
  "1000000000111111111110000000001",
  "1000000001111111111111000000001",
  "1000000011111111111111100000001",
  "1000000111111111111111110000001",
  "1000000001111111111111000000001",
  "1000000011111111111111100000001",
  "1000000111111111111111110000001",
  "1000001111111111111111111000001",
  "1000011111111111111111111100001",
  "1000000000000011100000000000001",
  "1000000000000011100000000000001",
  "1000000000000011100000000000001",
  "1000000000000000000000000000001",
  "1000000000000000000000000000001",
  "1000000000000000000000000000001",
  "1000000000000000000000000000001",
  "1111111111111111111111111111111",
)

private val ExpectedImageSize = ExpectedImage.size2D
