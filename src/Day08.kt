enum class D08Direction(){
    Left,
    Right,
}

fun Char.toD08Direction(): D08Direction {
    check(this == 'R' || this == 'L')
    return if (this == 'L') D08Direction.Left else  D08Direction.Right
}

class D08Directions(val list: List<D08Direction>){
    private var position = 0
    constructor(list: List<D08Direction>, pos: Int): this(list){
        this.position = pos
    }
    constructor(encoded: String): this(encoded.trim(' ').map{it.toD08Direction()})
    fun next(): D08Direction {
        val n = this.list[this.position]
        this.position = (this.position + 1) % this.list.size
        return n
    }
    fun now(): D08Direction = this.list[this.position]
    fun step(): Unit {
        this.position = (this.position + 1) % this.list.size
    }
    fun copy(): D08Directions = D08Directions(this.list, this.position)
}

class D08Key(val pos: Long?, val at: Int=0): Comparable<D08Key> {
    override fun toString(): String {
        return "($pos, $at)"
    }
    fun equals(other: D08Key?): Boolean {
        return pos == other?.pos && at == other?.at
    }
    override fun compareTo(other: D08Key): Int {
        if (pos == other.pos && at == other.at) return 0
        if (pos == null && other.pos == null) return at.compareTo(other.at)
        if (pos == null) return -1
        if (other.pos == null) return 1
        return pos.compareTo(other.pos)
    }
}
class D08Res(val key: D08Key, val dist: Long) {
    override fun toString(): String {
        return "[$key = $dist]"
    }
    val pos get() = key.pos
    val at get() = key.at
}

fun intPow(base: Int, power: Int): Int {
    var res = 1
    for (i in 0..<power) res *= base
    return res
}

fun String.toD08Key(): Long {
    var out = 0L
    for ((i, x) in this.withIndex()) out += (x - 'A') * intPow(26, i)
    return out
}

fun main() {
    fun readMap(input: List<String>): Pair<D08Directions, Map<Long, Pair<Long, Long>>> {
        val directions = D08Directions(input[0])
        val network = mutableMapOf<Long, Pair<Long, Long>>()
        for (line in input.slice(2..<input.size)) {
            val letters = line.split('=', ' ', '(', ',', ')').filter{it.isNotEmpty()}.map{it.toD08Key()}
            check(letters.size == 3)
            network[letters[0]] = Pair(letters[1], letters[2])
        }
        return Pair(directions, network)
    }

    fun part1(input: List<String>): Long {
        val (direction, network) = readMap(input)
        var pos: Long? = 0L
        val goal = "ZZZ".toD08Key()
        var distance = 0L
        while (pos != goal) {
            check(pos != null)
            pos = if (direction.next() == D08Direction.Left) network[pos]?.first else network[pos]?.second
            distance += 1L
        }
        return distance
    }

    fun notGoal(pos: Long?): Boolean {
        if (pos == null) return false
        val goal = "AAZ".toD08Key()
        return pos < goal
    }
    fun oneLoop(directions: D08Directions, network: Map<Long,Pair<Long,Long>>, start: D08Key): D08Res {
        var distance = 0L
        var pos = start.pos
        var at = start.at
        while (notGoal(pos)) {
            pos = if (directions.list[at] == D08Direction.Left) network[pos]?.first else network[pos]?.second
            at = (at + 1) % directions.list.size
            distance += 1L
        }
        return D08Res(D08Key(pos, at), distance)
    }
    fun oneStep(dirs: D08Directions, network: Map<Long, Pair<Long, Long>>, from: D08Key): D08Res {
        if (from.pos == null) return D08Res(D08Key(null, -1), 0L)
        val to = if (dirs.list[from.at] == D08Direction.Left) network[from.pos]?.first else network[from.pos]?.second
        val at = (from.at + 1) % dirs.list.size
        return D08Res(D08Key(to, at), 1L)
    }
    fun unknownStartingPoint(map: Map<D08Key, D08Res>, key: D08Key): Boolean {
        return !map.keys.any { it.equals(key) }
    }

    fun findLoops(dirs: D08Directions, network: Map<Long,Pair<Long,Long>>, start: Long): List<Long> {
        // need (from, direction-index) -> ((to, direction-index), length) map
        val startKey = D08Key(start, 0)
        var res = oneLoop(dirs, network, startKey)
        val maps = mutableMapOf(startKey to res)
        var counter = 0
        while (counter < 10 && (unknownStartingPoint(maps, res.key) && res.pos != null)){
            val next = oneStep(dirs, network, res.key)
            maps[res.key] = next
            res = oneLoop(dirs, network, next.key)
            maps[next.key] = res
            counter += 1
        }
        var next = maps[startKey]
        val distances = mutableListOf<Long>()
        var subtotal = 0L
        while (next?.pos != null && maps.isNotEmpty()) {
            if (notGoal(next.pos)) {
                subtotal += next.dist
            } else {
                // subtotal is _always_ 0 or 1?
                distances.add(distances.size, subtotal + next.dist)
                subtotal = 0
            }
            val nn = maps[next.key]
            maps.remove(next.key)
            next = nn
        }
        // repeated pairs (probably) mean we had a choice that resulted in the same path length
        val newDist = mutableListOf(distances.first())
        for (dist in distances) if (dist != newDist.last()) newDist.add(newDist.size, dist)
        return newDist
    }

    fun twoLCM(a: Long, b: Long): Long {
        val larger = if (a > b) a else b
        val maxLCM = a * b
        var lcm = larger
        while (lcm <= maxLCM) {
            if (lcm % a == 0L && lcm % b == 0L) return lcm
            lcm += larger
        }
        return maxLCM
    }

    fun listLCM(numbers: List<Long>): Long {
        var result = numbers.first()
        for (i in numbers) result = twoLCM(result, i)
        return result
    }

    fun part2(input: List<String>): Long {
        val (direction, network) = readMap(input)
        val starts = network.keys.filter{it < "AAB".toD08Key()}  // select all keys ending in A
        val distances = starts.map{findLoops(direction, network, it)}
        if (distances.all{it.size == 1}) return listLCM(distances.map{it.first()})
        println("We need to solve a system of linear equations of the form C_0 + x N_0 == C_1 + y N_1 == ... ")
        // this _would_ be very painful, thankfully the input doesn't require solving a system of linear equations
        // It would be even worse (non-linear) if there were self-intersecting loops of different sizes
        return -1L
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readTestInput("Day08a")) == 2L)
    check(part1(readTestInput("Day08b")) == 6L)
    check(part2(readTestInput("Day08c")) == 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
