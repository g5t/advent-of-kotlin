import kotlin.math.max
import kotlin.math.min

class D05Range(a: Long, b: Long){
    val start = min(a, b)
    val stop = max(a, b)
}
fun D05Range.toMyString(): String = "(${this.start}, ${this.stop})"
fun D05Range.offset(x: Long): D05Range = D05Range(this.start + x, this.stop + x)
fun D05Range?.equals(other: D05Range?): Boolean {
    if (this == null && other == null) return true
    if (this == null) return false
    if (other == null) return false
    return this.start == other.start && this.stop == other.stop
}
fun D05Range?.equals(x: Long?): Boolean {
    if (this == null) return false
    if (x == null) return false
    return this.start == x && this.stop == x
}
fun D05Range.contains(x: Long) = this.start <= x && x <= this.stop
fun D05Range.intersect(other: D05Range): D05Range? {
    if (this.stop < other.start || other.stop < this.start) return null
    return D05Range(max(other.start, this.start), min(other.stop, this.stop))
}
fun D05Range.union(other: D05Range): List<D05Range> {
    if (this.intersect(other) == null) return listOf(this, other)
    return listOf(D05Range(min(this.start, other.start), max(this.stop, other.stop)))
}
fun D05Range.difference(other: D05Range): D05Range? {
    val int = this.intersect(other) ?: return this  // return this if no intersection
    if (int.start == this.start && int.stop == this.stop) {
        // one would think this would be caught by the == check below, but no.
        return null
    }
    if (other.start == int.start && other.stop == int.stop && other != int){
        // one would think this would be caught by the == check below, but no.
        return null
    }
    if (int == this || int == other) return null // return no difference if full-intersection
    // either int.start == this.start or int.stop == this.stop -- let the constructor sort out the other end
    return if (int.start == this.start) D05Range(int.stop, this.stop) else D05Range(int.start, this.start)
}

operator fun Long.minus(x: D05Range): Long {
    check(x.contains(this))
    return this - x.start
}
operator fun Long.plus(x: D05Range): Long {
    return this + x.start
}


class D05Key (first_dest: Long, first_source: Long, len: Long) {
    val dest = D05Range(first_dest, first_dest + len)
    val source = D05Range(first_source, first_source + len)
    val delta = first_dest - first_source
}
fun D05Key.from_to(x: Long): Long? = if (this.source.contains(x)) (x - this.source) + this.dest else null
fun D05Key.to_from(x: Long): Long? = if (this.dest.contains(x)) (x - this.dest) + this.source else null
fun D05Key.safeprint() = println("${this.dest.start} ${this.source.start} ${this.dest.stop - this.dest.start}")

fun D05Key.from_to(key: D05Range): Pair<D05Range?, D05Range?> {
    val int = key.intersect(this.source)    // these get transformed
    val diff = key.difference(this.source)  // these pass through unchanged
    check(diff == null || key.intersect(diff) != null)
    return Pair(int?.offset(this.delta), diff)
}

fun String.to_Day05Key(): D05Key {
    val one = this.split(" ")
    check(one.size == 3)
    return D05Key(one[0].toLong(), one[1].toLong(), one[2].toLong())
}

class Day05Map(var items: List<D05Key>) {
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
    fun from_to(range: D05Range): List<D05Range> {
        val transformed = mutableListOf<D05Range>()
        var unchanged = range
        for (key in this.items) {
            val (intersection, difference) = key.from_to(unchanged)
            if (intersection != null) transformed.add(transformed.size, intersection)
            if (difference == null) return transformed
            unchanged = difference
        }
        // catch any remaining range that did not overlap anywhere
        transformed.add(transformed.size, unchanged)
        return transformed
    }
    fun from_to(ranges: List<D05Range>): List<D05Range> {
        // the transformation ranges don't overlap, so our life is slightly easier, hopefully
        return ranges.flatMap{this.from_to(it)}
    }
}
fun Day05Map.safeprint(): Unit {
    for (key in this.items)  key.safeprint()
}

fun makeDay05Map(lines: List<String>): Day05Map {
    val keys = mutableListOf<D05Key>()
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
fun followPath(seeds: D05Range, path: List<String>, maps: Map<Pair<String, String>, Day05Map>): List<D05Range> {
    var current = listOf(seeds)
    for (i in 0..<path.size-1) {
        val step = Pair(path[i], path[i+1])
        check(maps.containsKey(step))
        check(maps[step] != null)
//        println("${step.first} ${current.map{it.toMyString()}}, ")
        current = maps[step]!!.from_to(current)
    }
//    println("${path.last()} ${current.map{it.toMyString()}}")
    return current
}

fun main() {
    val path = listOf("seed", "soil", "fertilizer", "water", "light", "temperature", "humidity", "location")

    fun part1(input: List<String>): Long {
        val (seeds, maps) = readDay05Maps(input)
        return seeds.minOfOrNull {followPath(it, path, maps)} ?: -1
    }

    fun part2(input: List<String>): Long {
        val (sr, maps) = readDay05Maps(input)
        val seeds = (sr.indices step 2).map{D05Range(sr[it], sr[it]+sr[it+1]-1)}
        return seeds.flatMap { followPath(it, path, maps) }.minOfOrNull { it.start } ?: -1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readTestInput("Day05")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
