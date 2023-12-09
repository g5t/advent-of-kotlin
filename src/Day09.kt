
fun main() {
    fun listDiff(list: List<Int>): List<Int> = list.zipWithNext{a, b -> b - a}

    fun oneLine(line: List<Int>): Int {
        val work = mutableListOf(line.toMutableList())
        var diff = listDiff(work.last())
        while (diff.any{it != 0}) {
            work.add(work.size, diff.toMutableList())
            diff = listDiff(work.last())
        }
        work.reversed().zipWithNext{a, b -> b.add(b.size, b.last() + a.last())}
        return work.first().last()
    }

    fun linesToInts(input: List<String>): List<List<Int>> {
        val out = mutableListOf<List<Int>>()
        for (x in input) out.add(out.size, x.split(" ").map{it.toInt()})
        return out
    }

    fun part1(input: List<String>): Int {
        val lines = linesToInts(input)
        return lines.sumOf { oneLine(it) }
    }

    fun part2(input: List<String>): Int {
        val lines = linesToInts(input)
        return lines.sumOf { oneLine(it.reversed()) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readTestInput("Day09")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
