import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.jvm.JvmInline
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

inline fun <T : Any> runIf(c: Boolean, action: () -> T): T? = if (c) action() else null

inline fun <reified T> String.value(): T = when (T::class) {
  String::class -> this as T
  Long::class -> toLongOrNull() as T
  Int::class -> toIntOrNull() as T
  else -> TODO("Add support to read ${T::class.simpleName}")
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
  val n = map { it.size }.toSet().singleOrNull() ?: throw AdventDay.Exception("Invalid data to transpose: $this")
  return List(n) { y -> List(size) { x -> this[x][y] } }
}

infix fun Int.directedTo(o: Int) = if (this <= o) this..o else this downTo o

class DefaultMap<K, V>(
  private val default: V,
  private val map: MutableMap<K, V> = HashMap(),
) : MutableMap<K, V> by map {
  override fun get(key: K): V = map[key] ?: default.also { map[key] = it }
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
  override fun get(key: K): V = map[key] ?: default().also { map[key] = it }
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

data class V2(val x: Int, val y: Int) {
  override fun toString() = "($x,$y)"
}

infix fun Int.xy(i: Int): V2 = V2(this, i)

operator fun V2.unaryMinus(): V2 = -x xy -y

operator fun V2.plus(other: V2): V2 = x + other.x xy y + other.y

operator fun V2.minus(other: V2): V2 = x - other.x xy y - other.y

operator fun V2.rem(v: Int): V2 = x % v xy y % v

operator fun V2.rem(v: V2): V2 = x % v.x xy y % v.y

fun V2.mod(v: Int): V2 = x.mod(v) xy y.mod(v)

fun V2.mod(v: V2): V2 = x.mod(v.x) xy y.mod(v.y)

operator fun Int.times(other: V2): V2 = other.x * this xy other.y * this

val V2.length: Long get() = x.toLong() * x.toLong() + y.toLong() * y.toLong()

val V2.abs: V2 get() = abs(x) xy abs(y)

val V2.normalized: V2 get() = x.sign * min(1, abs(x)) xy y.sign * min(1, abs(y))

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
  val size2D: V2
    get() {
      val sizeX = data.map2Set { it.size }.single()
      val sizeY = data.size
      return sizeX xy sizeY
    }

  operator fun get(v: V2): T? = data.getOrNull(v.y)?.getOrNull(v.x)
}

val List<String>.size2D: V2
  get() {
    val sizeX = map2Set { it.length }.single()
    val sizeY = size
    return sizeX xy sizeY
  }

tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

tailrec fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

fun lcm(a: Int, b: Int): Int = a / gcd(a, b) * b

tailrec fun gcd(a: BigInteger, b: BigInteger): BigInteger = if (b == BigInteger.ZERO) a else gcd(b, a % b)

fun lcm(a: BigInteger, b: BigInteger): BigInteger = a / gcd(a, b) * b

fun Iterable<BigInteger>.sum(): BigInteger = fold(BigInteger.ZERO) { acc, i -> acc + i }

inline fun <T, R> Iterable<T>.map2Set(
  destination: MutableSet<R> = LinkedHashSet(),
  transform: (T) -> R,
): MutableSet<R> = destination.apply { for (item in this@map2Set) add(transform(item)) }

@Suppress("NOTHING_TO_INLINE")
private inline fun <T> Array<T>.exch(i: Int, j: Int) {
  val tmp = this[i]
  this[i] = this[j]
  this[j] = tmp
}

@Suppress("UNCHECKED_CAST")
private class PriorityQueue<T>(size: Int, val comparator: Comparator<T>? = null) : Collection<T> {
  override var size: Int = 0
    private set

  private var arr: Array<T?> = Array<Any?>(size) { null } as Array<T?>

  fun add(element: T) {
    if (size + 1 == arr.size) {
      resize()
    }
    arr[++size] = element
    swim(arr, size, comparator)
  }

  fun peek(): T {
    if (isEmpty()) throw AdventDay.Exception("Priority queue is empty")
    return arr[1]!!
  }

  fun poll(): T {
    if (isEmpty()) throw AdventDay.Exception("Priority queue is empty")
    val res = peek()
    arr.exch(1, size--)
    sink(arr, 1, size, comparator)
    arr[size + 1] = null
    if (isNotEmpty() && (size == (arr.size - 1) / 4)) {
      resize()
    }
    return res
  }

  private fun resize() {
    val old = arr
    arr = Array<Any?>(size * 2) {
      (if (it < size + 1) old[it] else null) as Any?
    } as Array<T?>
  }

  override fun isEmpty(): Boolean {
    return size == 0
  }

  override fun contains(element: T): Boolean {
    for (obj in this) {
      if (obj == element) return true
    }
    return false
  }

  override fun containsAll(elements: Collection<T>): Boolean {
    for (element in elements) {
      if (!contains(element)) return false
    }
    return true
  }

  override fun iterator(): Iterator<T> = arr.copyOfRange(1, size + 1).map { it!! }.iterator()

