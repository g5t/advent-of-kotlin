import kotlin.math.max
import kotlin.math.min

class D05Range(a: Long, b: Long){
    val start = min(a, b)
    val stop = max(a, b)
}
fun D05Range.intersect(other: D05Range): D05Range? {
    if (this.stop < other.start || other.stop < this.start) return null
    return D05Range(max(other.start, this.start), min(other.stop, this.stop))
}
fun D05Range.union(other: D05Range): List<D05Range> {
    if (this.intersect(other) == null) return listOf(this, other)
    return listOf(D05Range(min(this.start, other.start), max(this.stop, other.stop)))
}


class Day05Key (var _dest: Long, var _source: Long, var _len: Long)
fun Day05Key.in_to(x: Long): Boolean = this._dest <= x  && x <= (this._dest + this._len)
fun Day05Key.in_from(x: Long): Boolean = this._source <= x && x <= (this._source + this._len)
fun Day05Key.idx_to(x: Long): Long = x - this._dest
fun Day05Key.idx_from(x: Long): Long = x - this._source
fun Day05Key.from_to(x: Long): Long? = if (this.in_from(x)) this._dest + this.idx_from(x) else null
fun Day05Key.to_from(x: Long): Long? = if (this.in_to(x)) this._source + this.idx_to(x) else null
fun Day05Key.safeprint() = println("${this._dest} ${this._source} ${this._len}")

fun Day05Key.from_to(key: D05Range): List<Day05Key> {

}

fun String.to_Day05Key(): Day05Key {
    val one = this.split(" ")
    check(one.size == 3)
    return Day05Key(one[0].toLong(), one[1].toLong(), one[2].toLong())
}

class Day05Map(var items: List<Day05Key>) {
    fun from_to(from: Long): Long {
        for (k in this.items) {
            val to = k.from_to(from)
            if (to != null) return to
        }
        return from
    }
    fun to_from(to: Long): Long {
        for (k in this.items) {
            val from = k.to_from(to)
            if (from != null) return from
        }
        return to
    }
}
fun Day05Map.safeprint(): Unit {
    for (key in this.items)  key!!.safeprint()
}

fun makeDay05Map(lines: List<String>): Day05Map {
    val keys = mutableListOf<Day05Key>()
    for (line in lines) keys.add(keys.size, line.to_Day05Key())
    return Day05Map(keys)
}

fun readDay05Maps(lines: List<String>): Pair<List<Long>, Map<Pair<String, String>, Day05Map>> {
    check(lines[0].startsWith("seeds:"))
    val seeds = lines[0].split(':')[1].toLongList()
    val maps = mutableMapOf<Pair<String, String>, Day05Map>()
    var from = 1
    while (from < lines.size){
        while (from < lines.size && lines[from].isEmpty()) from += 1
        var to = from
        while (to < lines.size && lines[to].isNotEmpty()) to += 1
        if (from < lines.size) {
            check(lines[from].trim().endsWith(':'))
            val fromToNames = lines[from].split(' ' , '-')
            maps[Pair(fromToNames[0], fromToNames[2])] = makeDay05Map(lines.slice(from+1..<to))
        }
        from = to
    }
    return Pair(seeds, maps)
}

fun followPath(seed: Long, path: List<String>, maps: Map<Pair<String, String>, Day05Map>): Long {
    var current = seed
    for (i in 0..<path.size-1) {
        val step = Pair(path[i], path[i+1])
        check(maps.containsKey(step))
        check(maps[step] != null)
//        print("${step.first} $current, ")
        current = maps[step]!!.from_to(current)
    }
//    println("${path.last()} $current")
    return current
}

fun main() {
    val path = listOf("seed", "soil", "fertilizer", "water", "light", "temperature", "humidity", "location")

    fun part1(input: List<String>): Long {
        val (seeds, maps) = readDay05Maps(input)
//        println("seeds: $seeds")
//        for (key in maps.keys) {
//            println(key)
//            maps[key]!!.safeprint()
//        }
        val locations = seeds.map{followPath(it, path, maps)}
//        println("locations: $locations")
        return locations.min()
    }

    fun part2(input: List<String>): Long {
        val (seed_ranges, maps) = readDay05Maps(input)
        var closest: Long? = null
        for (i in seed_ranges.indices step 2) {
            val starting_seed = seed_ranges[i]
            val seed_range = seed_ranges[i+1]
            val best = generateSequence(seed_range) {followPath(starting_seed + it, path, maps)}.min()
            closest = min(best, closest ?: best)
        }
        return closest ?: -1L
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readTestInput("Day05")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
