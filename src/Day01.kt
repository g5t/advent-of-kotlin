
fun extract_integer(look: Map<String, Int>, line: String): Pair<Int?, Int> {
    for (key in look.keys){
        if (line.startsWith(key)) {
            return look[key] to 1  // overlap of words allowed
            // return look[key] to key.length // no overlap of words
        }
    }
    return null to 1
}

fun extract_integers(look: Map<String, Int>, line: String): List<Int> {
    val numbers = mutableListOf<Int>()
    var from = 0
    while (from <= line.length) {
        val (number, skip) = extract_integer(look, line.substring(from))
        from += skip
        if (number != null) numbers.add(numbers.size, number)
        //println("$line -> ${line.substring(from-skip)} -> $numbers")
    }
    return numbers
}

fun line_number(look: Map<String, Int>, line: String): Int {
    val numbers = extract_integers(look, line)
    return if (numbers.isEmpty()) 0 else numbers[0]*10 + numbers[numbers.size-1]
}

fun lines_total(look: Map<String, Int>, lines: List<String>): Int {
    var total = 0
    for (line in lines) {
        total += line_number(look, line)
    }
    return total
}

fun main() {
    fun part1(input: List<String>): Int {
        val map = mapOf("1" to 1, "2" to 2, "3" to 3, "4" to 4, "5" to 5, "6" to 6, "7" to 7, "8" to 8, "9" to 9)
        return lines_total(map, input)
    }

    fun part2(input: List<String>): Int {
        val map = mapOf("1" to 1, "2" to 2, "3" to 3, "4" to 4, "5" to 5, "6" to 6, "7" to 7, "8" to 8, "9" to 9,
            "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7,
            "eight" to 8, "nine" to 9
        )
        return lines_total(map, input)
    }

    // test if implementation meets criteria from the description, like:
    val testAInput = readTestInput("Day01a")
    check(part1(testAInput) == 142)

    val testBInput = readTestInput("Day01b")
    check(part2(testBInput) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
