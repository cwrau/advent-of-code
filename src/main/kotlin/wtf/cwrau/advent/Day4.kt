package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

class Day4 : AdventOfCodeDay<Int> {
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
        return input.parseScoreCards()
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
        val cards = input.parseScoreCards()
            .fold(input.parseScoreCards().map { it to 1 }) { currentCards, currentCard ->
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
}
