
fun alt_extract_integer(look: Map<ByteArray, Int>, line: ByteArray): Int? {
    for (key in look.keys) if (line.startsWith(key)) return look[key]  // overlap of words allowed
    return null
}

fun alt_line_number(look: Map<ByteArray, Int>, line: ByteArray): Int {
    var value: Int? = null
    var offset: Int = 0
    while (offset < line.size && value == null) {
        value = alt_extract_integer(look, line.sliceArray(IntRange(offset, line.size-1)))
        offset += 1
    }
    return value ?: 0
}

fun fwd_bkd_line_number(fwd: Map<ByteArray, Int>, bkd: Map<ByteArray, Int>, line: ByteArray): Int {
    return 10 * alt_line_number(fwd, line) + alt_line_number(bkd, line.reversedArray())
}

fun fwd_bkd_lines_total(str_map: Map<String, Int>, lines: List<String>): Int {
    val fwd = mutableMapOf<ByteArray, Int>()
    val bkd = mutableMapOf<ByteArray, Int>()
    for (pair in str_map) {
        fwd[pair.key.toByteArray()] = pair.value
        bkd[pair.key.toByteArray().reversedArray()] = pair.value
    }
    var total = 0
    for (line in lines) total += fwd_bkd_line_number(fwd, bkd, line.toByteArray())
    return total
}

fun alt_lines_total(look: Map<String, Int>, lines: List<String>): Int {
    return fwd_bkd_lines_total(look, lines)
}

fun main() {
    fun part1(input: List<String>): Int {
        val map = mapOf("1" to 1, "2" to 2, "3" to 3, "4" to 4, "5" to 5, "6" to 6, "7" to 7, "8" to 8, "9" to 9)
        return alt_lines_total(map, input)
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
