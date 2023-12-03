fun Char.isGear(): Boolean = this == '*'
fun Char.isPart(): Boolean = this != '.' && !this.isDigit()
fun String.extractNumber(from: Int, to: Int) = this.substring(from, to).toInt()

fun identifyNumbers(line: String): List<Triple<Int, Int, Int>> {
    var from: Int? = null
    val numbers = mutableListOf<Triple<Int, Int, Int>>()
    for (i in line.indices) {
        if (line[i].isDigit()) from = from ?: i
        else if (from != null) {
            numbers.add(numbers.size, Triple(from, i, line.extractNumber(from, i)))
            from = null
        }
    }
    if (from != null) numbers.add(numbers.size, Triple(from, line.length, line.extractNumber(from, line.length)))
    return numbers
}

fun identifyParts(line: String, method: (input: Char) -> Boolean): List<Int> {
    val parts = mutableListOf<Int>()
    for (i in line.indices) if (method(line[i])) parts.add(parts.size, i)
    return parts
}

fun identifyPartNumbers(lines: List<String>): List<Int> {
    val numbers = mutableListOf<Int>()
    var last_parts: List<Int>? = null
    var this_parts: List<Int>? = null
    var next_parts: List<Int>? = identifyParts(lines[0], Char::isPart)
    for (i in lines.indices){
        this_parts = next_parts
        next_parts = if (i + 1 < lines.size) identifyParts(lines[i + 1], Char::isPart) else null
        val this_numbers = identifyNumbers(lines[i])
        for (number in this_numbers){
            var adjacent = false
            for (parts in arrayOf(last_parts, this_parts, next_parts)) if (parts != null) for (part in parts) {
                if (number.first - 1 <= part && number.second >= part) adjacent = true
            }
            if (adjacent) numbers.add(numbers.size, number.third)
        }
        last_parts = this_parts
    }
    return numbers
}

fun identifyGears(lines: List<String>): Map<Pair<Int, Int>, Int> {
    val gears = mutableMapOf<Pair<Int, Int>, Int>()
    var last_numbers: List<Triple<Int, Int, Int>>? = null
    var next_numbers: List<Triple<Int, Int, Int>>? = identifyNumbers(lines[0])
    var this_numbers: List<Triple<Int, Int, Int>>?
    for (i in lines.indices){
        this_numbers = next_numbers
        next_numbers = if (i+1 < lines.size) identifyNumbers(lines[i+1]) else null
        // do stuff
        val gear_parts = identifyParts(lines[i], Char::isGear)
        for (gear in gear_parts){
            val adjacent_numbers = mutableListOf<Triple<Int, Int, Int>>()
            for (numbers in arrayOf(last_numbers, next_numbers, this_numbers)) if (numbers != null) {
                for (number in numbers) if (number.first - 1 <= gear && number.second >= gear) {
                    adjacent_numbers.add(adjacent_numbers.size, number)
                }
            }
            if (adjacent_numbers.size == 2) {
                // this _is_ a valid gear
                gears[Pair(i, gear)] = adjacent_numbers[0].third * adjacent_numbers[1].third
            }
        }
        // cycle for next loop
        last_numbers = this_numbers
    }
    return gears
}

fun main() {
    fun part1(input: List<String>): Int {
        return identifyPartNumbers(input).sum()
    }

    fun part2(input: List<String>): Int {
        return identifyGears(input).values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readTestInput("Day03a")
    val input = readInput("Day03")
    check(part1(testInput) == 4361 + 123 + 456)
    part1(input).println()

    check(part2(testInput) == 467835 + 123*456)
    part2(input).println()
}
