import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

data object Day17 : AdventDay(n = 17) {
  override suspend fun solve(lines: List<String>) {

    val (rawRegisters, rawProgram) = lines.groupSeparatedBy(separator = { it == "" }, transform = { it })
    val registers = rawRegisters.associate {
      RegisterRegex.matchEntire(it)!!.groups.let { it["name"]!!.value to it["value"]!!.value.toBigInteger() }
    }.toRegisters()
    val program = rawProgram.single().let {
      ProgramRegex.matchEntire(it)!!.groups["instructions"]!!.value.split(",").map(String::toInt)
    }

    runProgram(program, registers).joinToString(",").part1()

    backtrack(program, BigInteger.ZERO, 1).part2()
  }
}

private fun backtrack(program: List<Int>, answer: BigInteger, depth: Int): BigInteger? {
  if (depth > program.size) return answer
  for (t in BigInteger.ZERO..BigInteger.fromInt(7)) {
    val a = answer shl 3 or t
    val output = runProgram(
      program = program,
      registers = Registers(
        A = a,
        B = BigInteger.ZERO,
        C = BigInteger.ZERO,
      )
    )
    if (output == program.takeLast(depth)) {
      backtrack(program, a, depth + 1)?.let { return it }
    }
  }
  return null
}

private fun runProgram(program: List<Int>, registers: Registers): List<BigInteger> {
  var ip = 0
  val stdout = mutableListOf<BigInteger>()
  while (true) {
    val i = program.getOrNull(ip) ?: break
    val op = program.getOrNull(ip + 1) ?: break

    fun cop(): BigInteger = when (op) {
      0, 1, 2, 3 -> op.toBigInteger()
      4 -> registers.A
      5 -> registers.B
      6 -> registers.C
      else -> error("unexpected combo operand $op")
    }
    when (i) {
      adv, bdv, cdv -> {
        val cop = cop()
        val num = registers.A
        val result = num shr cop.intValue(exactRequired = true)
        when (i) {
          adv -> registers.A = result
          bdv -> registers.B = result
          cdv -> registers.C = result
        }
        ip += 2
      }

      bxl -> {
        registers.B = registers.B xor op.toBigInteger()
        ip += 2
      }

      bst -> {
        val cop = cop()
        registers.B = cop.rem(8)
        ip += 2
      }

      jnz -> {
        if (registers.A == BigInteger.ZERO) {
          ip += 2
        } else {
          ip = op
        }
      }

      bxc -> {
        registers.B = registers.B xor registers.C
        ip += 2
      }

      out -> {
        val cop = cop().rem(8)
        stdout += cop
        ip += 2
      }
    }
  }
  return stdout
}

private data class Registers(var A: BigInteger, var B: BigInteger, var C: BigInteger)

private fun Map<String, BigInteger>.toRegisters(): Registers =
  Registers(this["A"]!!, this["B"]!!, this["C"]!!)

private val RegisterRegex = Regex("""Register (?<name>[A-Z]): (?<value>\d+)""")
private val ProgramRegex = Regex("""Program: (?<instructions>\d+(,\d+)*)""")

private const val adv = 0
private const val bxl = 1
private const val bst = 2
private const val jnz = 3
private const val bxc = 4
private const val out = 5
private const val bdv = 6
private const val cdv = 7
