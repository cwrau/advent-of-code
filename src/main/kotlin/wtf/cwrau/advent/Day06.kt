package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

object Day06 : AdventOfCodeDay<Int>(6, "Wait for it") {
    private val whiteSpaceRegex = Regex("""\s+""")
    override fun calculatePartOne(input: List<String>): Int {
        val (totalTimes, recordDistances) = input.filter { it.isNotEmpty() }
            .map { it.split(":").last().trim().split(whiteSpaceRegex).map { it.toLong() } }
        return calculateNumberOfWaysToWin(
            totalTimes
                .zip(recordDistances)
                .toMap()
        )
    }

    private fun calculateNumberOfWaysToWin(races: Map<Long, Long>) = races
        .map { (totalTime, distance) ->
            (1..<totalTime)
                .map { buttonPressTime ->
                    (totalTime - buttonPressTime) * buttonPressTime
                }
                .count { it > distance }
        }
        .reduce(Int::times)

    override fun calculatePartTwo(input: List<String>): Int {
        val (totalTime, recordDistance) = input.filter { it.isNotEmpty() }
            .map { it.split(":").last().trim().split(whiteSpaceRegex).joinToString("").toLong() }
        return calculateNumberOfWaysToWin(
            mapOf(totalTime to recordDistance)
        )
    }

    override val partOneExamples: Map<List<String>, Int> = mapOf(
        """
            Time:      7  15   30
            Distance:  9  40  200
        """.trimIndent().lines() to 288
    )

    override val partTwoExamples: Map<List<String>, Int> = mapOf(
        partOneExamples.keys.first() to 71516
    )
}
