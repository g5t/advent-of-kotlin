fun main() {
    fun extractCard(line: String): Pair<List<Int>, List<Int>> {
        val one = line.split(":", "|")
        check(one.size == 3)
        return Pair(one[1].toIntList(), one[2].toIntList())
    }

    fun cardMatches(line: String): Int {
        val (winning, card) = extractCard(line)
        val matches = winning.toSet().intersect(card.toSet())
        return matches.size
    }

    fun countCards(lines: List<String>): Int {
        val counts = MutableList(lines.size){1}
        for ((index, line) in lines.withIndex())
            for (next in 1..cardMatches(line)) counts[index + next] += counts[index]
        return counts.sum()
    }

    fun part1(input: List<String>): Int {
        return input.sumOf{1 shl (cardMatches(it) - 1)}
    }

    fun part2(input: List<String>): Int {
        return countCards(input)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readTestInput("Day04")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