  companion object {
    private fun <T> greater(arr: Array<T?>, i: Int, j: Int, comparator: Comparator<T>? = null): Boolean {
      if (comparator != null) {
        return comparator.compare(arr[i] as T, arr[j] as T) > 0
      } else {
        val left = arr[i]!! as Comparable<T>
        return left > arr[j]!!
      }
    }

    private fun <T> sink(arr: Array<T?>, a: Int, size: Int, comparator: Comparator<T>? = null) {
      var k = a
      while (2 * k <= size) {
        var j = 2 * k
        if (j < size && greater(arr, j, j + 1, comparator)) j++
        if (!greater(arr, k, j, comparator)) break
        arr.exch(k, j)
        k = j
      }
    }

    private fun <T> swim(arr: Array<T?>, size: Int, comparator: Comparator<T>? = null) {
      var n = size
      while (n > 1 && greater(arr, n / 2, n, comparator)) {
        arr.exch(n, n / 2)
        n /= 2
      }
    }
  }
}

fun <N> WeightedGraph<N, Unit>.shortestPaths(source: N): DefaultMap<N, WeightedGraph.D<Unit>> = shortestPaths(
  source = source,
  startDistanceContext = Unit,
  zeroDistanceContext = Unit,
  maxDistanceContext = Unit,
  cost = { _, _ -> BigInteger.ONE },
  alterContext = { _, _ -> }
)

fun <N> WeightedGraph<N, Unit>.shortestPath(source: N, destination: N): BigInteger =
  shortestPaths(source)[destination].value

class WeightedGraph<N, ECtx>(
  val adj: Map<N, Set<E<N, ECtx>>>,
) {
  data class E<N, E>(val to: N, val context: E)
  data class QN<N, DC>(val n: N, val distance: D<DC>)
  data class D<DC>(val value: BigInteger, val context: DC)

  val nodes: List<N> = adj.keys.toList()

  fun <DC> shortestPaths(
    source: N,
    startDistanceContext: DC,
    zeroDistanceContext: DC,
    maxDistanceContext: DC,
    cost: (from: QN<N, DC>, to: E<N, ECtx>) -> BigInteger,
    alterContext: (to: E<N, ECtx>, altDistance: BigInteger) -> DC
  ): DefaultMap<N, D<DC>> {

    val dist = DefaultMap<N, D<DC>>(D(BigInteger.ZERO, zeroDistanceContext))
    val queue = PriorityQueue<QN<N, DC>>(adj.keys.size, compareBy(selector = { it.distance.value }))

    adj.keys.forEach { v ->
      if (v != source) dist[v] = D(INFINITY, maxDistanceContext)
      queue.add(if (v != source) QN(v, dist[v]) else QN(source, D(BigInteger.ZERO, startDistanceContext)))
    }

    while (queue.isNotEmpty()) {
      val u = queue.poll()

      if (u.distance.context == null) break

      adj[u.n]?.forEach neigh@{ edge ->
        val alt = dist[u.n].value + cost(u, edge)

        if (alt >= dist[edge.to].value) return@neigh

        val altDist = D(alt, alterContext(edge, alt))
        dist[edge.to] = altDist
        queue.add(QN(edge.to, altDist))
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
    cost: (from: QN<N, DC>, to: E<N, ECtx>) -> BigInteger,
    alterContext: (to: E<N, ECtx>, altDistance: BigInteger) -> DC,
  ): D<DC> = shortestPaths(
    source = source,
    startDistanceContext = startDistanceContext,
    zeroDistanceContext = zeroDistanceContext,
    maxDistanceContext = maxDistanceContext,
    cost = cost,
    alterContext = alterContext
  )[destination]

  override fun toString() = adj.toString()

  companion object {
    val INFINITY: BigInteger by lazy { BigInteger.fromLong(Long.MAX_VALUE) * BigInteger.fromLong(Long.MAX_VALUE) }
  }
}

enum class Dir(val v: V2) {
  N(0 xy 1), E(1 xy 0), S(0 xy -1), W(-1 xy 0);

  val reversed: Dir
    get() = when (this) {
      N -> S; E -> W; S -> N; W -> E
    }
}

interface CharSequenceTrie {

  operator fun contains(word: CharSequence): Boolean
}

class MutableCharSequenceTrie private constructor() : CharSequenceTrie {

  private class Node {
    var hasValue: Boolean = false

    private val children: MutableMap<Int, Node> = HashMap()

    operator fun plus(key: Char): Node {
      val code = key.code
      return when (val child = children[code]) {
        null -> Node().also { children.put(code, it) }
        else -> child
      }
    }

    operator fun get(key: Char): Node? = children[key.code]
  }

  private val root: Node = Node()

  operator fun plusAssign(element: CharSequence) {
    if (element.isEmpty()) {
      root.hasValue = true
      return
    }
    element.foldIndexed(root) { idx, currNode, c ->
      (currNode + c).also { if (idx == element.lastIndex) it.hasValue = true }
    }
  }

  override operator fun contains(word: CharSequence): Boolean =
    word.fold(root) { currNode, c -> currNode[c] ?: return false }.hasValue

  companion object {
    private inline fun buildMutableCharSequenceTrie(action: MutableCharSequenceTrie.() -> Unit = {}): MutableCharSequenceTrie =
      MutableCharSequenceTrie().apply(action)

    fun charSequenceTrieOf(vararg elements: CharSequence): CharSequenceTrie =
      buildMutableCharSequenceTrie { elements.forEach { this += it } }
  }
}
