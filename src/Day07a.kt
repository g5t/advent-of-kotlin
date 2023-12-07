fun main() {
    fun Char.decodeCard(): Int {
        if (this == 'A') return 12
        if (this == 'K') return 11
        if (this == 'Q') return 10
        if (this == 'J') return 9
        if (this == 'T') return 8
        return this.digitToInt() - 2
    }

    fun String.classifyHand(): HandType {
        val counts = mutableMapOf<Int, Int>()
        for (card in this.map{it.decodeCard()}) counts[card] = 1 + (counts[card] ?: 0)
        val hand = counts.values.sortedDescending()
        if (hand[0] == 5) return HandType.FIVEOFAKIND
        if (hand[0] == 4) return HandType.FOUROFAKIND
        if (hand[0] == 3 && hand[1] == 2) return HandType.FULLHOUSE
        if (hand[0] == 3) return HandType.THREEOFAKIND
        if (hand[0] == 2 && hand[1] == 2) return HandType.TWOPAIR
        if (hand[0] == 2) return HandType.ONEPAIR
        return HandType.HIGHCARD
    }

    fun intPow(base: Int, power: Int): Int {
        var res = 1
        for (i in 0..<power) res *= base
        return res
    }

    class D07Hand(val repr: String, val type: HandType, val hand: Int, val based: String) : Comparable<D07Hand> {
        constructor(encoded: String) : this(
            encoded,
            encoded.classifyHand(),
            encoded.map { it.decodeCard() }.reversed().mapIndexed { i, it -> intPow(13, i) * it }.sum(),
            encoded.map{"%1x".format(it.decodeCard())}.joinToString(separator="")
        )
        override operator fun compareTo(other: D07Hand): Int {
            if (this.type == other.type) return this.hand.compareTo(other.hand)
            return this.type.strength.compareTo(other.type.strength)
        }
        override fun toString(): String {
            return "$repr ($type, $hand == ${based}_13)"
        }
    }

    class D07Bid(val hand: D07Hand, val bid: Long): Comparable<D07Bid> {
        override operator fun compareTo(other: D07Bid): Int {
            return this.hand.compareTo(other.hand)
        }
        override fun toString(): String {
            return "$hand :$bid"
        }
    }


    fun decodeBid(line: String): D07Bid {
        val pair = line.split(" ")
        check(pair.size == 2)
        return D07Bid(D07Hand(pair[0]), pair[1].toLong())
    }


    fun part1(input: List<String>): Long {
        val hands = input.map{decodeBid(it)}.sorted()
        hands.map{println(it)}
        return hands.mapIndexed{i, it -> (i + 1) * it.bid}.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readTestInput("Day07")
    check(part1(testInput) == 6440L)
    println()

    val testAInput = readTestInput("Day07a")
    check(part1(testAInput) == 650L)

    val input = readInput("Day07")
    part1(input).println()
}
