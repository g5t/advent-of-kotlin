import kotlin.math.max

fun decodeGroup(group: String): Map<String, Int> {
    val map = mutableMapOf<String, Int>()
    for (pair in group.split(',')) {
        val split = pair.trim(' ').split(' ', limit=2)
        map[split.last()] = split.first().toInt()
    }
    return map
}

fun combineGroups(groups: List<Map<String, Int>>): Map<String, Int> {
    val map = mutableMapOf<String, Int>()
    for (group in groups) {
        for (key in group.keys) {
            map[key] = max(group[key] ?: 0, map[key] ?: 0)
        }
    }
    return map
}

fun parseGame(gameString: String): Pair<Int, Map<String, Int>> {
    val (game, draws) = gameString.split(':', limit=2)
    val gameIndex = game.split(' ')
    val index: Int = gameIndex.last().toInt()
    val groups = draws.split(';').map{decodeGroup(it)}
    return Pair(index, combineGroups(groups))
}

fun validateGroup(group: Map<String, Int>, biggest: Map<String, Int>): Boolean {
    for (key in group.keys) if ((group[key] ?: 0) > (biggest[key] ?: 0)) return false
    return true
}

fun groupProduct(group: Map<String, Int>): Int {
    var product = 1
    for (value in group.values) product *= value
    return product
}

fun main() {
    fun part1(input: List<String>): Int {
        var total: Int = 0
        val biggest = mapOf("green" to 13, "red" to 12, "blue" to 14)
        for (pair in input.map{parseGame(it)}) if (validateGroup(pair.second, biggest)) total += pair.first
        return total
    }

    fun part2(input: List<String>): Int {
        var total = 0
        for (pair in input.map{parseGame(it)}) total += groupProduct(pair.second)
        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readTestInput("Day02a")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
