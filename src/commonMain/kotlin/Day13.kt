import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

data object Day13 : AdventDay() {
  override suspend fun solve() {
    val lines = reads<String>()

    lines.countTokens().printIt()
    lines.countTokens { it + 10000000000000L.toBigInteger() }.printIt()
  }
}

private fun List<String>.countTokens(
  f: (BigInteger) -> BigInteger = { it }
): BigInteger = groupSeparatedBy(
  separator = { it == "" },
  transform = transform@{ (a, b, prize) ->
    val (ax, ay) = GroupRegex.extract(a)
    val (bx, by) = GroupRegex.extract(b)
    val (x, y) = PrizeRegex.extract(prize, f)

    val alcm = lcm(ax, ay)
    val fstmul = alcm / ax
    val sndmul = alcm / ay

    val resmul = fstmul * x - sndmul * y
    val divmul = fstmul * bx - sndmul * by

    if (resmul % divmul != BigInteger.ZERO) return@transform BigInteger.ZERO
    val bb = resmul / divmul

    val temp = x - bb * bx
    if (temp % ax != BigInteger.ZERO) return@transform BigInteger.ZERO
    val aa = temp / ax

    aa * 3.toBigInteger() + bb
  }
).fold(BigInteger.ZERO) { acc, v -> acc + v }

private fun Regex.extract(
  input: String,
  f: (BigInteger) -> BigInteger = { it }
): List<BigInteger> =
  matchEntire(input)!!.let {
    listOf(
      it.groups["x"],
      it.groups["y"],
    ).map {
      BigInteger.fromLong(it!!.value.toLong()).let(f)
    }
  }

private val GroupRegex = Regex("""Button [A,B]: X\+(?<x>\d+), Y\+(?<y>\d+)""")
private val PrizeRegex = Regex("""Prize: X=(?<x>\d+), Y=(?<y>\d+)""")
