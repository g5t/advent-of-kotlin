import kotlin.math.sqrt
import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    fun solveQuadraticInequality(n: Long, m: Long): Pair<Long, Long> {
        // solve for the limiting (integer) x for
        //   (n - x) * x > m  ==  x^2 - n * x + m < 0
        val p = sqrt((n * n  - 4 * m).toDouble())
        val lowerExact = (n - p) / 2
        val upperExact = (n + p) / 2
        // the next larger integer should be chosen from the lower limit
        var lower = ceil(lowerExact).toLong()
        // the next smaller integer should be chosen from the upper limit
        var upper = floor(upperExact).toLong()
        // ensure the exact results are not integers, since they are not valid solutions
        if (lowerExact == lower.toDouble()) lower += 1
        if (upperExact == upper.toDouble()) upper -= 1
        // these represent the range of valid integer solutions
        return Pair(lower, upper)
    }
    fun loadTimeDistance(input: List<String>): List<Pair<Long, Long>> {
        check(input.size > 1)
        check(input[0].startsWith("Time:"))
        check(input[1].startsWith("Distance:"))
        val times = input[0].split(':')[1].toLongList()
        val distance = input[1].split(':')[1].toLongList()
        return times.zip(distance).toList()
    }
    fun countSolutions(tand: Pair<Long, Long>): Long {
        val (shortest, longest) = solveQuadraticInequality(tand.first, tand.second)
        return longest - shortest + 1
    }

    fun part1(input: List<String>): Long {
        return loadTimeDistance(input).map{countSolutions(it)}.prod()
    }

    fun part2(input: List<String>): Long {
        val one = loadTimeDistance(input.map{it.replace(" ", "")})
        check(one.size == 1)
        return countSolutions(one[0])
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readTestInput("Day06")
    check(part1(testInput) == 288L)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
