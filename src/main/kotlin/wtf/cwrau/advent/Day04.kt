package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

object Day04 : AdventOfCodeDay<Int>(4, "Scratchcards") {
    private val whiteSpaceRegex = Regex("""\s+""")

    data class ScoreCard(val id: Int, val winningNumbers: List<Int>, val myNumbers: List<Int>) {
        fun calculatePoints(): Int {
            val winningNumbers = calculateNumberOfWinningCards()
            val points = winningNumbers.downTo(1)
                .fold(0) { points, _ ->
                    when (points) {
                        0 -> 1
                        else -> points * 2
                    }
                }
            return points
        }

        fun calculateNumberOfWinningCards() = myNumbers.intersect(winningNumbers.toSet()).count()
    }

    override fun calculatePartOne(input: List<String>): Int {
        return input
            .filter { it.isNotEmpty() }
            .parseScoreCards()
            .sumOf { it.calculatePoints() }
    }

    private fun List<String>.parseScoreCards() = map { card ->
        val (id, details) = card
            .split(":")
            .map { it.trim() }
        val (setNumbersString, myNumbersString) = details
            .split("|")
            .map { it.trim() }
        val setNumbers = setNumbersString.split(whiteSpaceRegex)
            .map { it.toInt() }
        val myNumbers = myNumbersString.split(whiteSpaceRegex)
            .map { it.toInt() }
        val scoreCard = ScoreCard(id.removePrefix("Card").trim().toInt(), setNumbers, myNumbers)
        scoreCard
    }

    override fun calculatePartTwo(input: List<String>): Int {
        val cleanedInput = input.filter { it.isNotEmpty() }
        val cards = cleanedInput
            .parseScoreCards()
            .fold(cleanedInput.parseScoreCards().map { it to 1 }) { currentCards, currentCard ->
                val numberOfWinningCards = currentCard.calculateNumberOfWinningCards()
                val currentCount = currentCards.single { it.first.id == currentCard.id }.second
                currentCards
                    .map { (card, count) ->
                        if (numberOfWinningCards > 0 && card.id in (currentCard.id + 1)..(currentCard.id + numberOfWinningCards)) {
                            card to count + currentCount
                        } else {
                            card to count
                        }
                    }
            }
        return cards
            .sumOf { it.second }
    }

    override val partOneExamples: Map<List<String>, Int> = mapOf(
        """
                Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
                Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
                Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
                Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
                Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
                Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
            """.trimIndent().lines() to 13
    )

    override val partTwoExamples: Map<List<String>, Int> = mapOf(
        partOneExamples.keys.first() to 30
    )
}
