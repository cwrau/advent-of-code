package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

object Day07 : AdventOfCodeDay<Int>(7, "Camel Cards") {
    private val partOneCards = ("AKQJT" + (9 downTo 2).joinToString("")).toList()
    private val partTwoCards = ("AKQT" + (9 downTo 2).joinToString("") + 'J').toList()
    private val partOneHandTypes: List<(Map<Char, Int>) -> Boolean> = listOf(
        {
            it.size == 1
        },
        {
            it.values.max() == 4
        },
        {
            it.values.max() == 3 && it.values.min() == 2
        },
        {
            it.values.max() == 3
        },
        {
            it.count { it.value == 2 } == 2
        },
        {
            it.values.max() == 2
        },
        {
            it.size == 5
        }
    )
    private val partTwoHandTypes: List<(Map<Char, Int>) -> Boolean> = listOf(
        {
            when (it.getValue('J')) {
                5, 4 -> true
                3, 2, 1 -> it.size == 2
                else -> it.size == 1
            }
        },
        {
            when (it.getValue('J')) {
                5, 4, 3 -> true
                2 -> it.size == 3
                1 -> it.values.max() == 3
                else -> it.values.max() == 4
            }
        },
        {
            when (it.getValue('J')) {
                5, 4, 3 -> true
                2, 1 -> it.size == 3
                else -> it.values.max() == 3 && it.size == 2
            }
        },
        {
            when (it.getValue('J')) {
                5, 4, 3 -> true
                2, 1 -> it.size == 4
                else -> it.values.max() == 3
            }
        },
        {
            when (it.getValue('J')) {
                5, 4, 3, 2 -> true
                1 -> it.size < 5
                else -> it.size == 3
            }
        },
        {
            when (it.getValue('J')) {
                5, 4, 3, 2, 1 -> true
                else -> it.size == 4
            }
        },
        {
            true
        }
    )


    override fun calculatePartOne(input: List<String>): Int {
        val cleanedInput = input
            .filter { it.isNotEmpty() }
        return cleanedInput.calculateWinnings(partOneCards) { hand ->
            hand.calculateType(partOneHandTypes)
        }
    }

    override fun calculatePartTwo(input: List<String>): Int {
        val cleanedInput = input
            .filter { it.isNotEmpty() }
        return cleanedInput.calculateWinnings(partTwoCards) { hand ->
            hand.calculateType(
                if (hand.contains('J')) {
                    partTwoHandTypes
                } else {
                    partOneHandTypes
                }
            )
        }
    }

    private fun List<String>.calculateWinnings(
        cardOrder: List<Char>,
        calculateTypeStrength: (String) -> Int,
    ) = map { it.split(" ") }
        .associate { (hand, bid) ->
            hand to bid.toInt()
        }
        .mapKeys { (hand, _) ->
            hand to calculateTypeStrength(hand)
        }
        .toSortedMap { (handA, typeA), (handB, typeB) ->
            when {
                handA == handB -> 0
                typeA > typeB -> 1
                typeB > typeA -> -1
                else -> {
                    handA.zip(handB)
                        .first { (cardA, cardB) ->
                            cardA != cardB
                        }
                        .let { (cardA, cardB) ->
                            cardOrder.indexOf(cardA).compareTo(cardOrder.indexOf(cardB))
                        }
                }
            }
        }
        .toList()
        .mapIndexed { index, (_, bid) ->
            (size - index) to bid
        }
        .sumOf { (rank, bid) ->
            rank * bid
        }

    override val partOneExamples: Map<List<String>, Int> = mapOf(
        """
            32T3K 765
            T55J5 684
            KK677 28
            KTJJT 220
            QQQJA 483
        """.trimIndent().lines() to 6440
    )

    override val partTwoExamples: Map<List<String>, Int> = mapOf(
        partOneExamples.keys.first() to 5905
    )

    private fun String.calculateType(types: List<(Map<Char, Int>) -> Boolean>): Int {
        val cardCounts = this.groupBy { it }
            .mapValues {
                it.value.count()
            }
        return types.indexOfFirst {
            it(cardCounts)
        }
    }

}
