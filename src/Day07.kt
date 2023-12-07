enum class HandType(val strength: Int, val printableName: String) {
    HIGHCARD(1, "High card"),
    ONEPAIR(2, "One pair"),
    TWOPAIR(3, "Two pair"),
    THREEOFAKIND(4, "Three of a kind"),
    FULLHOUSE(5, "Full house"),
    FOUROFAKIND(6, "Four of a kind"),
    FIVEOFAKIND(7, "Five of a kind"),
}
