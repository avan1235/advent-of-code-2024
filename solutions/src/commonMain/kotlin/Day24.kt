import com.ionspin.kotlin.bignum.integer.BigInteger

data object Day24 : AdventDay(n = 24) {
  override suspend fun SolveContext.solve(lines: List<String>) {
    val (rawInit, rawGates) = lines.groupSeparatedByBlankLine()

    val states = rawInit.toGateStates()
    val gates = rawGates.map { it.toGate() }

    simulate(gates, states).part1()
    gates.fix()!!.sorted().joinToString(",").part2()
  }
}

private fun simulate(gates: List<Gate>, states: Map<String, Boolean>): BigInteger {
  val states = states.toMutableMap()
  val graph = buildMap {
    val byLeft = gates.groupBy { it.left }
    val byRight = gates.groupBy { it.right }
    for (gate in gates) {
      if (gate.out in byLeft) getOrPut(gate) { mutableSetOf<Gate>() } += byLeft[gate.out].orEmpty()
      if (gate.out in byRight) getOrPut(gate) { mutableSetOf<Gate>() } += byRight[gate.out].orEmpty()
    }
  }
  val sorted = graph.topologicalSort()
  val notSorted = gates - sorted
  (sorted + notSorted).forEach {
    val left = states[it.left]!!
    val right = states[it.right]!!
    states[it.out] = when (it) {
      is Gate.And -> left and right
      is Gate.Or -> left or right
      is Gate.Xor -> left xor right
    }
  }
  return states.entries
    .filter { it.key.startsWith("z") }
    .sortedByDescending { it.key }
    .joinToString("") { if (it.value) "1" else "0" }
    .let { BigInteger.parseString(it, base = 2) }
}

private fun List<String>.toGateStates(): Map<String, Boolean> =
  associate { it.split(": ").let { (name, value) -> name to (value.toInt() == 1) } }

private fun List<Gate>.fix(swapped: List<String> = emptyList()): List<String>? {
  val gates = this

  val gatesByInputs = gates.groupBy { it.inputs }
  val gatesByAnyInputs = gates.groupBy { it.left } + gates.groupBy { it.right }
  val gatesByOut = gates.groupBy { it.out }

  var carry: String? = null

  for (bit in 0..44) {
    val index = bit.toString().padStart(2, '0')
    val i1 = setOf("x$index", "y$index")
    val (sum1, carry1) = gatesByInputs.findGatesXorAndBy(i1)

    if (sum1 == null || carry1 == null) {
      error("broken at index $bit")
      break
    }

    if (carry == null) {
      if (sum1 != "z$index") {
        error("invalid output")
      }
      carry = carry1
    } else {
      val i2 = setOf(carry, sum1)
      val (sum2, carry2) = gatesByInputs.findGatesXorAndBy(i2)

      if (sum2 == null || carry2 == null) {
        val expectedOut = gatesByOut["z$index"]!!.filterIsInstance<Gate.Xor>().single()
        val swappedOutput1 = (i2 - expectedOut.inputs).single()
        val swappedOutput2 = (expectedOut.inputs - i2).single()
        val swapped1 = gatesByOut[swappedOutput1]!!.single()
        val swapped2 = gatesByOut[swappedOutput2]!!.single()
        buildList {
          addAll(this@fix.filter { it != swapped1 && it != swapped2 })
          add(swapped1.copy(out = swapped2.out))
          add(swapped2.copy(out = swapped1.out))
        }.fix(swapped + listOf(swappedOutput1, swappedOutput2))?.let { return@fix it }

        error("broken at index")
      }

      val or = gatesByInputs[setOf(carry1, carry2)].orEmpty().filterIsInstance<Gate.Or>().singleOrNull() ?: run {

        gatesByAnyInputs[carry1]?.filterIsInstance<Gate.Or>()?.singleOrNull()?.let {
          val otherInput = (it.inputs - carry1).single()
          val swapped1 = gatesByOut[otherInput]!!.single()
          val swapped2 = gatesByOut[carry2]!!.single()

          buildList {
            addAll(this@fix.filter { it != swapped1 && it != swapped2 })
            add(swapped1.copy(out = swapped2.out))
            add(swapped2.copy(out = swapped1.out))
          }.fix(swapped + listOf(otherInput, carry2))?.let { return@fix it }
        }

        gatesByAnyInputs[carry2]?.filterIsInstance<Gate.Or>()?.singleOrNull()?.let {
          val otherInput = (it.inputs - carry2).single()
          val swapped1 = gatesByOut[otherInput]!!.single()
          val swapped2 = gatesByOut[carry1]!!.single()

          buildList {
            addAll(this@fix.filter { it != swapped1 && it != swapped2 })
            add(swapped1.copy(out = swapped2.out))
            add(swapped2.copy(out = swapped1.out))
          }.fix(swapped + listOf(otherInput, carry1))?.let { return@fix it }
        }

        error("")
      }
      carry = or.out

    }
  }
  return swapped
}

private fun <T> Map<T, List<Gate>>.findGatesXorAndBy(input: T): Pair<String?, String?> {
  val haGates = this[input].orEmpty<Gate>()
  val xor1 = haGates.filterIsInstance<Gate.Xor>().singleOrNull()
  val and1 = haGates.filterIsInstance<Gate.And>().singleOrNull()
  return xor1?.out to and1?.out
}

private sealed class Gate {
  abstract val left: String
  abstract val right: String
  abstract val out: String
  abstract fun copy(out: String): Gate

  val inputs: Set<String> get() = setOf(left, right)

  data class And(override val left: String, override val right: String, override val out: String) : Gate() {
    override fun copy(out: String): Gate = copy(left = left, right = right, out = out)
  }

  data class Or(override val left: String, override val right: String, override val out: String) : Gate() {
    override fun copy(out: String): Gate = copy(left = left, right = right, out = out)
  }

  data class Xor(override val left: String, override val right: String, override val out: String) : Gate() {
    override fun copy(out: String): Gate = copy(left = left, right = right, out = out)
  }
}

private fun String.toGate(): Gate {
  val match = GateRegex.matchEntire(this)!!
  val left = match.groups["left"]!!.value
  val right = match.groups["right"]!!.value
  val out = match.groups["out"]!!.value
  val type = match.groups["type"]!!.value
  return when (type) {
    "XOR" -> Gate.Xor(left, right, out)
    "OR" -> Gate.Or(left, right, out)
    "AND" -> Gate.And(left, right, out)
    else -> error("unknown type $type")
  }
}

private val GateRegex = Regex("""(?<left>...) (?<type>XOR|OR|AND) (?<right>...) -> (?<out>...)""")
