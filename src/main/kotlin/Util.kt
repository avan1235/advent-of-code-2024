import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

inline fun <T : Any> runIf(c: Boolean, action: () -> T): T? = if (c) action() else null

inline fun <reified T> String.value(): T = when (T::class) {
  String::class -> this as T
  Long::class -> toLongOrNull() as T
  Int::class -> toIntOrNull() as T
  else -> TODO("Add support to read ${T::class.java.simpleName}")
}

inline fun <reified T> String.separated(by: String): List<T> = split(by).map { it.value() }

fun <U, V> List<U>.groupSeparatedBy(
  separator: (U) -> Boolean,
  includeSeparator: Boolean = false,
  transform: (List<U>) -> V,
): List<V> = sequence {
  var curr = mutableListOf<U>()
  forEach {
    if (separator(it) && curr.isNotEmpty()) yield(transform(curr))
    if (separator(it)) curr = if (includeSeparator) mutableListOf(it) else mutableListOf()
    else curr += it
  }
  if (curr.isNotEmpty()) yield(transform(curr))
}.toList()

fun <T> List<List<T>>.transpose(): List<List<T>> {
  val n = map { it.size }.toSet().singleOrNull()
    ?: throw IllegalArgumentException("Invalid data to transpose: $this")
  return List(n) { y -> List(size) { x -> this[x][y] } }
}

infix fun Int.directedTo(o: Int) = if (this <= o) this..o else this downTo o

class DefaultMap<K, V>(
  private val default: V,
  private val map: MutableMap<K, V> = HashMap(),
) : MutableMap<K, V> by map {
  override fun get(key: K): V = map.getOrDefault(key, default).also { map[key] = it }
  operator fun plus(kv: Pair<K, V>): DefaultMap<K, V> = (map + kv).toDefaultMap(default)
  override fun toString() = map.toString()
  override fun hashCode() = map.hashCode()
  override fun equals(other: Any?) = map == other
}

fun <K, V> Map<K, V>.toDefaultMap(default: V) = DefaultMap(default, toMutableMap())

class LazyDefaultMap<K, V>(
  private val default: () -> V,
  private val map: MutableMap<K, V> = HashMap(),
) : MutableMap<K, V> by map {
  override fun get(key: K): V = map.getOrDefault(key, default()).also { map[key] = it }
  operator fun plus(kv: Pair<K, V>): LazyDefaultMap<K, V> = (map + kv).toLazyDefaultMap(default)
  override fun toString() = map.toString()
  override fun hashCode() = map.hashCode()
  override fun equals(other: Any?) = map == other
}

fun <K, V> Map<K, V>.toLazyDefaultMap(default: () -> V) = LazyDefaultMap(default, toMutableMap())

interface Graph<Node> {
  enum class SearchType { DFS, BFS }

  val nodes: Sequence<Node>

  fun neighbours(node: Node): Sequence<Node>

  fun search(
    from: Node,
    type: SearchType = SearchType.DFS,
    checkIfVisited: Boolean = true,
    checkIfOnQueue: Boolean = false,
    visit: (from: Node, to: Node, toDistance: Int) -> Boolean = { _, _, _ -> true },
    action: (node: Node, distance: Int) -> Unit = { _, _ -> },
  ): Set<Node> {
    data class NodeAtDistance(val node: Node, val distance: Int)

    val visited = mutableSetOf<Node>()
    val onQueue = mutableSetOf<NodeAtDistance>()
    val queue = ArrayDeque<NodeAtDistance>()
    tailrec fun go(curr: NodeAtDistance) {
      onQueue -= curr
      visited += curr.also { action(it.node, it.distance) }.node
      neighbours(curr.node).forEach {
        if (checkIfVisited && it in visited) return@forEach
        if (!visit(curr.node, it, curr.distance + 1)) return@forEach
        val next = NodeAtDistance(it, curr.distance + 1)
        if (checkIfOnQueue && next in onQueue) return@forEach

        onQueue += next
        queue += next
      }
      when (type) {
        SearchType.DFS -> go(queue.removeLastOrNull() ?: return)
        SearchType.BFS -> go(queue.removeFirstOrNull() ?: return)
      }
    }
    return visited.also { go(NodeAtDistance(from, 0)) }
  }
}

fun <Node> Map<Node, Set<Node>>.topologicalSort(): List<Node> = let { graph ->
  val inCount = DefaultMap<Node, Int>(0)
  for ((vertex, neighbours) in graph) {
    if (vertex !in inCount) {
      inCount[vertex] = 0
    }
    for (n in neighbours) {
      inCount[n] += 1
    }
  }

  val queue = ArrayDeque<Node>()
  for ((vertex, edges) in inCount) {
    if (edges == 0) {
      queue += vertex
    }
  }

  val result = mutableListOf<Node>()

  while (true) {
    val vertex = queue.removeFirstOrNull() ?: break
    result += vertex

    for (successor in graph[vertex].orEmpty()) {
      inCount[successor] = inCount[successor] - 1
      if (inCount[successor] == 0) {
        queue += successor
      }
    }
  }

  if (result.size != inCount.size) {
    error("Cycle in graph detected, topological sort not possible")
  }

  return result
}


fun <T> List<T>.repeat(count: Int): List<T> = List(size * count) { this[it % size] }

suspend fun <T, U> Iterable<T>.parallelMap(selector: suspend (T) -> U): List<U> = coroutineScope {
  map { async { selector(it) } }.awaitAll()
}

suspend fun <T> Iterable<T>.parallelFilter(selector: suspend (T) -> Boolean): List<T> =
  parallelMap { it to selector(it) }.filter { it.second }.map { it.first }

