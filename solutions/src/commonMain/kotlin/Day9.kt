data object Day9 : AdventDay(n = 9) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    val line = lines.single()

    val files = line.mapIndexedNotNull { idx, c -> if (idx % 2 == 0) c.digitToInt() else null }
    val free = line.mapIndexedNotNull { idx, c -> if (idx % 2 == 1) c.digitToInt() else null }

    compactedChecksum(files, free).part1()
    compactedNoFragmentationChecksum(files, free).part2()
  }
}

private fun List<Int>.checksum(): Long =
  mapIndexed { idx, fileIdx -> idx.toLong() * fileIdx.toLong() }
    .filter { it > 0 }
    .sum()

private fun List<Int>.findFirstFreeSpace(requiredSize: Int): Int? {
  for (i in 0..<size - requiredSize) {
    var valid = true
    for (j in i..<i + requiredSize) {
      if (this[j] != -1) {
        valid = false
        break
      }
    }
    if (valid) return i
  }
  return null
}

private fun compactedNoFragmentationChecksum(files: List<Int>, free: List<Int>): Long {
  val possiblyOccupiedSpace = files.sum() + free.sum()
  val disk = MutableList<Int>(size = possiblyOccupiedSpace) { -1 }

  files.first().let { size ->
    for (i in 0..<size) disk[i] = 0
  }

  val fileStartPosition = DefaultMap<Int, Int>(default = -1)
  fileStartPosition[0] = 0

  var diskIdx = files.first()
  outer@ for ((fileIdx, sizes) in (free.zip(files.drop(1))).withIndex()) {
    val (freeBeforeSize, fileSize) = sizes

    diskIdx += freeBeforeSize

    fileStartPosition[fileIdx + 1] = diskIdx

    for (s in 1..fileSize) {
      if (diskIdx !in disk.indices) break@outer
      disk[diskIdx++] = fileIdx + 1
    }
  }

  for (fileIdx in files.indices.reversed()) {
    val fileSize = files[fileIdx]
    val putIdx = disk.findFirstFreeSpace(requiredSize = fileSize) ?: continue
    if (putIdx >= fileStartPosition[fileIdx]) continue

    for (i in 0..<fileSize) {
      disk[i + putIdx] = fileIdx
      disk[i + fileStartPosition[fileIdx]] = -1
    }
  }
  return disk.checksum()
}

private fun compactedChecksum(files: List<Int>, free: List<Int>): Long {
  val occupiedSpace = files.sum()
  val disk = MutableList<Int>(size = occupiedSpace) { -1 }

  files.first().let { size ->
    for (i in 0..<size) disk[i] = 0
  }

  var diskIdx = files.first()
  outer@ for ((fileIdx, sizes) in (free.zip(files.drop(1))).withIndex()) {
    val (freeBeforeSize, fileSize) = sizes

    diskIdx += freeBeforeSize

    for (s in 1..fileSize) {
      if (diskIdx !in disk.indices) break@outer
      disk[diskIdx++] = fileIdx + 1
    }
  }

  diskIdx = 0
  outer@ for ((fileIdx, fileSize) in files.withIndex().reversed()) {
    for (s in 1..fileSize) {
      if (diskIdx !in disk.indices) break@outer
      while (disk[diskIdx] != -1) {
        diskIdx++
        if (diskIdx !in disk.indices) break@outer
      }
      disk[diskIdx++] = fileIdx
    }
  }

  return disk.checksum()
}
