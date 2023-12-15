package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

object Day10 : AdventOfCodeDay<Int>(9, "Mirage Maintenance") {
    override fun calculatePartOne(input: List<String>): Int {
        return input
            .filter { it.isNotEmpty() }
            .sumOf {
                val numbers = it.split(" ").map { it.toInt() }
                val derivates = numbers.createDerivates()
                derivates.predict(numbers, true)
            }
    }

    override fun calculatePartTwo(input: List<String>): Int {
        return input
            .filter { it.isNotEmpty() }
            .sumOf {
                val numbers = it.split(" ").map { it.toInt() }
                val derivates = numbers.createDerivates()
                derivates.predict(numbers, false)
            }
    }

    private fun List<List<Int>>.predict(
        numbers: List<Int>,
        next: Boolean,
    ): Int {
        val prediction = reversed()
            .plus(element = numbers)
            .fold(0) { value, derivate ->
                if (next) {
                    value + derivate.last()
                } else {
                    derivate.first() - value
                }
            }
        return prediction
    }

    private fun List<Int>.createDerivates(): List<List<Int>> {
        return if (all { it == 0 }) {
            listOf()
        } else {
            val derivate = zipWithNext().map { (left, right) ->
                right - left
            }
            listOf(derivate) + derivate.createDerivates()
        }.filter { it.isNotEmpty() }
    }

    override val partOneExamples: Map<List<String>, Int> = mapOf(
        """
            0 3 6 9 12 15
            1 3 6 10 15 21
            10 13 16 21 30 45
        """.trimIndent().lines() to 114
    )

    override val partTwoExamples: Map<List<String>, Int> = mapOf(
        partOneExamples.keys.first() to 2
    )
}