suspend fun <T> Iterable<T>.parallelCount(selector: suspend (T) -> Boolean): Int =
  parallelMap { selector(it) }.count { it }

data class V2(val first: Int, val second: Int)

infix fun Int.xy(i: Int): V2 = V2(this, i)

operator fun V2.unaryMinus(): V2 = -first xy -second

operator fun V2.plus(other: V2): V2 = first + other.first xy second + other.second

operator fun V2.minus(other: V2): V2 = first - other.first xy second - other.second

operator fun V2.rem(v: Int): V2 = first % v xy second % v

operator fun V2.rem(v: V2): V2 = first % v.first xy second % v.second

fun V2.mod(v: Int): V2 = first.mod(v) xy second.mod(v)

fun V2.mod(v: V2): V2 = first.mod(v.first) xy second.mod(v.second)

operator fun Int.times(other: V2): V2 = other.first * this xy other.second * this

val V2.length: Long get() = first.toLong() * first.toLong() + second.toLong() * second.toLong()

val V2.abs: V2 get() = abs(first) xy abs(second)

val V2.normalized: V2 get() = first.sign * min(1, abs(first)) xy second.sign * min(1, abs(second))

fun Char.toMove(): V2 = when (this) {
  '>' -> 1 xy 0
  '<' -> -1 xy 0
  '^' -> 0 xy -1
  'v' -> 0 xy 1
  else -> error("unknown move char $this")
}

fun <T> T.runIf(condition: Boolean, f: T.() -> T): T = if (condition) f() else this

@JvmInline
value class Matrix2D<T : Any>(val data: List<List<T>>) {
  operator fun get(v: V2): T? = data.getOrNull(v.second)?.getOrNull(v.first)
}

val List<String>.size2D: V2
  get() {
    val sizeX = map2Set { it.length }.single()
    val sizeY = size
    return sizeX xy sizeY
  }

tailrec fun gcd(a: Long, b: Long): Long =
  if (b == 0L) a else gcd(b, a % b)

fun lcm(a: Long, b: Long): Long =
  a / gcd(a, b) * b

tailrec fun gcd(a: Int, b: Int): Int =
  if (b == 0) a else gcd(b, a % b)

fun lcm(a: Int, b: Int): Int =
  a / gcd(a, b) * b

tailrec fun gcd(a: BigInteger, b: BigInteger): BigInteger =
  if (b == BigInteger.ZERO) a else gcd(b, a % b)

fun lcm(a: BigInteger, b: BigInteger): BigInteger =
  a / gcd(a, b) * b

tailrec fun gcd(a: BigDecimal, b: BigDecimal): BigDecimal =
  if (b == BigDecimal.ZERO) a else gcd(b, a % b)

fun lcm(a: BigDecimal, b: BigDecimal): BigDecimal =
  a / gcd(a, b) * b

inline fun <T, R> Iterable<T>.map2Set(
  destination: MutableSet<R> = LinkedHashSet(),
  transform: (T) -> R,
): MutableSet<R> =
  destination.apply { for (item in this@map2Set) add(transform(item)) }

class WeightedGraph<N, ECtx>(
  private val adj: Map<N, List<E<N, ECtx>>>,
) {
  data class E<N, E>(val to: N, val context: E)
  data class QN<N, DC>(val n: N, val distance: D<DC>)
  data class D<DC>(val value: BigDecimal, val context: DC)

  val nodes: List<N> = adj.keys.toList()

  fun <DC> shortestPaths(
    source: N,
    startDistanceContext: DC,
    zeroDistanceContext: DC,
    maxDistanceContext: DC,
    cost: (from: QN<N, DC>, to: E<N, ECtx>) -> BigDecimal,
    alterContext: (to: E<N, ECtx>, altDistance: BigDecimal) -> DC
  ): DefaultMap<N, D<DC>> {

    val dist = DefaultMap<N, D<DC>>(D(BigDecimal.ZERO, zeroDistanceContext))
    val queue = PriorityQueue<QN<N, DC>>(compareBy(selector = { it.distance.value }))

    adj.keys.forEach { v ->
      if (v != source) dist[v] = D(BigDecimal.valueOf(Long.MAX_VALUE), maxDistanceContext)
      queue += if (v != source) QN(v, dist[v]) else QN(source, D(BigDecimal.ZERO, startDistanceContext))
    }

    while (queue.isNotEmpty()) {
      val u = queue.remove()

      if (u.distance.context == null) break

      adj[u.n]?.forEach neigh@{ edge ->
        val alt = dist[u.n].value + cost(u, edge)

        if (alt >= dist[edge.to].value) return@neigh

        val altDist = D(alt, alterContext(edge, alt))
        dist[edge.to] = altDist
        queue += QN(edge.to, altDist)
      }
    }
    return dist
  }

  fun <DC> shortestPath(
    source: N,
    destination: N,
    startDistanceContext: DC,
    zeroDistanceContext: DC,
    maxDistanceContext: DC,
    cost: (from: QN<N, DC>, to: E<N, ECtx>) -> BigDecimal,
    alterContext: (to: E<N, ECtx>, altDistance: BigDecimal) -> DC,
  ): D<DC> = shortestPaths(
    source,
    startDistanceContext,
    zeroDistanceContext,
    maxDistanceContext,
    cost,
    alterContext
  )[destination]
}

enum class Dir(val v: V2) {
  N(0 xy -1), E(1 xy 0), S(0 xy 1), W(-1 xy 0);

  val reversed: Dir
    get() = when (this) {
      N -> S; E -> W; S -> N; W -> E
    }
}
