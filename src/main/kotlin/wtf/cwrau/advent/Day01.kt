package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

object Day01 : AdventOfCodeDay<Int>(1, "Trebuchet?!") {
    private val digits = listOf(
        "zero",
        "one",
        "two",
        "three",
        "four",
        "five",
        "six",
        "seven",
        "eight",
        "nine",
    )

    override fun calculatePartOne(input: List<String>): Int {
        return input
            .filter { it.isNotEmpty() }
            .map {
                it.filter { it.isDigit() }
                    .map { it.digitToInt() }
            }
            .sumOf { numbers ->
                "${numbers.first()}${numbers.last()}".toInt()
            }
    }

    override fun calculatePartTwo(input: List<String>): Int {
        return input
            .filter { it.isNotEmpty() }
            .map {
                it.parseDigits()
            }
            .sumOf { numbers ->
                "${numbers.first()}${numbers.last()}".toInt()
            }
    }

    private fun String.parseDigits(): List<Int> = indices
        .fold(listOf()) { numbers, index ->
            val subString = substring(index)
            val number = digits.withIndex().firstOrNull { (_, digit) ->
                subString.startsWith(digit)
            }?.index ?: subString.first().digitToIntOrNull()
            numbers + listOfNotNull(number)
        }

    override val partOneExamples: Map<List<String>, Int> = mapOf(
        """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
        """.trimIndent().lines() to 142
    )

    override val partTwoExamples: Map<List<String>, Int> = mapOf(
        """
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixteen
        """.trimIndent().lines() to 281
    )
}
