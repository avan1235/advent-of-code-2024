import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

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

    var curr = robots
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

private suspend fun List<Robot>.matchesExpectedImage(): Boolean {
  val image = Array(MapSize.second) { ByteArray(MapSize.first) }
  for (y in 0..<MapSize.second) for (x in 0..<MapSize.first) if (any { it.p == x xy y }) image[y][x] = 1

  val (sizeX, sizeY) = ExpectedImageSize
  return coroutineScope {
    buildList {
      for (x in 0..<MapSize.first - sizeX) for (y in 0..<MapSize.second - sizeY) async {
        var match = true
        check@ for (yy in 0..<sizeY) for (xx in 0..<sizeX) {
          if (image[y + yy][x + xx] != ExpectedImage[yy][xx].digitToInt().toByte()) {
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

private fun List<Robot>.draw(index: Int) {
  val image = BufferedImage(MapSize.first, MapSize.second, BufferedImage.TYPE_INT_RGB)
  val graphics = image.createGraphics()

  graphics.color = Color.WHITE
  graphics.fillRect(0, 0, MapSize.first, MapSize.second)

  graphics.color = Color.BLACK
  for (y in 0..<MapSize.second) for (x in 0..<MapSize.first) any { it.p == x to y }.let {
    if (it) graphics.drawLine(x, y, x, y)
  }
  graphics.dispose()

  val outputFile = File("$index.png")
  ImageIO.write(image, "png", outputFile)
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
